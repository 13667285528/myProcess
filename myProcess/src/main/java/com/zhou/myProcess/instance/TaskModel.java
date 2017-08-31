package com.zhou.myProcess.instance;

public class TaskModel{
	
	private String uuid;
	private String taskId;
	private String processRecordId;
	private String processId;
	private String examineStatus;
	private String executorId;
	private String executorName;
	private String executorIdea;
	private String taskType;
	private Integer orderNumber;
	private String executorTime;
	private String createTime;
	private String updateTime;
	
	public TaskModel(){
		
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	public String getProcessRecordId() {
		return processRecordId;
	}

	public void setProcessRecordId(String processRecordId) {
		this.processRecordId = processRecordId;
	}

	public String getExamineStatus() {
		return examineStatus;
	}

	public void setExamineStatus(String examineStatus) {
		this.examineStatus = examineStatus;
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

	public String getExecutorIdea() {
		return executorIdea;
	}

	public void setExecutorIdea(String executorIdea) {
		this.executorIdea = executorIdea;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getExecutorTime() {
		return executorTime;
	}

	public void setExecutorTime(String executorTime) {
		this.executorTime = executorTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

}
