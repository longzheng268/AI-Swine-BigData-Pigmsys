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
 * 输入格式：监测点,采集时间,温度,湿度,CO2浓度,氨气浓度,光照强度
 * 例如：监测点A,2024-01-01 10:30:00,25.5,65.0,450.5,10.2,500.0
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
            
            // 格式：监测点(0),采集时间(1),温度(2),湿度(3),CO2浓度(4),氨气浓度(5),光照强度(6)
            if (fields.length >= 7) {
                // 提取各项环境数据
                double temperature = Double.parseDouble(fields[2].trim());
                double humidity = Double.parseDouble(fields[3].trim());
                double co2 = Double.parseDouble(fields[4].trim());
                double ammonia = Double.parseDouble(fields[5].trim());
                
                // 输出各项指标的值
                outputKey.set("temperature");
                outputValue.set(temperature);
                context.write(outputKey, outputValue);
                
                outputKey.set("humidity");
                outputValue.set(humidity);
                context.write(outputKey, outputValue);
                
                outputKey.set("co2");
                outputValue.set(co2);
                context.write(outputKey, outputValue);
                
                outputKey.set("ammonia");
                outputValue.set(ammonia);
                context.write(outputKey, outputValue);
            }
        } catch (Exception e) {
            // 记录错误但继续处理其他数据
            System.err.println("Error parsing line: " + value.toString());
            e.printStackTrace();
        }
    }
}

