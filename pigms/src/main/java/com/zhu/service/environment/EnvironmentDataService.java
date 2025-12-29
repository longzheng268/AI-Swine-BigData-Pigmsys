package com.zhu.service.environment;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhu.mapper.EnvironmentDataMapper;
import com.zhu.mapper.EnvironmentStandardMapper;
import com.zhu.pojo.EnvironmentData;
import com.zhu.pojo.EnvironmentStandard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 环境监测数据 Service
 */
@Service
public class EnvironmentDataService {

    @Autowired
    private EnvironmentDataMapper environmentDataMapper;

    @Autowired
    private EnvironmentStandardMapper environmentStandardMapper;

    /**
     * 插入单条数据
     */
    public int insert(EnvironmentData environmentData) {
        // 设置创建时间
        if (environmentData.getCreateTime() == null) {
            environmentData.setCreateTime(new Date());
        }
        // 检查数据是否异常
        checkAbnormal(environmentData);
        return environmentDataMapper.insert(environmentData);
    }

    /**
     * 批量插入
     */
    public int insertBatch(List<EnvironmentData> list) {
        for (EnvironmentData data : list) {
            if (data.getCreateTime() == null) {
                data.setCreateTime(new Date());
            }
            checkAbnormal(data);
        }
        return environmentDataMapper.insertBatch(list);
    }

    /**
     * 删除
     */
    public int deleteByPrimaryKey(Long id) {
        return environmentDataMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新
     */
    public int updateByPrimaryKey(EnvironmentData environmentData) {
        checkAbnormal(environmentData);
        return environmentDataMapper.updateByPrimaryKey(environmentData);
    }

    /**
     * 查询单条
     */
    public EnvironmentData selectByPrimaryKey(Long id) {
        return environmentDataMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询所有
     */
    public List<EnvironmentData> selectAll() {
        return environmentDataMapper.selectAll();
    }

    /**
     * 条件查询
     */
    public List<EnvironmentData> selectByCondition(String monitorPoint, Date startTime, Date endTime, Integer isAbnormal) {
        return environmentDataMapper.selectByCondition(monitorPoint, startTime, endTime, isAbnormal);
    }

    /**
     * 分页查询
     */
    public PageInfo<EnvironmentData> selectByPage(Integer pageNum, Integer pageSize, String monitorPoint, 
                                                   Date startTime, Date endTime, Integer isAbnormal) {
        PageHelper.startPage(pageNum, pageSize);
        List<EnvironmentData> list = environmentDataMapper.selectByCondition(monitorPoint, startTime, endTime, isAbnormal);
        return new PageInfo<>(list);
    }

    /**
     * 查询最新的监测数据
     */
    public List<EnvironmentData> selectLatestByMonitorPoint(Integer limit) {
        return environmentDataMapper.selectLatestByMonitorPoint(limit);
    }

    /**
     * 统计异常数据数量
     */
    public int countAbnormal(Date startTime, Date endTime) {
        return environmentDataMapper.countAbnormal(startTime, endTime);
    }

    /**
     * 检查数据是否异常
     */
    private void checkAbnormal(EnvironmentData data) {
        try {
            StringBuilder abnormalReason = new StringBuilder();
            boolean isAbnormal = false;

            // 检查温度
            if (data.getTemperature() != null) {
                EnvironmentStandard standard = environmentStandardMapper.selectByParameterName("温度");
                if (standard != null && !isInRange(data.getTemperature(), standard.getMinValue(), standard.getMaxValue())) {
                    abnormalReason.append("温度超标;");
                    isAbnormal = true;
                }
            }

            // 检查湿度
            if (data.getHumidity() != null) {
                EnvironmentStandard standard = environmentStandardMapper.selectByParameterName("湿度");
                if (standard != null && !isInRange(data.getHumidity(), standard.getMinValue(), standard.getMaxValue())) {
                    abnormalReason.append("湿度超标;");
                    isAbnormal = true;
                }
            }

            // 检查CO2浓度
            if (data.getCo2Concentration() != null) {
                EnvironmentStandard standard = environmentStandardMapper.selectByParameterName("CO₂浓度");
                if (standard != null && !isInRange(data.getCo2Concentration(), standard.getMinValue(), standard.getMaxValue())) {
                    abnormalReason.append("CO₂浓度超标;");
                    isAbnormal = true;
                }
            }

            // 检查氨气浓度
            if (data.getNh3Concentration() != null) {
                EnvironmentStandard standard = environmentStandardMapper.selectByParameterName("氨气浓度");
                if (standard != null && !isInRange(data.getNh3Concentration(), standard.getMinValue(), standard.getMaxValue())) {
                    abnormalReason.append("氨气浓度超标;");
                    isAbnormal = true;
                }
            }

            data.setIsAbnormal(isAbnormal ? 1 : 0);
            if (isAbnormal) {
                data.setAbnormalReason(abnormalReason.toString());
            }
        } catch (Exception e) {
            // 如果标准表没有数据，默认设置为正常
            System.err.println("检查异常数据失败（可能是缺少环境标准数据）：" + e.getMessage());
            data.setIsAbnormal(0);
            data.setAbnormalReason(null);
        }
    }

    /**
     * 判断值是否在范围内
     */
    private boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}



