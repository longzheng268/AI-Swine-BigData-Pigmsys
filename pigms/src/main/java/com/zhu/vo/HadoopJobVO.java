package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hadoop 任务视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HadoopJobVO {
    
    /**
     * 任务名称
     */
    private String jobName;
    
    /**
     * 任务类型（pig_analysis, environment_analysis）
     */
    private String jobType;
    
    /**
     * 输入文件名
     */
    private String inputFile;
    
    /**
     * 输出路径
     */
    private String outputPath;
    
    /**
     * 任务状态
     */
    private Boolean success;
    
    /**
     * 任务结果
     */
    private Object result;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 创建时间
     */
    private Long createTime;
}

