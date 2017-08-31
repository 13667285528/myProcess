package com.zhou.myProcess.repository;

import java.util.List;

import com.zhou.myProcess.instance.ProcessModel;
import com.zhou.myProcess.instance.TaskModel;

/**
 * 用于进行连接数据库的接口
 */
public interface ProcessExcuter {
	/**
	 * 获取uuid
	 */
    String getuuid();

    /**
     * 执行一个用户任务
     */
    boolean excuteUserTask(TaskModel taskModel);
    
    /**
     * 激活任务
     */
    boolean activitiTask(TaskModel taskModel);
    
    /**
     * 执行一个流程的抄送任务
     */
    boolean excuteCopyToTask(ProcessModel processModel);

    /**
     * 发起一个流程，添加流程记录
     */
    boolean insertProcessRecord(ProcessModel processModel);
    
    /**
     * 发起一个流程，添加流程任务记录
     */
    boolean insertProcessTaskRecord(ProcessModel processModel);
    
    /**
     * 获取第一个流程的信息
     */
    TaskModel getFirstTask(ProcessModel processModel);
    
    /**
     * 获取第一个流程的信息
     */
    TaskModel getFirstGroupTask(ProcessModel processModel);
    
    /**
     * 添加第一个流程
     */
    boolean addFirstTask(TaskModel firstTask);
    
    /**
     * 修改流程状态
     */
    boolean updateProcessStatus(ProcessModel processModel);
    
    /**
     * 将某个流程的活动设置为睡眠状态
     */
    boolean sleepTask(ProcessModel processModel);
    
    /**
     * 查询下一个需要执行的任务
     */
    TaskModel selectNextTask(TaskModel taskModel);
    
    /**
     * 根据任务Id查询该任务所属的流程信息
     */
    ProcessModel selectProcessByTaskId(TaskModel taskModel);
    
    /**
     * 查询用户是否创建了这个类别的流程信息，如果创建了则返回流程id
     */
    ProcessModel checkUserProcess(ProcessModel processModel);
    
    /**
     * 为用户添加主流程信息
     */
    boolean addMainProcess(ProcessModel ProcessModel);
    
    /**
     * 为一个流程添加一个抄送流程节点，直接将流程节点中的所有信息添加进来
     */
    boolean addCopyToTask(TaskModel taskModel);
    
    /**
     * 为一个流程添加一个普通流程节点，自动添加流程序号为最后一个流程节点
     */
    boolean addTask(TaskModel taskModel);
    
    /**
     * 为当前最后一个节点，添加下一个节点的Id
     */
    boolean addNextTask(TaskModel taskModel);
    
    /**
     * 根据taskId获取完成的task信息
     */
    TaskModel getTaskByTaskId(TaskModel taskModel);
    
    /**
     * 根据taskId获取完成的task信息
     */
    TaskModel getTaskRecordByTaskId(TaskModel taskModel);
    
    /**
     * 根据taskId获取完成的task信息
     */
    ProcessModel getProcessRecordByProcessId(ProcessModel processModel);
    
    /**
     * 更新后续的任务的orderNumber
     */
    boolean updateAfterTask(TaskModel taskModel);
    
    /**
     * 删除流程任务节点
     */
    boolean deleteTask(TaskModel taskModel);
    
    /**
     * 获取一个流程的所有抄送流程的详细信息
     */
    List<TaskModel> getCopyToMessage(ProcessModel processModel);
}
