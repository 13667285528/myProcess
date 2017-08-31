package com.zhou.myProcess.instance;

import java.util.List;

public class Process {
	private String typeId;
	
	private String iconUrl;
	
	private String name;
	
	private String formUrl;
	
	private String specialProcess;
	
	private String hasFile;
	
	private String hasUsers;
	
	private List<Transaction> transactions;
	
	private String jsonData;
	
	public Process(){
		
	}

	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public String getSpecialProcess() {
		return specialProcess;
	}

	public void setSpecialProcess(String specialProcess) {
		this.specialProcess = specialProcess;
	}

	public String getHasFile() {
		return hasFile;
	}

	public void setHasFile(String hasFile) {
		this.hasFile = hasFile;
	}

	public String getHasUsers() {
		return hasUsers;
	}

	public void setHasUsers(String hasUsers) {
		this.hasUsers = hasUsers;
	}
	
}
