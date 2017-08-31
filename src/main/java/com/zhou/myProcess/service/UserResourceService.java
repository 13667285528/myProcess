package com.zhou.myProcess.service;

import java.util.HashMap;

/**
 * 这个接口用于对开发者的用户组操作进行规范
 * 实现这个接口以后，将这个对象注入到processEngine中进行使用
 * 使用时使用ProcessEngine中的getUserResourceService()方法获取该对象。
 */
public interface UserResourceService {
    /**
     * 将所有查询到的用户都添加到用户资源中
     * 如果用户组是空的那么需要返回一个用户组Id
     * @param params 从用户表中查询用户信息的查询参数，其中不包含分页参数
     */
    String addAllUsers(HashMap<String, String> params);

    /**
     * 将单一的用户添加到用户资源中
     * 如果用户组是空的，那么需要返回一个用户组Id
     */
    String addUser(String groupId, String userId);

    /**
     * 删除一个用户
     */
    void deleteUser(String groupId, String userId);

    /**
     * 清空一个用户组
     */
    void cleanUserGroup(String groupId);

    /**
     * 查询用户组
     * 其中包含了分页参数
     */
    void selectUserGroup(HashMap<String, String> params);

    /**
     * 查询用户列表
     * 其中包含了分页参数
     */
    void selectUserList(HashMap<String, String> params);
}
