package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.nio.charset.StandardCharsets;

@TableName(value = "work_flow_ge_xml")
public class WorkFlowGeXml {
    @TableId(type = IdType.INPUT, value = "deployment_id")
    private String deploymentId;
    @TableField(exist = false)
    private String xml;
    @TableField(exist = true, value = "xml")
    private byte[] xmlBlob;

    public byte[] getXmlBlob() {
        return xmlBlob;
    }

    public void setXmlBlob(byte[] xmlBlob) {
        this.xmlBlob = xmlBlob;
        this.xml = new String(xmlBlob);
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
        this.xmlBlob = xml.getBytes(StandardCharsets.UTF_8);
    }
}
