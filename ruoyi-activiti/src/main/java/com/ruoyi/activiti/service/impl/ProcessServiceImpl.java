package com.ruoyi.activiti.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.ruoyi.activiti.domain.WorkFlowDef;
import com.ruoyi.activiti.domain.WorkFlowGeXml;
import com.ruoyi.activiti.mapper.ActReDeploymentMapper;
import com.ruoyi.activiti.mapper.WorkFlowDefMapper;
import com.ruoyi.activiti.mapper.WorkFlowGeXmlMapper;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.attribute.HashAttributeSet;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessServiceImpl implements IProcessService {

    private static final Logger log = LoggerFactory.getLogger(IProcessService.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActReDeploymentMapper actReDeploymentMapper;
    @Autowired
    private WorkFlowGeXmlMapper workFlowGeXmlMapper;
    @Autowired
    private WorkFlowDefMapper workFlowDefMapper;

    @Override
    public JSONObject createProcess(JSONObject params) {
        String modelId = params.getString("modelId");
        String processName = params.getString("processName");
        String processKey = params.getString("processKey");
        String category = params.getString("category");
        String description = params.getString("description");
        JSONObject result = new JSONObject();

        Model model = repositoryService.createModelQuery().modelKey(processKey).latestVersion().singleResult();
        if (model != null) {
            result.put("success", false);
            result.put("msg", "当前key已存在");
            return result;
        }

        if (StringUtils.isNotEmpty(modelId)) {
            model = repositoryService.getModel(modelId);
        }
        if (model == null) {
            model = repositoryService.newModel();
            model.setVersion(1);
        }
        JSONObject metaInfo = new JSONObject();
        metaInfo.put("name", processName);
        metaInfo.put("revision", 1);
        metaInfo.put("description", description);

        model.setName(processName);
        model.setKey(processKey);
        model.setCategory(category);
        model.setMetaInfo(metaInfo.toJSONString());

        repositoryService.saveModel(model);//保存模型

        WorkFlowDef workFlowDef = new WorkFlowDef();
        workFlowDef.setProcessKey(processKey);
        workFlowDef.setCategory(category);
        workFlowDef.setVersion(1);
        workFlowDef.setProcessName(processName);
        workFlowDef.setDeploymentId(model.getDeploymentId());
        workFlowDef.setCreateTime(model.getCreateTime());
        workFlowDef.setDescription(description);
        workFlowDef.setUpdateTime(model.getLastUpdateTime());
        workFlowDef.setModelId(model.getId());
        workFlowDef.setDeleteFlag("1");
        workFlowDefMapper.insert(workFlowDef);//保存流程列表

        result.put("model", model);
        String xmlStr = this.getOrgXml(category, processKey, processName);
        xmlStr = StringUtils.join(xmlStr.split("activiti"), "camunda");
        result.put("xmlStr", xmlStr);
        result.put("success", true);
        return result;
    }

    private String getOrgXml(String category, String processKey, String processName) {
        String uuid = UUID.randomUUID().toString().toLowerCase().replaceAll("-", "").substring(0, 7);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<definitions " +
                "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" " +
                "xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" " +
                "xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:activiti=\"http://activiti.org/bpmn\" " +
                "id=\"sid-" + UUID.randomUUID().toString().toLowerCase().replaceAll("-", "") + "\" " +
                "targetNamespace=\"" + category + "\" " +
                "exporter=\"bpmn-js (https://demo.bpmn.io)\"" +
                " exporterVersion=\"8.0.1\">" +
                "  <process id=\"" + processKey + "\" name=\"" + processName + "\" isExecutable=\"true\">" +
                "    <startEvent id=\"Event_s" + uuid + "\">" +
                "      <outgoing>Flow_" + uuid + "</outgoing>" +
                "    </startEvent>" +
                "    <sequenceFlow id=\"Flow_" + uuid + "\" sourceRef=\"Event_s" + uuid + "\" targetRef=\"Activity_" + uuid + "\" />" +
                "    <userTask id=\"Activity_" + uuid + "\" activiti:assignee=\"${startUser}\" name=\"申请\">" +
                "      <incoming>Flow_" + uuid + "</incoming>" +
                "      <outgoing>Flow_1" + uuid + "</outgoing>" +
                "    </userTask>" +
                "    <endEvent id=\"Event_e" + uuid + "\">" +
                "      <incoming>Flow_1" + uuid + "</incoming>" +
                "    </endEvent>" +
                "    <sequenceFlow id=\"Flow_1" + uuid + "\" sourceRef=\"Activity_" + uuid + "\" targetRef=\"Event_e" + uuid + "\" />" +
                "  </process>" +
                "  <bpmndi:BPMNDiagram id=\"BpmnDiagram_1\">" +
                "    <bpmndi:BPMNPlane id=\"BpmnPlane_1\" bpmnElement=\"" + processKey + "\">" +
                "      <bpmndi:BPMNEdge id=\"Flow_" + uuid + "_di\" bpmnElement=\"Flow_" + uuid + "\">" +
                "        <omgdi:waypoint x=\"188\" y=\"300\" />" +
                "        <omgdi:waypoint x=\"250\" y=\"300\" />" +
                "      </bpmndi:BPMNEdge>" +
                "      <bpmndi:BPMNEdge id=\"Flow_1" + uuid + "_di\" bpmnElement=\"Flow_1" + uuid + "\">" +
                "        <omgdi:waypoint x=\"350\" y=\"300\" />" +
                "        <omgdi:waypoint x=\"412\" y=\"300\" />" +
                "      </bpmndi:BPMNEdge>" +
                "      <bpmndi:BPMNShape id=\"Event_s" + uuid + "_di\" bpmnElement=\"Event_s" + uuid + "\">" +
                "        <omgdc:Bounds x=\"152\" y=\"282\" width=\"36\" height=\"36\" />" +
                "      </bpmndi:BPMNShape>" +
                "      <bpmndi:BPMNShape id=\"Activity_" + uuid + "_di\" bpmnElement=\"Activity_" + uuid + "\">" +
                "        <omgdc:Bounds x=\"250\" y=\"260\" width=\"100\" height=\"80\" />" +
                "      </bpmndi:BPMNShape>" +
                "      <bpmndi:BPMNShape id=\"Event_e" + uuid + "_di\" bpmnElement=\"Event_e" + uuid + "\">" +
                "        <omgdc:Bounds x=\"412\" y=\"282\" width=\"36\" height=\"36\" />" +
                "      </bpmndi:BPMNShape>" +
                "    </bpmndi:BPMNPlane>" +
                "  </bpmndi:BPMNDiagram>" +
                "</definitions>";
    }

    @Override
    public JSONObject deploymentByXml(String xmlStr) {
        xmlStr = StringUtils.join(xmlStr.split("camunda"), "activiti");
        log.debug(xmlStr);
        JSONObject result = new JSONObject();
        try {
            //获取流程XML
            XMLInputFactory factory = XMLInputFactory.newFactory();
            Reader reader = new StringReader(xmlStr);
            XMLStreamReader streamReader = factory.createXMLStreamReader(reader);
            BpmnXMLConverter converter = new BpmnXMLConverter();
            BpmnModel bpmnModel = converter.convertToBpmnModel(streamReader);
            Process process = bpmnModel.getMainProcess();
            String processKey = process.getId();
            WorkFlowDef workFlowDef = workFlowDefMapper.selectById(processKey);
            if (workFlowDef == null) {
                result.put("success", false);
                result.put("msg", "当前流程定义信息不存在，请先创建流程，再部署流程");
                return result;
            }

            //进行部署
            Deployment deployment = repositoryService.createDeployment()//创建Deployment对象
                    .name(process.getName())//名称
                    .key(processKey)
                    .category(bpmnModel.getTargetNamespace())//分类
                    .addBpmnModel(processKey + ".bpmn20.xml", bpmnModel)//fileName:oneTasktest.bpmn20.xml
                    .deploy();//部署

            log.info("deploymentByXml:" + process.getName() + ", " + processKey + ".bpmn20.xml" + ", " + processKey + ", " + bpmnModel.getTargetNamespace());
            log.debug("xmlStr:" + xmlStr);

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processKey)
                    .latestVersion()
                    .singleResult();

            Model model = repositoryService.createModelQuery().modelKey(processKey).singleResult();
            if (model == null) {
                model = repositoryService.newModel();
                model.setName(process.getName());
                model.setKey(processKey);
                model.setCategory(bpmnModel.getTargetNamespace());
            }
            JSONObject metaInfo = JSONObject.parseObject(model.getMetaInfo());
            if (metaInfo == null) {
                metaInfo = new JSONObject();
            }
            metaInfo.put("name", process.getName());
            metaInfo.put("revision", processDefinition.getVersion());
            model.setMetaInfo(metaInfo.toJSONString());
            model.setVersion(processDefinition.getVersion());//版本号加一
            model.setDeploymentId(deployment.getId());
            repositoryService.saveModel(model);//保存流程图模型
            model = repositoryService.createModelQuery().modelKey(processKey).singleResult();
            JSONObject processDefJson = new JSONObject();
            processDefJson.put("id", processDefinition.getId());
            processDefJson.put("category", processDefinition.getCategory());
            processDefJson.put("name", processDefinition.getName());
            processDefJson.put("key", processDefinition.getKey());
            processDefJson.put("description", processDefinition.getDescription());
            processDefJson.put("version", processDefinition.getVersion());
            processDefJson.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, model.getCreateTime()));
            processDefJson.put("deploymentTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, model.getLastUpdateTime()));
            processDefJson.put("resourceName", processDefinition.getResourceName());
            processDefJson.put("deploymentId", processDefinition.getDeploymentId());
            processDefJson.put("diagramResourceName", processDefinition.getDiagramResourceName());
            processDefJson.put("tenantId", processDefinition.getTenantId());
            processDefJson.put("engineVersion", processDefinition.getEngineVersion());
            result.put("processDefinition", processDefJson);
            result.put("success", true);

            WorkFlowGeXml workFlowGeXml = new WorkFlowGeXml();
            workFlowGeXml.setDeploymentId(model.getDeploymentId());
            workFlowGeXml.setXml(xmlStr);
            workFlowGeXmlMapper.insert(workFlowGeXml);

            workFlowDef.setProcessKey(processKey);
            workFlowDef.setCategory(processDefinition.getCategory());
            workFlowDef.setVersion(processDefinition.getVersion());
            workFlowDef.setProcessName(processDefinition.getName());
            workFlowDef.setDeploymentId(model.getDeploymentId());
            workFlowDef.setDescription(processDefinition.getDescription());
            workFlowDef.setUpdateTime(model.getLastUpdateTime());
            workFlowDef.setDeleteFlag("1");
            workFlowDef.setModelId(model.getId());
            workFlowDefMapper.updateById(workFlowDef);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @Override
    public JSONObject startProcess(String processDefinitionKey, String userId, String businessKey, JSONObject variablesJson) {
        Map<String, Object> variables = new HashMap<>();
        variables.putAll(variablesJson);
        variables.put("startUser", userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        JSONObject result = new JSONObject();
        List<JSONObject> tasksJson = new ArrayList<>();
        for (Task task : tasks) {
            JSONObject tmp = new JSONObject();
            tmp.put("id", task.getId());
            tmp.put("name", task.getName());
            tmp.put("assignee", task.getAssignee());
            tmp.put("category", task.getCategory());
            tmp.put("processInstanceId", task.getProcessInstanceId());
            tmp.put("taskDefinitionKey", task.getTaskDefinitionKey());
            tmp.put("claimTime", task.getClaimTime());
            tmp.put("createTime", task.getCreateTime());
            tmp.put("dueDate", task.getDueDate());
            tmp.put("owner", task.getOwner());
            taskService.claim(task.getId(), task.getAssignee());
            tasksJson.add(tmp);
        }
        JSONObject processInstanceJson = new JSONObject();
        processInstanceJson.put("id", processInstance.getId());
        processInstanceJson.put("processInstanceId", processInstance.getProcessInstanceId());
        processInstanceJson.put("businessKey", processInstance.getBusinessKey());
        processInstanceJson.put("deploymentId", processInstance.getDeploymentId());
        processInstanceJson.put("name", processInstance.getName());
        processInstanceJson.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
        processInstanceJson.put("description", processInstance.getDescription());
        processInstanceJson.put("processVariables", processInstance.getProcessVariables());
        processInstanceJson.put("startTime", processInstance.getStartTime());
        processInstanceJson.put("startUserId", processInstance.getStartUserId());
        processInstanceJson.put("processDefinitionVersion", processInstance.getProcessDefinitionVersion());
        result.put("tasks", tasksJson);
        result.put("processInstance", processInstanceJson);
        return result;
    }

    @Override
    public JSONObject getCurrentUserTaskByProInstId(String procInstId, String assignees) {
        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(procInstId);
        List<String> assigneeList = Arrays.asList(assignees.split(","));
        if (assigneeList != null && assigneeList.size() > 1) {
            taskQuery.taskAssigneeIds(assigneeList);
        } else {
            taskQuery.taskAssignee(assignees);
        }
        List<Task> tasks = taskQuery.list();
        JSONObject result = new JSONObject();
        result.put("tasks", tasks);
        return result;
    }

    @Override
    public JSONObject completeTask(String taskId, JSONObject variablesJson) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                throw new RuntimeException("当前任务节点不存在");
            } else if (!String.valueOf(task.getAssignee()).equals(String.valueOf(SecurityUtils.getUserId()))) {
                throw new RuntimeException("这不是你的任务，完成失败");
            }
            taskService.setVariables(taskId, variablesJson);
            taskService.complete(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", e.getMessage());
        } finally {
            return result;
        }
    }

    @Override
    public JSONObject getTodoList(String userId, int pageNo, int pageSize) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId.toString()).orderByTaskCreateTime().desc();
        List<Task> taskList = taskQuery.listPage(pageNo - 1, pageSize);
        JSONObject result = new JSONObject();
        List<JSONObject> taskListJson = new ArrayList<>();
        for (Task task : taskList) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            JSONObject tmp = createTaskJson(task, historicProcessInstance);
            tmp.put("nextNodes", this.getNextNode(task.getId()));
            taskListJson.add(tmp);
        }
        result.put("tasks", taskListJson);
        result.put("total", taskQuery.count());
        return result;
    }

    private JSONObject createTaskJson(Task task, HistoricProcessInstance historicProcessInstance) {
        JSONObject tmp = new JSONObject();
        tmp.put("assignee", task.getAssignee());
        if (StringUtils.isNotEmpty(task.getAssignee())) {
            tmp.put("assigneeName", sysUserService.selectUserById(task.getAssignee()).getNickName());
        }
        if (StringUtils.isNotEmpty(task.getOwner())) {
            tmp.put("ownerName", sysUserService.selectUserById(task.getOwner()).getNickName());
        }
        tmp.put("category", task.getCategory());
        tmp.put("startTime", task.getCreateTime());
        tmp.put("claimTime", task.getClaimTime());
        tmp.put("id", task.getId());
        tmp.put("delegationState", task.getDelegationState());//委托状态
        tmp.put("description", task.getDescription());
        tmp.put("executionId", task.getExecutionId());
        tmp.put("name", task.getName());
        tmp.put("owner", task.getOwner());
        tmp.put("parentTaskId", task.getParentTaskId());
        tmp.put("processInstanceId", task.getProcessInstanceId());
        tmp.put("tenantId", task.getTenantId());

        tmp.put("createTime", historicProcessInstance.getStartTime());
        tmp.put("processName", historicProcessInstance.getProcessDefinitionName());
        tmp.put("businessKey", historicProcessInstance.getBusinessKey());
        tmp.put("modelId", repositoryService.createModelQuery().deploymentId(historicProcessInstance.getDeploymentId()).singleResult().getId());
        return tmp;
    }

    @Override
    public JSONObject getDoneList(String userId, int pageNo, int pageSize) {
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .orderByTaskCreateTime().desc();
        List<HistoricTaskInstance> taskList = taskInstanceQuery.listPage(pageNo - 1, pageSize);
        List<JSONObject> taskJsonList = new ArrayList<>();
        JSONObject result = new JSONObject();
        for (HistoricTaskInstance historicTaskInstance : taskList) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(historicTaskInstance.getProcessInstanceId())
                    .singleResult();
            JSONObject tmp = createDoneTaskJson(historicTaskInstance, historicProcessInstance);
            taskJsonList.add(tmp);
        }
        result.put("tasks", taskJsonList);
        result.put("total", taskInstanceQuery.count());
        return result;
    }

    private JSONObject createDoneTaskJson(HistoricTaskInstance task, HistoricProcessInstance historicProcessInstance) {
        JSONObject tmp = new JSONObject();
        tmp.put("assignee", task.getAssignee());
        if (StringUtils.isNotEmpty(task.getAssignee())) {
            tmp.put("assigneeName", sysUserService.selectUserById(task.getAssignee()).getNickName());
        }
        if (StringUtils.isNotEmpty(task.getOwner())) {
            tmp.put("ownerName", sysUserService.selectUserById(task.getOwner()).getNickName());
        }
        tmp.put("id", task.getId());
        tmp.put("startTime", task.getCreateTime());
        tmp.put("claimTime", task.getClaimTime());
        tmp.put("endTime", task.getEndTime());
        tmp.put("description", task.getDescription());
        if (task.getDurationInMillis() != null) {
            tmp.put("dueDate", (task.getDurationInMillis() / 1000 / 60));
        }
        tmp.put("executionId", task.getExecutionId());
        tmp.put("name", task.getName());
        tmp.put("owner", task.getOwner());
        tmp.put("parentTaskId", task.getParentTaskId());
        tmp.put("processInstanceId", task.getProcessInstanceId());
        tmp.put("tenantId", task.getTenantId());

        tmp.put("businessKey", historicProcessInstance.getBusinessKey());
        tmp.put("processName", historicProcessInstance.getProcessDefinitionName());
        tmp.put("createTime", historicProcessInstance.getStartTime());
        tmp.put("modelId", repositoryService.createModelQuery().deploymentId(historicProcessInstance.getDeploymentId()).singleResult().getId());
        return tmp;
    }

    @Override
    public JSONObject getCatgoryTree() {
        JSONObject result = new JSONObject();
        result.put("catgory", actReDeploymentMapper.selectAllCatgory());
        return result;
    }

    /**
     * 获取当前TaskId对应的节点的下一个节点
     *
     * @param taskId
     * @return
     */
    @Override
    public List<JSONObject> getNextNode(String taskId) {
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = currentTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        Process process = bpmnModel.getMainProcess();
        FlowElement currentElement = this.getCurrentElement(process, currentTask);
        List<JSONObject> nextNode = new ArrayList<>();
        if (currentElement != null) {
            findNextNodeByOutgoingFlows(UserTask.class, ((UserTask) currentElement).getOutgoingFlows(), nextNode);
        }
        return nextNode;
    }

    @Override
    public JSONObject getTaskInfo(String taskId) {
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(taskInstance.getProcessInstanceId())
                .singleResult();
        return this.createDoneTaskJson(taskInstance, historicProcessInstance);
    }

    @Override
    public JSONObject getHistoryTask(String processInstanceId) {
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
        List<String> highLightedShapes = new ArrayList<>();
        Process process = bpmnModel.getMainProcess();
        Map<String, FlowElement> flowElementMap = process.getFlowElementMap();
        List<String> historicTaskIds = new ArrayList<>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            historicTaskIds.add(historicActivityInstance.getActivityId());
        }
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            String activityId = historicActivityInstance.getActivityId();
            highLightedShapes.add(activityId);
            FlowNode tmp = (FlowNode) flowElementMap.get(activityId);
            this.getHightLine(highLightedShapes, historicTaskIds, tmp.getOutgoingFlows());
        }

        List<Task> currentTask = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        List<String> taskIdList = new ArrayList<>();
        for (Task task : currentTask) {
            taskIdList.add(task.getTaskDefinitionKey());
        }

        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("highLightedShapes", highLightedShapes);
        result.put("currentTask", taskIdList);
        return result;
    }

    private void getHightLine(List<String> highLightedShapes, List<String> historicTaskIds, List<SequenceFlow> outgoingFlows) {
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            String targetElementId = outgoingFlow.getTargetFlowElement().getId();
            if (historicTaskIds.contains(targetElementId)) {
                highLightedShapes.add(outgoingFlow.getId());
            }
        }
    }

    /**
     * 查找当前节点
     *
     * @param process
     * @param currentTask
     * @return
     */
    private FlowElement getCurrentElement(BaseElement process, Task currentTask) {
        FlowElement currentElement = (process instanceof Process) ? ((Process) process).getFlowElement(currentTask.getTaskDefinitionKey()) : (process instanceof SubProcess) ? ((SubProcess) process).getFlowElement(currentTask.getTaskDefinitionKey()) : null;
        if (currentElement == null) {
            Collection<FlowElement> flowElements = (process instanceof Process) ? ((Process) process).getFlowElements() : (process instanceof SubProcess) ? ((SubProcess) process).getFlowElements() : new ArrayList<>();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof SubProcess) {
                    currentElement = this.getCurrentElement(flowElement, currentTask);
                    if (currentElement != null) {
                        break;
                    }
                }
            }
        }
        return currentElement;
    }

    /**
     * 根据当前节点的所有出路，遍历寻找下一个指定类型的节点
     *
     * @param T             指定的节点类型
     * @param outgoingFlows 当前节点的全部出路集合
     * @param nextNode      找到的节点的集合
     */
    private void findNextNodeByOutgoingFlows(Class T, List<SequenceFlow> outgoingFlows, List<JSONObject> nextNode) {
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement targetElement = outgoingFlow.getTargetFlowElement();
            if (T.getName().equals(targetElement.getClass().getName())) {
                createFindNodeResult(nextNode, outgoingFlow, targetElement);
            } else if (targetElement instanceof UserTask) {//用户任务
                this.findNextNodeByOutgoingFlows(T, ((UserTask) targetElement).getOutgoingFlows(), nextNode);
            } else if (targetElement instanceof InclusiveGateway) {//包含网关
                this.findNextNodeByOutgoingFlows(T, ((InclusiveGateway) targetElement).getOutgoingFlows(), nextNode);
            } else if (targetElement instanceof ExclusiveGateway) {//排他网关
                this.findNextNodeByOutgoingFlows(T, ((ExclusiveGateway) targetElement).getOutgoingFlows(), nextNode);
            } else if (targetElement instanceof ParallelGateway) {//并行网关
                this.findNextNodeByOutgoingFlows(T, ((ParallelGateway) targetElement).getOutgoingFlows(), nextNode);
            } else if (targetElement instanceof SubProcess) {//子流程
                for (FlowElement flowElement : ((SubProcess) targetElement).getFlowElements()) {
                    if (flowElement instanceof StartEvent) {
                        this.findNextNodeByOutgoingFlows(T, ((StartEvent) flowElement).getOutgoingFlows(), nextNode);
                        break;
                    }
                }
            } else if (targetElement instanceof EndEvent) {//结束事件
                continue;
            }
        }
    }

    /**
     * 组装返回的结果
     *
     * @param nextNode
     * @param outgoingFlow
     * @param targetElement
     */
    private void createFindNodeResult(List<JSONObject> nextNode, SequenceFlow outgoingFlow, FlowElement targetElement) {
        JSONObject tmp = new JSONObject();
        tmp.put("id", targetElement.getId());
        tmp.put("name", targetElement.getName());
        tmp.put("attributes", targetElement.getAttributes());
        tmp.put("outgoingFlow_ConditionExpression", outgoingFlow.getConditionExpression());
        tmp.put("outgoingFlow_Attributes", outgoingFlow.getAttributes());
        if (targetElement instanceof UserTask) {
            String assigneeStr = ((UserTask) targetElement).getAssignee();
            if (StringUtils.isNotEmpty(assigneeStr) && assigneeStr.startsWith("${") && assigneeStr.endsWith("}")) {
                tmp.put("assignee", assigneeStr.substring(2, assigneeStr.length() - 1));
            } else {
                tmp.put("assignee", ((UserTask) targetElement).getAssignee());
            }
            tmp.put("owner", ((UserTask) targetElement).getOwner());
            tmp.put("formKey", ((UserTask) targetElement).getFormKey());
            tmp.put("businessCalendarName", ((UserTask) targetElement).getBusinessCalendarName());
        } else if (targetElement instanceof Gateway) {
            tmp.put("DefaultFlow", ((Gateway) targetElement).getDefaultFlow());
        }
        nextNode.add(tmp);
    }

    @Override
    public JSONObject getFlowList(JSONObject params) {
        String processDefinitionName = params.getString("processDefinitionName");
        String processDefinitionCategory = params.getString("processDefinitionCategory");
        String processDefinitionVersion = params.getString("processDefinitionVersion");
        String processDefinitionKey = params.getString("processDefinitionKey");
        Integer pageNo = params.getInteger("pageNo");
        Integer pageSize = params.getInteger("pageSize");
        JSONObject result = new JSONObject();

        QueryWrapper<WorkFlowDef> workFlowDefQueryWrapper = new QueryWrapper<>();
        WorkFlowDef workFlowDef = new WorkFlowDef();
//        ModelQuery modelQuery = repositoryService.createModelQuery();
        if (StringUtils.isNotEmpty(processDefinitionName)) {
            workFlowDef.setProcessName(processDefinitionName);
        }
        if (StringUtils.isNotEmpty(processDefinitionCategory)) {
            workFlowDef.setCategory(processDefinitionCategory);
        }
        if (StringUtils.isNotEmpty(processDefinitionVersion)) {
            workFlowDef.setVersion(Integer.valueOf(processDefinitionVersion));
        }
        if (StringUtils.isNotEmpty(processDefinitionKey)) {
            workFlowDef.setProcessKey(processDefinitionKey);
        }
        workFlowDefQueryWrapper.setEntity(workFlowDef);
        workFlowDefQueryWrapper.orderByDesc("create_time");
        PageHelper.startPage(pageNo, pageSize);
        List<WorkFlowDef> workFlowDefs = workFlowDefMapper.selectList(workFlowDefQueryWrapper);
        PageInfo<WorkFlowDef> pageInfo = new PageInfo<>(workFlowDefs);
//        List<Model> models = modelQuery.latestVersion().orderByCreateTime().desc().listPage(pageNo - 1, pageSize);
        List<JSONObject> flowList = new ArrayList<>();
        for (int i = 0; i < workFlowDefs.size(); i++) {
            WorkFlowDef flowDef = workFlowDefs.get(i);
            JSONObject item = new JSONObject();
            item.put("id", flowDef.getProcessKey());
            item.put("name", flowDef.getProcessName());
            item.put("key", flowDef.getProcessKey());
            item.put("description", flowDef.getDescription());
            item.put("version", flowDef.getVersion());
            item.put("category", flowDef.getCategory());
            item.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, flowDef.getCreateTime()));
            item.put("deploymentTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, flowDef.getUpdateTime()));
            item.put("deploymentId", flowDef.getDeploymentId());
            item.put("modelId", flowDef.getModelId());
            flowList.add(item);
        }
        result.put("flowList", flowList);
        result.put("total", pageInfo.getTotal());
        return result;
    }

    @Override
    public JSONObject getProcessXmlByModelId(JSONObject params) {
        JSONObject result = new JSONObject();
        String modelId = params.getString("modelId");
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            result.put("success", false);
            result.put("msg", "不存在模型(" + modelId + ")");
            return result;
        }
        if (StringUtils.isEmpty(model.getDeploymentId())) {
            result.put("xmlStr", this.getOrgXml(model.getCategory(), model.getKey(), model.getName()));
            result.put("success", true);
            return result;
        }
//        String resourceName = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult().getResourceName();
//        InputStream inputStream = repositoryService.getResourceAsStream(model.getDeploymentId(), resourceName);
        WorkFlowGeXml workFlowGeXml = workFlowGeXmlMapper.selectById(model.getDeploymentId());
        String xmlStr = workFlowGeXml.getXml();
        xmlStr = StringUtils.join(xmlStr.split("activiti"), "camunda");
        result.put("xmlStr", xmlStr);
//            result.put("xmlStr", IOUtils.toString(inputStream, "utf-8"));
        result.put("success", true);
        return result;
    }

    @Override
    public JSONObject getProcessXmlByProcessInstanceId(JSONObject params) {
        JSONObject result = new JSONObject();
        String processInstanceId = params.getString("processInstanceId");
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        Model model = repositoryService.createModelQuery().deploymentId(historicProcessInstance.getDeploymentId()).singleResult();
        if (model == null) {
            result.put("success", false);
            result.put("msg", "未找到当前流程图");
            return result;
        }
        if (StringUtils.isEmpty(model.getDeploymentId())) {
            result.put("xmlStr", this.getOrgXml(model.getCategory(), model.getKey(), model.getName()));
            result.put("success", true);
            return result;
        }
        WorkFlowGeXml workFlowGeXml = workFlowGeXmlMapper.selectById(model.getDeploymentId());
        String xmlStr = workFlowGeXml.getXml();
        xmlStr = StringUtils.join(xmlStr.split("activiti"), "camunda");
        result.put("xmlStr", xmlStr);
        result.put("success", true);
        return result;
    }

    @Override
    public JSONObject deleteProcessDefind(JSONObject params) {
        JSONObject result = new JSONObject();
        String deploymentId = params.getString("deploymentId");

        WorkFlowDef workFlowDef = new WorkFlowDef();
        workFlowDef.setUpdateTime(new Date());
        workFlowDef.setDeleteFlag("0");
        UpdateWrapper<WorkFlowDef> workFlowDefUpdateWrapper = new UpdateWrapper<>();
        workFlowDefUpdateWrapper.eq("deployment_id", deploymentId);
        workFlowDefMapper.update(workFlowDef, workFlowDefUpdateWrapper);
        result.put("success", true);
        return result;
    }
}
