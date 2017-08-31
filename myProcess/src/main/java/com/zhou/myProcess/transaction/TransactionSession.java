package com.zhou.myProcess.transaction;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import com.zhou.myProcess.instance.Process;
import com.zhou.myProcess.instance.Transaction;

public abstract class TransactionSession {
	
	public TransactionSession(Map<String, Process> processMessage){
		this.processMessage = processMessage;
	}
	
	public Map<String, Process> processMessage;
	
    /**
     * 用于获取实例的方法
     *  @param clazz
     * @param <T> 实例的类型clazz
     * @return
     */
    public abstract <T> T getInstance(Class<T> clazz);

    /**
     * 提交方法
     */
    public abstract void commit();

    /**
     * 回滚方法
     */
    public abstract void rollBack();
    
    /**
	 * 执行流程所绑定的事务
	 * @param processType
	 * @param jsonParams
	 * @param string 
     * @throws Exception
	 */
    public void excuteTransaction(String processRecordId,String processType, String jsonParams) throws Exception{
		Map<String, String> mapParams = new Gson().fromJson(jsonParams, HashMap.class);
		mapParams.put("processRecordId", processRecordId);
		Set<String> keys = mapParams.keySet();
		Process process = this.processMessage.get(processType);
		List<Transaction> transactions = process.getTransactions();
		for(Transaction transaction : transactions){
			Class clazz = Class.forName(transaction.getClassId());
			Class paramType = Class.forName(transaction.getParamType());
			Object paramObj = getParamObj(mapParams, paramType);
			Object obj = this.getInstance(clazz);
			Method method = clazz.getMethod(transaction.getMethod(), paramType);
			method.invoke(obj, paramObj);
		}
    }
    
    /**
	 * 从参数Map中取出所需要的参数
	 * @param mapParams
	 * @param paramType
	 * @return
	 */
	private Object getParamObj(Map<String, String> mapParams, Class paramType) throws Exception{
		Object obj = paramType.newInstance();
		Method[] methods = paramType.getMethods();
		for(Method method : methods){
			String methodName = method.getName();
			if(methodName.substring(0, 3).equals("set") || method.getParameterTypes().length == 1){
				String paramName = methodName.substring(3).substring(0,1).toLowerCase() + methodName.substring(4);
				Class type = method.getParameterTypes()[0];
				if(mapParams.get(paramName) == null){
					continue ;
				}
				if(type.isAssignableFrom(String.class)){
					method.invoke(obj, mapParams.get(paramName));
				}else if(type.isAssignableFrom(Date.class)){
					method.invoke(obj, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mapParams.get(paramName)));
				}else if(type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)){
					method.invoke(obj, Integer.parseInt(mapParams.get(paramName)));
				}else if(type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)){
					method.invoke(obj, Double.parseDouble(mapParams.get(paramName)));
				}else if(type.isAssignableFrom(boolean.class)){
					method.invoke(obj, Boolean.parseBoolean(mapParams.get(paramName)));
				}else if(type.isAssignableFrom(BigDecimal.class)){
					method.invoke(obj, new BigDecimal(mapParams.get(paramName)));
				}else{
					throw new RuntimeException("未知的类型："+type.toString());
				}
			}
		}
		return obj;
	}
    
}
