package com.zhou.myProcess.sqlSession;

import com.zhou.myProcess.process.SqlSescribe;

public class LogSqlFilter implements SqlFilter{
    @Override
    public void before(SqlSescribe sqlSescribe, Object param) {
    	
        System.out.println("sqlId:" + sqlSescribe.getId() + "  Sql:" + sqlSescribe.getSql());
    }

    @Override
    public void after(SqlSescribe sqlSescribe, Object result) {
        System.out.println(sqlSescribe.getSql());
    }
}
