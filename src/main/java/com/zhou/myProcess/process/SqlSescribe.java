package com.zhou.myProcess.process;

import java.util.LinkedList;
import java.util.List;

/**
 * 这个类用于进行sql语句的信息描述，其中包含有关于sql语句的一系列信息
 */
public class SqlSescribe {
    public String sql;

    public String type;

    public Class paramType;

    public Class resultType;

    public String id;

    public List<String> params = new LinkedList<String>();

    public SqlSescribe(){

    }

    public String getSql() {
        return sql;
    }

    public SqlSescribe setSql(String sql) {
        this.sql = sql.trim();
        takeOutParam(this);
        return this;
    }

    public String getType() {
        return type;
    }

    public SqlSescribe setType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public SqlSescribe setId(String id) {
        this.id = id;
        return this;
    }

    public List<String> getParams() {
        return params;
    }

    public SqlSescribe setParams(List<String> params) {
        this.params = params;
        return this;
    }

    public Class getParamType() {
        return paramType;
    }

    public void setParamType(Class paramType) {
        this.paramType = paramType;
    }

    public Class getResultType() {
        return resultType;
    }

    public void setResultType(Class resultType) {
        this.resultType = resultType;
    }

    /**
     * 从sql语句中参数取出，用 ？代替 并返回参数类型
     */
    private void takeOutParam(SqlSescribe sql){
        for(;true;){
            if(sql.sql == null)
                return ;
            sql.sql = sql.sql.replaceAll("\\t", "  ");
            sql.sql = sql.sql.replaceAll("\\n", "  ");
            int start = sql.sql.indexOf("#{");
            int end = sql.sql.indexOf("}");
            if(start < 0 || end < 0)
                return ;
            String paramName = sql.sql.substring(start+2, end);
            String startStr = sql.sql.substring(0,start);
            String endStr = sql.sql.substring(end+1);
            sql.sql = startStr + "?" + endStr;
            this.params.add(paramName);
        }
    }
}
