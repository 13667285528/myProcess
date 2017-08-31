package com.zhou.myProcess.transaction;

import java.util.Map;

import com.zhou.myProcess.instance.Process;

public abstract class TransactionSessionFactory {
	
	public TransactionSessionFactory(){
		
	}
	
	public Map<String, Process> processMessage;
	
    public Map<String, Process> getProcessMessage() {
		return processMessage;
	}

	public void setProcessMessage(Map<String, Process> processMessage) {
		this.processMessage = processMessage;
	}
	
	/**
     * 启动sql会话所使用的方法
     */
    public abstract TransactionSession openSession();
}
