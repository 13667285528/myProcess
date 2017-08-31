package com.zhou.myProcess.sqlSession;

import java.util.HashMap;

import com.zhou.myProcess.process.SqlSescribe;

public interface SqlFilter {
    /**
     * 在执行sql之前进行执行的操作
     * @param param 执行Sql时所使用的参数
     * @param sqlSescribe sqlSescribe sql的描述信息，根据sql的描述信息进行一些处理
     */
    public void before(SqlSescribe sqlSescribe, Object param);

    /**
     * 在执行sql执行完成之后的操作
     * @param result 执行完成以后所返回的结果集
     * @param sqlSescribe sqlSescribe sql的描述信息，根据sql的描述信息进行一些处理
     */
    public void after(SqlSescribe sqlSescribe, Object result);
}
