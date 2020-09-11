package com.yang.service.impl;

import com.yang.mapper.UsersFansMapper;
import com.yang.mapper.UsersLikeVideosMapper;
import com.yang.mapper.UsersMapper;
import com.yang.mapper.UsersReportMapper;
import com.yang.pojo.Users;
import com.yang.pojo.UsersFans;
import com.yang.pojo.UsersLikeVideos;
import com.yang.pojo.UsersReport;
import com.yang.service.UserService;
import com.yang.utils.IMoocJSONResult;
import com.yang.utils.MD5Utils;
import org.apache.jute.Record;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author yg
 * @date 2020/8/11 20:01
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNameIsExist(String username) {
        Users users = new Users();
        users.setUsername(username);
        Users users1 = usersMapper.selectOne(users);
        return users1 != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNamePasswordIsExist(String username, String password) {
        Users users = new Users();
        users.setUsername(username);
        users.setPassword(password);
        Users users1 = usersMapper.selectOne(users);
        return users1 != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users users) {
        users.setId(sid.nextShort());
        usersMapper.insert(users);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public Users getUser(String username, String password) {
        Users users = new Users();
        users.setUsername(username);
        try {
            users.setPassword(MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Users one = usersMapper.selectOne(users);
        return one;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users users) {
//        Example example = new Example(Users.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id", users.getId());
//        usersMapper.updateByExampleSelective(users, example);
        usersMapper.updateByPrimaryKeySelective(users);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public Users queryUser(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(videoId)) {
            return false;
        }
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        int selectCount = usersLikeVideosMapper.selectCount(usersLikeVideos);
        if (selectCount == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        usersMapper.addFansCount(userId);

        usersMapper.addFollowersCount(fanId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void removeUserFanRelation(String userId, String fanId) {
        UsersFans usersFans = new UsersFans();
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.delete(usersFans);

        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollowersCount(fanId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        UsersFans usersFans = new UsersFans();
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        int count = usersFansMapper.selectCount(usersFans);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport usersReport) {
        usersReport.setId(sid.nextShort());
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }
}
