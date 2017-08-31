package com.zhou.myProcess.process;

import java.util.Map;

public class SqlMapper {
    private Class instance;

    private Map<String, SqlSescribe> map;

    public SqlMapper(){

    }

    public Class getInstance() {
        return instance;
    }

    public void setInstance(Class instance) {
        this.instance = instance;
    }

    public Map<String, SqlSescribe> getMap() {
        return map;
    }

    public void setMap(Map<String, SqlSescribe> map) {
        this.map = map;
    }
}
