package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HDFS 文件视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HdfsFileVO {
    
    /**
     * 文件路径
     */
    private String path;
    
    /**
     * 文件名
     */
    private String name;
    
    /**
     * 文件大小（字节）
     */
    private Long size;
    
    /**
     * 是否为目录
     */
    private Boolean isDirectory;
    
    /**
     * 修改时间
     */
    private Long modificationTime;
    
    /**
     * 所有者
     */
    private String owner;
    
    /**
     * 权限
     */
    private String permission;
}

