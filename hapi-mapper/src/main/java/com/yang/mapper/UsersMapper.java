package com.yang.mapper;

import com.yang.pojo.Users;
import com.yang.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

    void addReceiveLikeCount(String userId);

    void reduceReceiveLikeCount(String userId);

    void addFansCount(String userId);

    void reduceFansCount(String userId);

    void addFollowersCount(String userId);

    void reduceFollowersCount(String userId);

}