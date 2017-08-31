package com.zhou.myProcess.transaction;

/**
 * 事务的绑定当流程结束时所要进行执行的事务处理
 */
public class TransactionEngine {
    /**
     * 用于开启事务会话的接口，在本框架中已经有了基于Mybatis的实现类
     */
    private TransactionSessionFactory sessionFactory;

    TransactionEngine(TransactionSessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }




}
