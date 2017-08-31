package com.zhou.myProcess.service;

import com.google.gson.Gson;
import com.zhou.myProcess.instance.GroupTaskModel;
import com.zhou.myProcess.instance.Process;
import com.zhou.myProcess.instance.ProcessModel;
import com.zhou.myProcess.instance.TaskModel;
import com.zhou.myProcess.repository.ProcessExcuter;
import com.zhou.myProcess.repository.ProcessManage;
import com.zhou.myProcess.sqlSession.SqlFilter;
import com.zhou.myProcess.sqlSession.SqlSession;
import com.zhou.myProcess.sqlSession.SqlSessionFactory;
import com.zhou.myProcess.transaction.TransactionSession;
import com.zhou.myProcess.transaction.TransactionSessionFactory;
import com.zhou.myProcess.util.ProcessConstant;
import com.zhou.myProcess.util.SendMessage;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ExecutorService {

    /**
     * Sql会话工厂，用于创建Sql会话
     */
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 用于在事务执行完成后开启事务处理会话的事务会话工厂
     */
    private TransactionSessionFactory transactionSessionFactory;

    /**
     * 流程运行时进行消息发送的接口
     */
    private List<SendMessage> sendMessages;

    /**
     * 用于打印Sql日志的类
     */
    private List<SqlFilter> filters;
    
    /**
     * 流程相关信息
     */
    private Map<String, Process> processes;
    
    /**
     * 创建流程服务的构造器，创建时，必须有指定的数据源
     * @param dataSource
     */
    public ExecutorService(DataSource dataSource, TransactionSessionFactory transactionSessionFactory, 
    		List<SqlFilter> filters, List<SendMessage> sendMessages, Map<String, Process> processes)
            throws Exception {
        this.sendMessages = sendMessages;
        this.processes = processes;
        sqlSessionFactory = new SqlSessionFactory(dataSource, "com.zhou.myProcess.sql", "Sql.xml", filters);
        this.transactionSessionFactory = transactionSessionFactory;
        this.transactionSessionFactory.setProcessMessage(processes);
    }
    
    /**
	 * 用户进行开始流程
	 * 1.需要在processRecord表中添加userId字段
	 * 2.完成查询方法，该方法查询的目标为未进行修改的刚添加的任务信息
	 * 3.查询结果出来以后，根据这些Id来逐个修改任务对象信息
	 * @throws Exception
	 */
	public void startProcess(ProcessModel processModel) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessExcuter processExcuter = sqlSession.getInstance(ProcessExcuter.class);
		try{
			//设定流程的Id
			processModel.setProcessRecordId(processExcuter.getuuid());
			//添加流程的记录与 流程任务记录
			processExcuter.insertProcessRecord(processModel);
			processExcuter.insertProcessTaskRecord(processModel);
			//激活第一个任务
			TaskModel firstTask = processExcuter.getFirstTask(processModel);
			if(firstTask == null)
				firstTask = processExcuter.getFirstGroupTask(processModel);
			firstTask.setTaskId(processExcuter.getuuid());
			firstTask.setProcessRecordId(processModel.getProcessRecordId());
			processExcuter.addFirstTask(firstTask);
			
			//填充流程中的信息
			Process process = this.processes.get(processModel.getProcessTypeId());
			processModel.setFormUrl(process.getFormUrl());
			processModel.setProcessTypeName(process.getName());
			processModel.setUserName(
					new Gson().fromJson(processModel.getJsonData(), Map.class).get("userName").toString());
			if(sendMessages != null){
				for(SendMessage messageEngine : sendMessages){
					messageEngine.sendNextMessage(processModel, firstTask);
				}
			}
			sqlSession.commit();
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}

	/**
	 * 用户进行执行任务
	 * @throws Exception 
	 */
	public void excuteTask(TaskModel taskModel) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessExcuter processExcuter = sqlSession.getInstance(ProcessExcuter.class);
		try{
			ProcessModel process = processExcuter.selectProcessByTaskId(taskModel);
			if(process == null){
				throw new RuntimeException("结束流程异常，未根据任务Id找到流程，任务Id："+taskModel.getTaskId());
			}
			process.setProcessTypeName(
					this.processes.get(process.getProcessTypeId()).getName());
			processExcuter.excuteUserTask(taskModel);
			//审批人驳回了流程，则结束流程
			if(taskModel.getExamineStatus().equals("3")){
				process.setExecuteStatus("3");
				processExcuter.updateProcessStatus(process);
				if(this.sendMessages != null){
					for(SendMessage sendMessage : this.sendMessages){
						sendMessage.sendEndMessage(process, null);
					}
				}
				return ;
			}
			TaskModel nextTask = processExcuter.selectNextTask(taskModel);
			//流程审批结束了，已经不存在下一个流程的时候执行
			if(nextTask == null){
				processExcuter.excuteCopyToTask(process);
				process.setExecuteStatus("2");
				processExcuter.updateProcessStatus(process);
				TransactionSession transactionSession = this.transactionSessionFactory.openSession();
				transactionSession.excuteTransaction(process.getProcessRecordId(),process.getProcessTypeId(), process.getJsonData());
				List<TaskModel> copyToTasks = processExcuter.getCopyToMessage(process);
				if(this.sendMessages != null){
					for(SendMessage sendMessage : this.sendMessages){
						sendMessage.sendEndMessage(process, copyToTasks);
					}
				}
			}else{
				//流程审批通过，继续执行下一个任务
				processExcuter.activitiTask(nextTask);
				if(this.sendMessages != null){
					for(SendMessage sendMessage : this.sendMessages){
						sendMessage.sendNextMessage(process,nextTask);
					}
				}
			}
			sqlSession.commit();
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}
	
	/**
	 * 用户撤回流程
	 * @throws Exception 
	 */
	public void withdrawProcess(ProcessModel process) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessExcuter processExcuter = sqlSession.getInstance(ProcessExcuter.class);
		try{
			process.setExecuteStatus("4");
			processExcuter.updateProcessStatus(process);
			processExcuter.sleepTask(process);
			sqlSession.commit();
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}
	
	/**
	 * 删除某流程的一个任务节点
	 * @throws Exception 
	 */
	public void deleteTask(TaskModel task, String taskType) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessExcuter processExcuter = sqlSession.getInstance(ProcessExcuter.class);
		try{
			if(taskType.equals(ProcessConstant.USER_TASK)){
				task = processExcuter.getTaskByTaskId(task);
				processExcuter.updateAfterTask(task);
				processExcuter.deleteTask(task);
			}else if(taskType.equals(ProcessConstant.COPY_TO_TASK)){
				processExcuter.deleteTask(task);
			}
			sqlSession.commit();
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}
	
	/**
	 * 添加某流程的一个任务节点
	 * @param task 需要存在的参数为 执行人的姓名，id，
	 * @param process 需要流程所有人的userId和流程类型typeId
	 * @throws Exception
	 */
	public String addTask(TaskModel task,ProcessModel process, String taskType) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessExcuter processExcuter = sqlSession.getInstance(ProcessExcuter.class);
		try{
			ProcessModel thisProcess = processExcuter.checkUserProcess(process);
			if(thisProcess == null){
				process.setFormUrl(this.processes.get(process.getProcessTypeId()).getFormUrl());
				processExcuter.addMainProcess(process);
				thisProcess = processExcuter.checkUserProcess(process);
			}
			if(thisProcess == null){
				throw new RuntimeException("创建用户的主流程信息失败");
			}
			String uuid = processExcuter.getuuid();
			task.setTaskId(uuid);
			task.setProcessId(thisProcess.getProcessId());
			if(taskType.equals(ProcessConstant.USER_TASK)){
				task.setTaskType(ProcessConstant.USER_TASK);
				processExcuter.addTask(task);
			}else if(taskType.equals(ProcessConstant.COPY_TO_TASK)){
				task.setTaskType(ProcessConstant.COPY_TO_TASK);
				processExcuter.addCopyToTask(task);
			}
			sqlSession.commit();
			return uuid;
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}
	
	
	/**
	 * 删除用户组的某流程的一个任务节点
	 * @throws Exception 
	 */
	public void deleteGroupTask(GroupTaskModel task, String taskType) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessManage processManage = sqlSession.getInstance(ProcessManage.class);
		try{
			if(taskType.equals(ProcessConstant.USER_TASK)){
				task = processManage.getGroupTaskByTaskId(task);
				processManage.updateAfterGroupTask(task);
				processManage.deleteGroupTask(task);
			}else if(taskType.equals(ProcessConstant.COPY_TO_TASK)){
				processManage.deleteGroupTask(task);
			}
			sqlSession.commit();
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}
	
	/**
	 * 添加用户组的某流程的一个任务节点
	 * @param task 需要存在的参数为 执行人的姓名，id，
	 * @throws Exception
	 */
	public String addGroupTask(GroupTaskModel task, String taskType) throws Exception{
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ProcessManage processManage = sqlSession.getInstance(ProcessManage.class);
		try{
			String taskId = processManage.getuuid();
			task.setTaskId(taskId);
			if(taskType.equals(ProcessConstant.USER_TASK)){
				task.setTaskType(ProcessConstant.USER_TASK);
				processManage.addGroupTask(task);
			}else if(taskType.equals(ProcessConstant.COPY_TO_TASK)){
				task.setTaskType(ProcessConstant.COPY_TO_TASK);
				processManage.addGroupCopyToTask(task);
			}
			sqlSession.commit();
			return taskId;
		}catch(Exception e){
			if(sqlSession != null)
				sqlSession.rollback();
			throw e;
		}
	}
	
}