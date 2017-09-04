package com.zhou.myprocess.sqlSession;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhou.myProcess.instance.Process;
import com.zhou.myProcess.instance.ProcessModel;
import com.zhou.myProcess.process.ProcessConfig;
import com.zhou.myProcess.process.ProcessEngine;
import com.zhou.myProcess.transaction.TransactionSession;
import com.zhou.myProcess.transaction.TransactionSessionFactory;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;


public class TestSqlSession {

    private DataSource dataSource;

    private ProcessEngine processEngine;

    @Before
    public void before() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/process?useUnicode=true&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        this.dataSource = dataSource;
        ProcessConfig config = new ProcessConfig();
        config.setDataSource(this.dataSource);
        config.setProcessXmlPath("Processes.xml");
        config.setTransactionSessionFactory(new TransactionSessionFactory() {
            @Override
            public TransactionSession openSession() {
                return null;
            }
        });
        this.processEngine = new ProcessEngine(config);
    }

    @Test
    public void testAddTaskRecord() throws Exception {
        ProcessModel process = new ProcessModel();
        process.setProcessTypeId("001");
        process.setUserId("001");
        this.processEngine.getProcessService().startProcess(process);
    }
}
