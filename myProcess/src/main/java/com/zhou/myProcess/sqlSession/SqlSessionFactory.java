package com.zhou.myProcess.sqlSession;

import com.zhou.myProcess.exception.LoadReflectionException;
import com.zhou.myProcess.process.SqlMapper;
import com.zhou.myProcess.util.Util;
import com.zhou.myProcess.xmlParser.SqlXmlReader;
import org.dom4j.DocumentException;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于创建sql会话工厂的接口
 * 使用指定的数据源来创建会话工厂
 */
public class SqlSessionFactory {
    /**
     * 用来存放sql描述的对象
     */
    private Map<Class, SqlMapper> sqlMapperMap = new HashMap<Class, SqlMapper>();

    /**
     * Sql处理拦截器
     */
    private List<SqlFilter> filters;

    /**
     * 必须存在的Log拦截器
     */
    private SqlFilter logSqlFilter = new LogSqlFilter();

    /**
     * 提供进行外部设置的数据源
     */
    private DataSource dataSource;

    public Map<Class, SqlMapper> getSqlMapperMap() {
        return sqlMapperMap;
    }

    public List<SqlFilter> getFilters() {
        return filters;
    }

    public SqlFilter getLogSqlFilter() {
        return logSqlFilter;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public SqlSessionFactory(DataSource dataSource, String scanSqlXmlPackage, String endWith, List<SqlFilter> filters) throws Exception {
        this.dataSource = dataSource;
        List<String> sqlXmlPath = Util.getXmlPathFromPackage(scanSqlXmlPackage, endWith);
        for(String sqlXml : sqlXmlPath){
            SqlXmlReader reader = new SqlXmlReader(sqlXml);
            SqlMapper sqlMapper = reader.readSqlXml();
            if(this.sqlMapperMap.containsKey(sqlMapper.getInstance())){
            	throw new LoadReflectionException("出现重复的映射："+sqlMapper.getInstance().toString());
            }
            this.sqlMapperMap.put(sqlMapper.getInstance(), sqlMapper);
        }
    }

    /**
     * 开启一个新的sql会话，使用工厂类中所定义的成员属性DataSource
     * @return
     */
    public SqlSession openSession() throws SQLException {
        Connection conn = this.dataSource.getConnection();
        conn.setAutoCommit(false);
        SqlSession sqlsession = new SqlSession(conn);
        sqlsession.sqlMapperMap = this.sqlMapperMap;
        if(this.filters == null)
            this.filters = new LinkedList<SqlFilter>();
        if(!this.filters.contains(this.logSqlFilter))
            this.filters.add(this.logSqlFilter);
        sqlsession.filters = this.filters;
        return sqlsession;
    }
}
