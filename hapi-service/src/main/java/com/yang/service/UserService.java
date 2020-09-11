package com.yang.service;

import com.yang.pojo.Users;
import com.yang.pojo.UsersReport;

/**
 * @author yg
 * @date 2020/8/11 19:59
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean queryUserNameIsExist(String username);

    /**
     * 判断用户名密码是否存在
     * @param username
     * @param password
     * @return
     */
    boolean queryUserNamePasswordIsExist(String username, String password);

    /**
     * 保存用户
     * @param users
     */
    void saveUser(Users users);

    /**
     * 查询用户信息
     * @param username
     * @param password
     * @return
     */
    Users getUser(String username, String password);

    /**
     * 修改用户信息
     * @param users
     */
    void updateUserInfo(Users users);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    Users queryUser(String userId);

    /**
     * 查询用户是否喜欢视频
     * @param userId
     * @param videoId
     */
    boolean isUserLikeVideo(String userId, String videoId);

    /**
     *关注
     * @param userId
     * @param fanId
     */
    void saveUserFanRelation(String userId, String fanId);

    /**
     * 取关
     * @param userId
     * @param fanId
     */
    void removeUserFanRelation(String userId, String fanId);

    /**
     * 查询是否是粉丝
     * @return
     */
    boolean queryIfFollow(String userId, String fanId);

    /**
     * 举报视频
     * @param usersReport
     */
    void reportUser(UsersReport usersReport);

}
