package com.zhou.myProcess.sqlSession;

import com.zhou.myProcess.exception.IllegalityParamException;
import com.zhou.myProcess.exception.IllegalityResultException;
import com.zhou.myProcess.exception.NotFountProxyException;
import com.zhou.myProcess.exception.SqlResultException;
import com.zhou.myProcess.process.SqlMapper;
import com.zhou.myProcess.process.SqlSescribe;
import com.zhou.myProcess.util.Util;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * sql会话管理的类
 * 这个类用于保持事务的一致性、
 * 每次所开启的sqlSession均采用一个独立的Connection
 */
public class SqlSession implements MethodInterceptor {

    public Connection conn;

    /**
     * 用于创建SqlSession的构造器，使用的为默认的连接
     * @param conn
     */
    public SqlSession(Connection conn){
        this.conn = conn;
    }

    private Enhancer enhancer = new Enhancer();

    private BeanGenerator beanGenerator = new BeanGenerator();

    /**
     * 私有化的一个sqlMapperMap，声明仅为便于引用
     */
    protected Map<Class, SqlMapper> sqlMapperMap;

    /**
     * 私有化的一个Sql拦截器
     */
    protected List<SqlFilter> filters;

    /**
     * 获取持久层实例
     * @param clazz 所需要获取的实例的类型
     */
    public <T> T getInstance(Class<T> clazz) throws NotFountProxyException {
        if(!this.sqlMapperMap.containsKey(clazz)){
            throw new NotFountProxyException("无效的类，没有找到指定的instance："+clazz.getName());
        }
        //设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        //通过字节码技术动态创建子类实例
        return (T) enhancer.create();

    }


    public void rollback() throws SQLException {
        try{
            if(conn != null){
                conn.rollback();
            }
        }finally {
            if(conn != null)
                conn.close();
        }
        this.conn = null;
    }

    public void commit() throws SQLException {
        try{
            if(conn != null){
                conn.commit();
            }
        }finally {
            if(conn != null)
                conn.close();
            this.conn = null;
        }
    }

    /**
     * cglib进行的自动实现的规则
     * @param o
     * @param method
     * @param objects
     * @param methodProxy
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String className = o.getClass().getName();
        className = className.substring(0,className.indexOf("$"));
        SqlMapper sqlMapper =  this.sqlMapperMap.get(Class.forName(className));
        SqlSescribe sqlSescribe = sqlMapper.getMap().get(method.getName());
        this.checkParamAndResult(method, sqlSescribe);
        if(this.filters != null){
            for(SqlFilter filter : this.filters){
                if(Util.isNotEmpty(objects)){
                    filter.before(sqlSescribe, objects[0]);
                }
            }
        }
        Object result = executeSql(sqlSescribe, Util.isNotEmpty(objects) ? objects[0] : null, method);
        if(this.filters != null){
            for(SqlFilter filter : this.filters){
                filter.after(sqlSescribe, result);
            }
        }
        return result;
    }

    /**
     * 验证 这个方法的传入参数与返回类型是否符合Sql描述
     * @param method
     * @param sqlSescribe
     */
    private void checkParamAndResult(Method method, SqlSescribe sqlSescribe) throws IllegalityParamException, IllegalityResultException {
        if(method.getParameterTypes().length > 1 )
            throw new IllegalityParamException("方法"+method.getName()+"中最多只可以传入一个参数");
        if(method.getParameterTypes().length > 0 && !method.getParameterTypes()[0].equals(sqlSescribe.getParamType()))
            throw new IllegalityParamException("方法"+method.getName()+"与paramType："+sqlSescribe.getParamType()+"不符");
        if(sqlSescribe.getType().equals("select")){
            if(method.getReturnType().isAssignableFrom(List.class)){
                Type returnType = method.getGenericReturnType();
                if(returnType instanceof ParameterizedType){
                    Type[] typesto = ((ParameterizedType) returnType).getActualTypeArguments();// 强制转型为带参数的泛型类型，
                    if(typesto.length > 0 && !typesto[0].equals(sqlSescribe.getResultType()))
                        throw new IllegalityResultException("方法"+method.getName()+"具有错误的返回类型："+typesto[0].getTypeName());
                }
            }else{
                if(sqlSescribe.getResultType() != null && !sqlSescribe.getResultType().equals(method.getReturnType()))
                    throw new IllegalityResultException("方法"+method.getName()+"没有使用指定的返回值类型");
            }
        }
    }

    /**
     * 执行一个指定的sql语句并携带部分参数
     * @return
     * @throws Exception
     */
    private <T> T executeSql(SqlSescribe sql, Object params,Method method) throws Exception{
        Connection conn = this.conn;
        String paramsStr = null;
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(sql.sql);
            for(int i = 1; i<= sql.params.size() ; i++){
                ps.setObject(i, Util.invokeGet(params, sql.params.get(i-1)));
                paramsStr += sql.params.get(i-1)+",";
            }
            Class<T> resultType = sql.getResultType();
            if(sql.type.equals("select")){
                return excuteSelect(ps, resultType, method);
            }else{
                return excuteUpdate(ps, (Class<T>) method.getReturnType());
            }
        }finally{
            if(ps != null)
                ps.close();
        }
    }

    public <T> T excuteUpdate(PreparedStatement ps, Class<T> returnType) throws SQLException, SqlResultException {
        Integer updateRows = ps.executeUpdate();
        if(returnType.isAssignableFrom(Integer.class) || returnType.isAssignableFrom(int.class)){
            return (T) updateRows;
        }else if(returnType.isAssignableFrom(Boolean.class) || returnType.isAssignableFrom(boolean.class)){
            if(updateRows > 0){
                return (T) new Boolean(true);
            }else{
                return (T) new Boolean(false);
            }
        }else{
            throw new SqlResultException("执行了修改操作，返回值为不支持的返回值");
        }
    }

    public <T, V> T excuteSelect(PreparedStatement ps, Class<T> resultType, Method method)
            throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, SqlResultException {
        ResultSet resultSet = ps.executeQuery();
        try{
            if(method.getReturnType().isAssignableFrom(List.class)){
                List result = new LinkedList();
                while(resultSet.next()){
                    ResultSetMetaData columns = resultSet.getMetaData();
                    V model = (V) resultType.newInstance();
                    for(int i = 1; i <= columns.getColumnCount() ; i++ ){
                        String columnName = columns.getColumnLabel(i);
                        Util.invokeSet(model, columnName,resultSet.getObject(columnName));
                    }
                    result.add(model);
                }
                return (T) result;
            }else if(method.getReturnType().isAssignableFrom(String.class)){
            	if(resultSet.next()){
            		String str = resultSet.getString(1);
                    if(resultSet.next())
                        throw new SqlResultException("存在多个结果集，但是返回类型为String");
                    else
                        return (T) str;
                }else{
                    return null;
                }
            }else if(method.getReturnType().isAssignableFrom(Integer.class) 
            		|| method.getReturnType().isAssignableFrom(int.class)){
            	if(resultSet.next()){
                    Integer count = resultSet.getInt(1);
                    if(resultSet.next())
                        throw new SqlResultException("存在多个结果集，但是返回类型为Integer");
                    else
                        return (T) count;
                }else{
                    return null;
                }
            }else{
                if(resultSet.next()){
                    ResultSetMetaData columns = resultSet.getMetaData();
                    T model = (T) resultType.newInstance();
                    for(int i = 1; i <= columns.getColumnCount() ; i++ ){
                        String columnName = columns.getColumnLabel(i);
                        Util.invokeSet(model, columnName,resultSet.getObject(columnName));
                    }
                    if(resultSet.next())
                        throw new SqlResultException("存在多个结果集，但是返回类型不为List");
                    else
                        return model;
                }else{
                    return null;
                }
            }
        }finally{
            if(resultSet != null){
                resultSet.close();
            }
        }
    }
}
