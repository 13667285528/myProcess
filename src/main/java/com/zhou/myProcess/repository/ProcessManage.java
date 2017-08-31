package com.zhou.myProcess.repository;

import com.zhou.myProcess.instance.GroupTaskModel;

public interface ProcessManage {
	/**
	 * 获取UUid
	 * @return
	 */
	String getuuid();
	
	/**
	 * 添加任务节点
	 */
	boolean addGroupTask(GroupTaskModel task);
	
	/**
	 * 添加抄送任务节点
	 */
	boolean addGroupCopyToTask(GroupTaskModel task);
	
	/**
	 * 根据用户组流程任务的Id，获取这个流程的信息
	 */
	GroupTaskModel getGroupTaskByTaskId(GroupTaskModel task);
	
	/**
	 * 修改后续的用户组的Task任务的orderNumber
	 */
	boolean updateAfterGroupTask(GroupTaskModel task);
	
	/**
	 * 删除一个任务
	 */
	boolean deleteGroupTask(GroupTaskModel task);
}
