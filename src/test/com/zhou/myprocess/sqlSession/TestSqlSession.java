package com.zhou.myprocess.sqlSession;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhou.myProcess.process.ProcessConfig;
import com.zhou.myProcess.process.ProcessEngine;
import com.zhou.myProcess.transaction.TransactionSession;
import com.zhou.myProcess.transaction.TransactionSessionFactory;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;


public class TestSqlSession {

    private DataSource dataSource;

    @Before
    public void before(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/process?useUnicode=true&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        this.dataSource = dataSource;
    }

    @Test
    public void testSqlSession() throws Exception {
        ProcessConfig config = new ProcessConfig();
        config.setDataSource(this.dataSource);
        config.setProcessXmlPath("Processes.xml");
        config.setTransactionSessionFactory(new TransactionSessionFactory() {
            @Override
            public TransactionSession openSession() {
                return null;
            }
        });
        ProcessEngine processEngine = new ProcessEngine(config);
    }
}
