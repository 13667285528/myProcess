package com.zhou.myProcess.process;

import com.zhou.myProcess.service.UserResourceService;
import com.zhou.myProcess.sqlSession.SqlFilter;
import com.zhou.myProcess.transaction.TransactionSessionFactory;
import com.zhou.myProcess.util.SendMessage;

import javax.sql.DataSource;
import java.util.List;

/**
 * 流程的初始化信息类，所有的流程初始化信息在此处进行配置
 * 在配置完成后，注入到ProcessInit的构造器中即可
 */
public class ProcessConfig {
    private TransactionSessionFactory transactionSessionFactory;

    private DataSource dataSource;

    private String processXmlPath = "myProcessConfig.xml";

    private UserResourceService userResourceService;

    private String fileSavePath;

    private List<SendMessage> sendMessage;

    private List<SqlFilter> filters;

    public ProcessConfig(){

    }

    public List<SendMessage> getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(List<SendMessage> sendMessage) {
        this.sendMessage = sendMessage;
    }

    public List<SqlFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<SqlFilter> filters) {
        this.filters = filters;
    }

    public TransactionSessionFactory getTransactionSessionFactory() {
        return transactionSessionFactory;
    }

    public void setTransactionSessionFactory(TransactionSessionFactory transactionSessionFactory) {
        this.transactionSessionFactory = transactionSessionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getProcessXmlPath() {
        return processXmlPath;
    }

    public void setProcessXmlPath(String processXmlPath) {
        this.processXmlPath = processXmlPath;
    }

    public UserResourceService getUserResourceService() {
        return userResourceService;
    }

    public void setUserResourceService(UserResourceService userResourceService) {
        this.userResourceService = userResourceService;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }
}
