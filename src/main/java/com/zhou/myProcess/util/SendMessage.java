package com.zhou.myProcess.util;

import java.util.List;

import com.zhou.myProcess.instance.ProcessModel;
import com.zhou.myProcess.instance.TaskModel;

public interface SendMessage {
    /**
     * 发送下一个审批人的消息信息
     */
    void sendNextMessage(ProcessModel processModel, TaskModel taskModel);

    /**
     * 发送结束流程的信息
     */
    void sendEndMessage(ProcessModel processModel, List<TaskModel> copyToTasks);
}
