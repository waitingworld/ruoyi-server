package com.ruoyi.web.controller.business;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yw")
public class YwController extends BaseController {

    @PostMapping(value = "hello")
    public AjaxResult hello(@RequestBody JSONObject jsonObject) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        jsonObject.put("message", "hello, " + loginUser.getUsername());
        return AjaxResult.success(jsonObject);
    }
}
