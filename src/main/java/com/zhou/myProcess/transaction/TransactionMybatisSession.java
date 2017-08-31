package com.zhou.myProcess.transaction;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.zhou.myProcess.instance.Process;

public class TransactionMybatisSession extends TransactionSession{

    /**
     * mybatis的sql会话
     */
    private SqlSession sqlSession;

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public TransactionMybatisSession(SqlSession sqlSession, Map<String, Process> processMessage){
    	super(processMessage);
        this.sqlSession = sqlSession;
    }

    /**
     * 用于获取实例的方法
     *  @param clazz
     * @param <T> 实例的类型clazz
     * @return
     */
    public <T> T getInstance(Class<T> clazz){
        return sqlSession.getMapper(clazz);
    }

    @Override
    public void commit() {
        sqlSession.commit();
    }

    @Override
    public void rollBack() {
        sqlSession.rollback();
    }
}
