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
 * 输入格式：猪ID,猪编号,猪名称,猪类型,年龄,性别,出生日期,产地
 * 例如：1,PIG001,小猪,二元杂交猪,2,公,2023-01-15,本地
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
            
            // 猪类型在第4列（索引3）
            if (fields.length >= 4) {
                // 提取猪类型
                String type = fields[3].trim();
                if (!type.isEmpty()) {
                    pigType.set(type);
                    
                    // 输出 <猪类型, 1>
                    context.write(pigType, one);
                }
            }
        } catch (Exception e) {
            // 记录错误但继续处理其他数据
            System.err.println("Error parsing line: " + value.toString());
            e.printStackTrace();
        }
    }
}

