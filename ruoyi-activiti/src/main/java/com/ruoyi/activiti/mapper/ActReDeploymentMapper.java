package com.ruoyi.activiti.mapper;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.ActReDeployment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 【activiti部署记录表】Mapper接口
 *
 * @author ruoyi
 * @date 2021-06-22
 */
@Mapper
public interface ActReDeploymentMapper extends BaseMapper<ActReDeployment> {

    /**
     * 获取全部流程类型
     *
     * @return
     */
    public List<JSONObject> selectAllCatgory();
}
