package com.zhu.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 猪场数据分析 - Reducer
 * 汇总相同类型猪的数量
 * 
 * 输出格式：猪类型,数量
 */
public class PigDataAnalysisReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    
    private IntWritable result = new IntWritable();
    
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) 
            throws IOException, InterruptedException {
        
        // 统计每种类型猪的总数
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        
        result.set(sum);
        
        // 输出 <猪类型, 总数量>
        context.write(key, result);
    }
}

