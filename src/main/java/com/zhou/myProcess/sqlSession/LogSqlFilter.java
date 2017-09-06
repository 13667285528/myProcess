package com.zhou.myProcess.sqlSession;

import java.lang.reflect.InvocationTargetException;

import com.zhou.myProcess.process.SqlSescribe;
import com.zhou.myProcess.util.Util;

public class LogSqlFilter implements SqlFilter{
	
    @Override
    public void before(SqlSescribe sqlSescribe, Object param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.println("sqlId:" + sqlSescribe.getId() + "  Sql:" + sqlSescribe.getSql());
        System.out.print("params: ");
        for(String paramName : sqlSescribe.getParams()){
        	System.out.print(paramName+": "+Util.invokeGet(param, paramName)+" ; ");
        }
        System.out.println();
    }

    @Override
    public void after(SqlSescribe sqlSescribe, Object result) {
    }
}
