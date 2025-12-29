package com.zhu.hadoop;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 环境数据分析 - Reducer
 * 计算各项环境指标的平均值、最大值、最小值
 * 
 * 输出格式：指标名称,平均值,最大值,最小值,总数
 */
public class EnvironmentDataAnalysisReducer extends Reducer<Text, DoubleWritable, Text, Text> {
    
    private Text result = new Text();
    private DecimalFormat df = new DecimalFormat("#.##");
    
    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) 
            throws IOException, InterruptedException {
        
        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int count = 0;
        
        // 统计各项指标
        for (DoubleWritable val : values) {
            double value = val.get();
            sum += value;
            count++;
            
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }
        
        // 计算平均值
        double average = sum / count;
        
        // 格式化输出：平均值,最大值,最小值,数据条数
        String output = String.format("%s,%s,%s,%d", 
            df.format(average), df.format(max), df.format(min), count);
        
        result.set(output);
        
        // 输出 <指标名称, "平均值,最大值,最小值,数据条数">
        context.write(key, result);
    }
}

