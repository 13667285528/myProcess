package com.zhou.myProcess.process;

import com.zhou.myProcess.service.FileResourceService;
import com.zhou.myProcess.service.ExecutorService;
import com.zhou.myProcess.service.UserResourceService;
import com.zhou.myProcess.instance.Process;
import com.zhou.myProcess.transaction.TransactionSessionFactory;
import com.zhou.myProcess.util.Util;
import com.zhou.myProcess.xmlParser.ProcessXmlReader;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 流程的执行
 */
public class ProcessEngine {
    /**
     * 流程的所有可配置信息
     */
    private ProcessConfig processConfig;

    /**
     * 从Xml文件中所读取到的流程信息
     */
    private Map<String, Process> processMessage;

    /**
     * 用户资源服务，包含用户的查询，用户资源的添加，插入等
     */
    private UserResourceService userResourceService;

    /**
     * 文件资源服务，文件将会被直接存储到本地硬盘中
     */
    private FileResourceService fileResourceService;

    /**
     * 流程服务，流程服务将会对流程进行增删改查处理
     */
    private ExecutorService processService;

    /**
     * 流程进行流转所使用的数据源配置
     */
    private DataSource dataSource;

    /**
     * 流程配置文件的地址，默认加载资源文件夹下的myProcessConfig.xml文件
     */
    private String processXmlPath = "myProcessConfig.xml";

    /**
     * 使用构造器构造该对象，构造时，需要传入ProcessConifg 配置
     */
    public ProcessEngine(ProcessConfig config) throws Exception {
        this.processConfig = config;
        this.dataSource = config.getDataSource();
        if(Util.isNotEmpty(config.getProcessXmlPath())){
        	this.processXmlPath = config.getProcessXmlPath();
        }
        this.userResourceService = config.getUserResourceService();
        this.processMessage = new ProcessXmlReader(this.processXmlPath).getProcesses();
        this.processService = new ExecutorService(this.dataSource, config.getTransactionSessionFactory(),
                config.getFilters(), config.getSendMessage(), this.processMessage);
        this.fileResourceService = new FileResourceService(config.getFileSavePath());
        CheckTable.checkTable(this.dataSource);
    }

    public ProcessConfig getProcessConfig() {
        return processConfig;
    }

    public Map<String, Process> getProcessMessage() {
        return processMessage;
    }

    public UserResourceService getUserResourceService() {
        return userResourceService;
    }

    public FileResourceService getFileResourceService() {
        return fileResourceService;
    }

    public ExecutorService getProcessService() {
        return processService;
    }

}
