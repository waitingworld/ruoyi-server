package com.ruoyi.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;

/**
 * 执行监听器
 */
@Component
public class ActivitiExecutionListener implements ExecutionListener {
    // 定义一个流程注入的表达式
    private Expression expression;
    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("xml任务：" + delegateExecution.getId() + " ActivitiListenner" + this.toString());
        System.out.println("expression：" + expression.getExpressionText());//输出字符串表达式的内容
//        eventName:
//        start：开始时触发
//        end：结束时触发
//        take：主要用于监控流程线，当流程流转该线时触发
        String eventName = delegateExecution.getEventName();
        System.out.println("===="+eventName+"=====");
    }
}
