package com.zhou.myProcess.xmlParser;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zhou.myProcess.instance.Process;
import com.zhou.myProcess.instance.Transaction;

public class ProcessXmlReader extends XmlReader{
    public ProcessXmlReader(String xmlPath) throws DocumentException {
        super(xmlPath);
    }

    /**
     * 从xml中读取流程的相关信息
     * @return
     */
    public Map<String, Process> getProcesses(){
        Map<String, Process> processes = new HashMap<String, Process>();
        Element rootElement = this.document.getRootElement();
        List<Element> elements = rootElement.elements("process");
        for(Element processElement : elements){
            Process process = this.getProcess(processElement);
            if(processes.containsKey(process.getTypeId())){
                throw new RuntimeException("发现相同typeId的流程,typeId="+process.getTypeId());
            }
            processes.put(process.getTypeId(), process);
        }
        return processes;
    }

    private Process getProcess(Element processElement){
        Process process = new Process();
        process.setFormUrl(processElement.elementText("formUrl"));
        process.setTypeId(processElement.attributeValue("typeId"));
        process.setName(processElement.elementText("name"));
        process.setIconUrl(processElement.elementText("iconUrl"));
        process.setSpecialProcess(processElement.elementText("specialProcess"));
        process.setHasFile(processElement.elementText("hasFile"));
        process.setHasUsers(processElement.elementText("hasUsers"));
        List<Transaction> transactions =  new LinkedList<Transaction>();

        List<Element> transactionsEle = processElement.element("transactions").elements("transaction");
        for(Element transaction : transactionsEle){
            transactions.add(getTransction(transaction));
        }
        process.setTransactions(transactions);
        return process;
    }

    private Transaction getTransction(Element transactionElement){
        Transaction transaction = new Transaction();
        transaction.setClassId(transactionElement.attributeValue("classId"));
        transaction.setMethod(transactionElement.attributeValue("method"));
        transaction.setName(transactionElement.attributeValue("name"));
        transaction.setParamType(transactionElement.attributeValue("paramType"));
        return transaction;
    }

}
