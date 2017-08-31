package com.zhou.myProcess.exception;

public class SqlResultException extends Exception {



    SqlResultException(){
        super();
    }

    public SqlResultException(String msg) {
        super(msg);
    }
}
