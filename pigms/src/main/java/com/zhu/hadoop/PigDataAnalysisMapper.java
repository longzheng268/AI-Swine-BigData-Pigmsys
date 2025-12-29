package com.zhu.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 猪场数据分析 - Mapper
 * 用于统计不同猪类型的数量和相关数据
 * 
 * 输入格式：猪ID,猪类型,状态,重量,价格
 * 例如：1,二元杂交猪,健康,100.5,1500
 */
public class PigDataAnalysisMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    
    private final static IntWritable one = new IntWritable(1);
    private Text pigType = new Text();
    
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
            
            if (fields.length >= 2) {
                // 提取猪类型
                String type = fields[1].trim();
                pigType.set(type);
                
                // 输出 <猪类型, 1>
                context.write(pigType, one);
            }
        } catch (Exception e) {
            // 记录错误但继续处理其他数据
            System.err.println("Error parsing line: " + value.toString());
        }
    }
}

