package com.ruoyi.activiti.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IProcessService {

    /**
     * 部署流程
     *
     * @param xmlStr
     * @return
     */
    JSONObject deploymentByXml(String xmlStr);

    /**
     * 启动流程
     *
     * @param processDefinitionKey
     * @param userId
     * @param businessKey
     * @param variablesJson
     * @return
     */
    JSONObject startProcess(String processDefinitionKey, String userId, String businessKey, JSONObject variablesJson);

    /**
     * 根据流程实力id获取用户任务
     *
     * @param procInstId
     * @return
     */
    JSONObject getCurrentUserTaskByProInstId(String procInstId, String assignees);

    /**
     * 根据UserId获取待办列表
     *
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    JSONObject getTodoList(String userId, int pageNo, int pageSize);

    /**
     * 根据UserId获取已办列表
     *
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    JSONObject getDoneList(String userId, int pageNo, int pageSize);

    /**
     * 完成用户任务
     *
     * @param taskId        任务id
     * @param variablesJson 流程参数
     * @return
     */
    JSONObject completeTask(String taskId, JSONObject variablesJson);

    /**
     * 获取树形结构的流程类型
     *
     * @return
     */
    JSONObject getCatgoryTree();

    /**
     * 获取工作流流程列表
     *
     * @param params
     * @return
     */
    JSONObject getFlowList(JSONObject params);

    /**
     * 建立流程
     *
     * @param params
     * @return
     */
    JSONObject createProcess(JSONObject params);

    /**
     * 建立流程
     *
     * @param params
     * @return
     */
    JSONObject getProcessXmlByModelId(JSONObject params);

    /**
     * 删除流程定义
     *
     * @param params
     * @return
     */
    JSONObject deleteProcessDefind(JSONObject params);

    /**
     * 获取下一个节点
     * @param taskId
     * @return
     */
    List<JSONObject> getNextNode(String taskId);
    JSONObject getTaskInfo(String taskId);
}
