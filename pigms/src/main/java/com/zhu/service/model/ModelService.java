package com.zhu.service.model;

import com.zhu.mapper.ModelMapper;
import com.zhu.pojo.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 预测模型 Service
 */
@Service
public class ModelService {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 插入
     */
    public int insert(Model model) {
        if (model.getCreateTime() == null) {
            model.setCreateTime(new Date());
        }
        if (model.getUpdateTime() == null) {
            model.setUpdateTime(new Date());
        }
        if (model.getStatus() == null) {
            model.setStatus("ACTIVE");
        }
        return modelMapper.insert(model);
    }

    /**
     * 删除
     */
    public int deleteByPrimaryKey(Integer id) {
        return modelMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新
     */
    public int updateByPrimaryKey(Model model) {
        model.setUpdateTime(new Date());
        return modelMapper.updateByPrimaryKey(model);
    }

    /**
     * 查询单条
     */
    public Model selectByPrimaryKey(Integer id) {
        return modelMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询所有
     */
    public List<Model> selectAll() {
        return modelMapper.selectAll();
    }

    /**
     * 按类型查询
     */
    public List<Model> selectByType(String modelType) {
        return modelMapper.selectByType(modelType);
    }

    /**
     * 查询公开模型
     */
    public List<Model> selectPublicModels() {
        return modelMapper.selectPublicModels();
    }

    /**
     * 按创建者查询
     */
    public List<Model> selectByCreatorId(Integer creatorId) {
        return modelMapper.selectByCreatorId(creatorId);
    }

    /**
     * 按条件查询
     */
    public List<Model> selectByCondition(String modelName, String modelType, String status) {
        return modelMapper.selectByCondition(modelName, modelType, status);
    }

    /**
     * 选择性更新
     */
    public int updateByPrimaryKeySelective(Model model) {
        model.setUpdateTime(new Date());
        return modelMapper.updateByPrimaryKeySelective(model);
    }
}



