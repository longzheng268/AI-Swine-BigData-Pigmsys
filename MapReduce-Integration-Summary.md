# MapReduce Integration - Summary of Changes

## Problem Statement

The Hadoop MapReduce jobs were not being submitted to YARN ResourceManager (port 8088), making it impossible to track job execution. The dashboard could display data, but it was using fallback/local data instead of actual MapReduce analysis results.

## Root Causes Identified

1. **Incorrect Input Path Handling**: The `runPigDataAnalysis()` and `runEnvironmentDataAnalysis()` methods were incorrectly prepending the `inputPath` to file paths that were already complete HDFS paths.

2. **CSV Format Mismatch**: The MapReduce Mapper classes expected different CSV column positions than what `DashboardService` was generating.

3. **Insufficient Error Logging**: Exceptions were caught but not logged with enough detail to diagnose issues.

4. **Log Noise**: DEBUG-level database logging was flooding the console, making it impossible to see critical MapReduce and system messages.

5. **Missing Job Tracking**: No Job ID or Tracking URL was being logged, making it impossible to correlate jobs with YARN UI.

## Changes Made

### 1. HadoopConfig.java
- **Enhanced Logging**: Added detailed configuration logging on startup
- **Configuration Validation**: Added warning when `mapreduce.framework.name` is not set to `yarn`
- **Job Creation Logging**: Logs all critical parameters when creating MapReduce jobs

### 2. HadoopService.java
- **Fixed Input Path Bug**: Removed incorrect path prepending
- **Input File Validation**: Verify HDFS input file exists before job submission
- **Job Tracking**: Capture and log Job ID and Tracking URL
- **Execution Timing**: Track and log job execution duration
- **Enhanced Error Messages**: Include exception type, message, and full stack trace
- **Structured Logging**: Use separator lines for easy log parsing

### 3. DashboardService.java
- **Detailed Success Logging**: Log Job ID, Tracking URL, and duration on success
- **Detailed Failure Logging**: Log all error details including stack traces
- **Clear Fallback Messages**: Explicitly log when falling back to local database

### 4. PigDataAnalysisMapper.java
- **Fixed Column Index**: Changed from index 1 to index 3 to match actual CSV format
- **Added Validation**: Check for empty pig type values
- **Better Error Handling**: Print full stack trace on errors

### 5. EnvironmentDataAnalysisMapper.java
- **Updated Field Indices**: Match the actual CSV format from DashboardService
- **Better Error Handling**: Print full stack trace on errors

### 6. log4j.properties (Configuration)
- **Reduced Root Level**: Changed from DEBUG to INFO
- **Database Logging**: Reduced from DEBUG to INFO
- **Result Set Logging**: Set to WARN to eliminate query result spam
- **Druid Pool**: Set to INFO to reduce connection noise
- **Spring Framework**: Set to WARN to reduce framework logging
- **Added Timestamp**: Include date/time in log format
- **Kept Hadoop Logs**: Maintained INFO level for Hadoop services to capture important events

## Expected Behavior After Changes

### On Application Startup
You should see:
```
========================================
Hadoop 配置初始化完成
NameNode: hdfs://localhost:9000
ResourceManager: localhost:8032
MapReduce Framework: yarn
HDFS User: wyb
========================================
```

### When MapReduce Job is Submitted
You should see:
```
========================================
开始提交 MapReduce 任务: PigDataAnalysis_1234567890
输入文件: /pig-system/dashboard/pig_data_1234567890.csv
========================================
创建 MapReduce Job: PigDataAnalysis_1234567890
Job 配置验证:
  - fs.defaultFS: hdfs://localhost:9000
  - yarn.resourcemanager.address: localhost:8032
  - mapreduce.framework.name: yarn
========================================
```

### On Successful Job Completion
You should see:
```
========================================
MapReduce 任务详情:
  - Job Name: PigDataAnalysis_1234567890
  - Job ID: job_1234567890_0001
  - Tracking URL: http://localhost:8088/proxy/application_1234567890_0001/
  - 执行状态: 成功
  - 耗时: 15234 ms
========================================
```

### On Job Failure
You should see:
```
========================================
猪数据 MapReduce 任务执行失败
  - 错误信息: Connection refused
  - 异常类型: java.net.ConnectException
  - Job ID: job_1234567890_0001
  - Tracking URL: http://localhost:8088/...
  - 堆栈跟踪:
    [full stack trace here]
========================================
降级使用本地数据库统计
```

## Verification Steps

1. **Check Application Logs**: Look for the log messages described above
2. **Check YARN UI**: Visit http://localhost:8088 to see if jobs appear
3. **Check Job ID**: The Job ID in logs should match the application in YARN UI
4. **Click Tracking URL**: The URL in logs should open the job details in YARN
5. **Verify Job Status**: Jobs should show as SUCCEEDED or FAILED with details

## Configuration Requirements

Ensure your `application.yml` has:
```yaml
hadoop:
  mapreduce:
    framework: yarn  # MUST be "yarn", not "local"
  resourcemanager: localhost:8032  # YARN ResourceManager RPC address
  user: wyb  # HDFS user with proper permissions
```

## Troubleshooting

### If Jobs Don't Appear in YARN UI

1. **Check Framework Setting**: Look for warning in logs about framework not being `yarn`
2. **Check YARN is Running**: Run `jps | grep ResourceManager`
3. **Check Network**: Ensure ResourceManager is accessible at configured address
4. **Check Logs for Errors**: Look for connection errors or exceptions in job submission

### If Jobs Fail

1. **Check Error Logs**: New logging will show detailed error information
2. **Check Job Logs in YARN**: Use the Tracking URL or `yarn logs` command
3. **Verify HDFS Access**: Ensure input files exist and are readable
4. **Check Permissions**: Ensure the configured user has HDFS write access

### To Enable More Debugging

Edit `log4j.properties`:
```properties
log4j.logger.com.zhu.service.hadoop.HadoopService=DEBUG
log4j.logger.org.apache.hadoop=DEBUG
```

## Files Changed

- `pigms/src/main/java/com/zhu/config/HadoopConfig.java`
- `pigms/src/main/java/com/zhu/service/hadoop/HadoopService.java`
- `pigms/src/main/java/com/zhu/service/dashboard/DashboardService.java`
- `pigms/src/main/java/com/zhu/hadoop/PigDataAnalysisMapper.java`
- `pigms/src/main/java/com/zhu/hadoop/EnvironmentDataAnalysisMapper.java`
- `pigms/src/main/resources/log4j.properties`

## Testing

To test the changes:

1. Start the application
2. Check startup logs for Hadoop configuration
3. Trigger the dashboard or call the Hadoop analysis API
4. Observe logs for job submission and tracking information
5. Check YARN UI at http://localhost:8088
6. Verify job appears with correct Job ID
7. Check job completes successfully or review error details

## Benefits

- **Visibility**: Can now see exactly what's happening with MapReduce jobs
- **Debugging**: Detailed error information makes troubleshooting easy
- **Monitoring**: Job IDs and URLs allow correlation with YARN UI
- **Reliability**: Input validation prevents jobs from failing due to missing files
- **Performance**: Execution timing helps identify slow jobs
- **Cleanliness**: Reduced log noise makes important messages stand out
