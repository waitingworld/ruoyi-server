package com.ruoyi.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 执行监听器
 */
@Component
public class ActivitiExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("xml任务：" + delegateExecution.getId() + " ActivitiListenner" + this.toString());

        String eventName = delegateExecution.getEventName();
        System.out.println("===="+eventName+"=====");
    }
}
