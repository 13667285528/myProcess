package com.zhou.myProcess.instance;

public class GroupTaskModel {
	private String taskId;
	private String executorId;
	private String executorName;
	private Integer orderNumber;
	private String groupId;
	private String taskType;
	private String processTypeId;
	private String createTime;
	
	public GroupTaskModel(){
		
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getTaskId() {
		return taskId;
	}


	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	public String getExecutorId() {
		return executorId;
	}

	public void setExecutorId(String executorId) {
		this.executorId = executorId;
	}

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getProcessTypeId() {
		return processTypeId;
	}

	public void setProcessTypeId(String processTypeId) {
		this.processTypeId = processTypeId;
	}
	
	
}
