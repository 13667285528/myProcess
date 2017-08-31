package com.zhou.myProcess.transaction;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class TransactionMybatisSessionFactory extends TransactionSessionFactory {

    private SqlSessionFactory sqlSessionFactory;

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    /**
     * 用于获取事务session的方法
     */
    @Override
    public TransactionSession openSession() {
        SqlSession sqlSession = this.sqlSessionFactory.openSession();
        TransactionMybatisSession transactionMybatisSession = 
        		new TransactionMybatisSession(sqlSession, this.processMessage);
        return transactionMybatisSession;
    }

}
