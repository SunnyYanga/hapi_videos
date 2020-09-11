package com.yang.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yang.mapper.*;
import com.yang.pojo.Comments;
import com.yang.pojo.SearchRecords;
import com.yang.pojo.UsersLikeVideos;
import com.yang.pojo.Videos;
import com.yang.pojo.vo.CommentsVO;
import com.yang.pojo.vo.VideosVo;
import com.yang.service.VideoService;
import com.yang.utils.PagedResult;
import com.yang.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author yg
 * @date 2020/8/13 9:19
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
        for (CommentsVO c : list) {
            String format = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(format);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos videos) {
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);

        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord,Integer page, Integer pageSize) {
        // 保存热搜词
        String videoDesc = video.getVideoDesc();
        String userId = video.getUserId();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords searchRecords = new SearchRecords();
            String id = sid.nextShort();
            searchRecords.setId(id);
            searchRecords.setContent(videoDesc);
            searchRecordsMapper.insert(searchRecords);
        }

        PageHelper.startPage(page, pageSize);
        List<VideosVo> videosVos = videosMapperCustom.queryAllVideos(videoDesc, userId);

        PageInfo<VideosVo> pageInfo = new PageInfo<>(videosVos);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(videosVos);
        pagedResult.setRecords(pageInfo.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotRecords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreatorId) {
        // 1 保存关联表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);

        // 2 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        // 3 用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreatorId) {
        // 1 删除关联表
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.delete(usersLikeVideos);

        // 2 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        // 3 用户受喜欢数量的累减
        usersMapper.reduceReceiveLikeCount(userId);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comments) {
        comments.setId(sid.nextShort());
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }
}
