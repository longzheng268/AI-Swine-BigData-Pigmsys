package com.zhu.hadoop;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 环境数据分析 - Mapper
 * 用于分析环境监测数据的统计信息
 * 
 * 输入格式：日期,温度,湿度,氨气浓度,二氧化碳浓度
 * 例如：2024-01-01,25.5,65.0,10.2,450.5
 */
public class EnvironmentDataAnalysisMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    
    private Text outputKey = new Text();
    private DoubleWritable outputValue = new DoubleWritable();
    
    @Override
    protected void map(LongWritable key, Text value, Context context) 
            throws IOException, InterruptedException {
        
        // 跳过第一行表头
        if (key.get() == 0) {
            return;
        }
        
        try {
            // 解析输入数据（CSV格式）
            String line = value.toString();
            String[] fields = line.split(",");
            
            if (fields.length >= 5) {
                // 提取各项环境数据
                double temperature = Double.parseDouble(fields[1].trim());
                double humidity = Double.parseDouble(fields[2].trim());
                double ammonia = Double.parseDouble(fields[3].trim());
                double co2 = Double.parseDouble(fields[4].trim());
                
                // 输出各项指标的值
                outputKey.set("temperature");
                outputValue.set(temperature);
                context.write(outputKey, outputValue);
                
                outputKey.set("humidity");
                outputValue.set(humidity);
                context.write(outputKey, outputValue);
                
                outputKey.set("ammonia");
                outputValue.set(ammonia);
                context.write(outputKey, outputValue);
                
                outputKey.set("co2");
                outputValue.set(co2);
                context.write(outputKey, outputValue);
            }
        } catch (Exception e) {
            // 记录错误但继续处理其他数据
            System.err.println("Error parsing line: " + value.toString());
        }
    }
}

