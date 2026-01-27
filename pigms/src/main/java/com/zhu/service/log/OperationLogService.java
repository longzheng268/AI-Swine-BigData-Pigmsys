//对操作日志的增删改查
package com.zhu.service.log;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhu.mapper.OperationLogMapper;
import com.zhu.pojo.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 操作日志 Service
 */
@Service
public class OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 插入日志
     */
    public int insert(OperationLog operationLog) {
        return operationLogMapper.insert(operationLog);
    }

    /**
     * 删除日志
     */
    public int deleteByPrimaryKey(Long id) {
        return operationLogMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询所有日志
     */
    public List<OperationLog> selectAll() {
        return operationLogMapper.selectAll();
    }

    /**
     * 条件查询
     */
    public List<OperationLog> selectByCondition(String username, String operation, Date startTime, Date endTime) {
        return operationLogMapper.selectByCondition(username, operation, startTime, endTime);
    }

    /**
     * 分页查询
     */
    public PageInfo<OperationLog> selectByPage(Integer pageNum, Integer pageSize, String username, 
                                                String operation, Date startTime, Date endTime) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = operationLogMapper.selectByCondition(username, operation, startTime, endTime);
        return new PageInfo<>(list);
    }

    /**
     * 删除指定日期之前的日志
     */
    public int deleteByCreateTime(Date beforeDate) {
        return operationLogMapper.deleteByCreateTime(beforeDate);
    }
}



