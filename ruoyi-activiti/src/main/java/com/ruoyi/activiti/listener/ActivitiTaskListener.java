package com.ruoyi.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 任务监听器
 */
@Component
public class ActivitiTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("xml任务：" + delegateTask.getId() + " ActivitiListenner" + this.toString());

        System.out.println("getEventName" + delegateTask.getEventName());
        System.out.println("getCategory" + delegateTask.getCategory());
        System.out.println("getDelegationState" + delegateTask.getDelegationState());
    }
}
