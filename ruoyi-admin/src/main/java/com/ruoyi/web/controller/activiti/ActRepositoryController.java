package com.ruoyi.web.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activiti/repository")
public class ActRepositoryController extends BaseController {
    @Autowired
    private IProcessService processService;

    /**
     * 根据xml部署流程
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/deploymentByXml")
    public AjaxResult deploymentByXml(@RequestBody JSONObject params) {
        String xmlStr = params.getString("xmlStr");
        return AjaxResult.success(processService.deploymentByXml(xmlStr));
    }

    /**
     * 获取工作流类型
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/getCatgoryTree")
    public AjaxResult getCatgoryTree(@RequestBody JSONObject params) {
        return AjaxResult.success(processService.getCatgoryTree());
    }

    /**
     * 获取流程列表
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/getFlowList")
    public AjaxResult getFlowList(@RequestBody JSONObject params) {
        return AjaxResult.success(processService.getFlowList(params));
    }

    /**
     * 创建流程模型
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/createProcess")
    public AjaxResult createProcess(@RequestBody JSONObject params) {
        return AjaxResult.success(processService.createProcess(params));
    }

    /**
     * 根据模型id获取xml
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/getProcessXmlByModelId")
    public AjaxResult getProcessXmlByModelId(@RequestBody JSONObject params) {
        return AjaxResult.success(processService.getProcessXmlByModelId(params));
    }

    @PostMapping(value = "/getProcessXmlByProcessInstanceId")
    public AjaxResult getProcessXmlByProcessInstanceId(@RequestBody JSONObject params) {
        return AjaxResult.success(processService.getProcessXmlByProcessInstanceId(params));
    }

    /**
     * 删除流程定义
     * @param params
     * @return
     */
    @PostMapping(value = "/deleteProcessDefind")
    public AjaxResult deleteProcessDefind(@RequestBody JSONObject params) {
        return AjaxResult.success(processService.deleteProcessDefind(params));
    }


}
