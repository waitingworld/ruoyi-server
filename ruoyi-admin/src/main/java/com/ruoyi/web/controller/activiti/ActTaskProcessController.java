package com.ruoyi.web.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activiti/task")
public class ActTaskProcessController extends BaseController {
    @Autowired
    private IProcessService processService;

    /**
     * 获取待办事项
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/getTodoList")
    public AjaxResult getTodoList(@RequestBody JSONObject params) {
        int pageNo = params.getInteger("pageNo");
        int pageSize = params.getInteger("pageSize");
        String userId = params.containsKey("userId") ? params.getString("userId") : SecurityUtils.getUserId();
        return AjaxResult.success(processService.getTodoList(userId, pageNo, pageSize));
    }

    /**
     * 获取已办事项
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/getDoneList")
    public AjaxResult getDoneList(@RequestBody JSONObject params) {
        int pageNo = params.getInteger("pageNo");
        int pageSize = params.getInteger("pageSize");
        String userId = params.containsKey("userId") ? params.getString("userId") : SecurityUtils.getUserId();
        return AjaxResult.success(processService.getDoneList(userId, pageNo, pageSize));
    }

    /**
     * 完成任务节点
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/completeTask")
    public AjaxResult completeTask(@RequestBody JSONObject params) {
        String taskId = params.getString("taskId");
        JSONObject variablesJson = params.getJSONObject("variablesJson");
        return AjaxResult.success(processService.completeTask(taskId, variablesJson));
    }

    /**
     * 根据taskId获取下一步节点
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/getNextNode")
    public AjaxResult getNextNode(@RequestBody JSONObject params) {
        String taskId = params.getString("taskId");
        return AjaxResult.success(processService.getNextNode(taskId));
    }

    @PostMapping(value = "/getTaskInfo")
    public AjaxResult getTaskInfo(@RequestBody JSONObject params) {
        String taskId = params.getString("taskId");
        return AjaxResult.success(processService.getTaskInfo(taskId));
    }
}
