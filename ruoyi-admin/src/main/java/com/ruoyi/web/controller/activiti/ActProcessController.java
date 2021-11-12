package com.ruoyi.web.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.uuid.impl.UUIDUtil;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activiti/process")
public class ActProcessController extends BaseController {
    @Autowired
    private IProcessService processService;

    /**
     * 启动流程
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/startProcess")
    public AjaxResult startProcess(@RequestBody JSONObject params) {
        String processDefinitionKey = params.getString("processDefinitionKey");
        String businessKey = params.getString("businessKey");
        JSONObject variablesJson = params.getJSONObject("variablesJson");
        // 获取当前用户ID
        String userId = SecurityUtils.getUserId();
        if (StringUtils.isAnyEmpty(businessKey)) {
            businessKey = UUID.randomUUID().toString(true);
        }
        return AjaxResult.success(processService.startProcess(processDefinitionKey, userId, businessKey, variablesJson));
    }
}
