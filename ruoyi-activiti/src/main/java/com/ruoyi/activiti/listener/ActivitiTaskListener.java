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
        String event = delegateTask.getEventName();
//        event:
//        create：任务创建时触发，此时所有属性已被设置完毕
//        assignment：任务被委派给某人后触发，如通过变量的方式设置处理人时会触发，先于create事件触发
//        complete：在任务完成后，且被从运行时数据（runtime data）中删除前触发。
//        delete：在任务将要被删除之前发生。
        switch (event) {
            case "create" :
                System.out.println("create event");
                break;
            case "assignment" :
                System.out.println("assignment event");
                break;
            case "complete" :
                System.out.println("complete event");
                break;
            case "delete" :
                System.out.println("delete event");
                break;
        }
    }
}
