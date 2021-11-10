package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName(value = "work_flow_def")
public class WorkFlowDef {
    @TableId(type = IdType.INPUT, value = "process_key")
    private String processKey;//流程key，主键id
    @TableField(value = "process_name")
    private String processName;//流程名称
    @TableField(value = "description")
    private String description;//流程描述
    @TableField(value = "version")
    private Integer version;//版本号
    @TableField(value = "category")
    private String category;//类型
    @TableField(value = "create_time")
    private Date createTime;//创建时间
    @TableField(value = "update_time")
    private Date updateTime;//更新时间
    @TableField(value = "deployment_id")
    private String deploymentId;//部署id
    @TableField(value = "model_id")
    private String modelId;//部署id
    @TableField(value = "delete_flag")
    private String deleteFlag;//删除标识，0删除，;1未删除

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
