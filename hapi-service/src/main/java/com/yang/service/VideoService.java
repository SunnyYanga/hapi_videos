package com.yang.service;

import com.yang.pojo.Comments;
import com.yang.pojo.Videos;
import com.yang.utils.PagedResult;

import java.util.List;

/**
 * @author yg
 * @date 2020/8/13 9:18
 */
public interface VideoService {

    /**
     * 留言分页
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);

    /**
     * 保存视频
     * @param videos
     * @return
     */
    String saveVideo(Videos videos);

    /**
     * 修改视频封面
     * @param videoId
     * @param coverPath
     */
    void updateVideo(String videoId, String coverPath);

    /**
     * 分页查询视频列表
     * @param video
     * @param isSaveRecord
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllVideos(Videos video, Integer isSaveRecord,Integer page, Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    List<String> getHotWords();

    /**
     * 用户喜欢视频
     * @param userId
     * @param videoId
     * @param videoCreatorId
     */
    void userLikeVideo(String userId, String videoId, String videoCreatorId);

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     * @param videoCreatorId
     */
    void userUnLikeVideo(String userId, String videoId, String videoCreatorId);


    /**
     * @Description: 查询我喜欢的视频列表
     */
    PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 查询我关注的人的视频列表
     */
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    /**
     * 保存留言
     * @param comments
     */
    void saveComment(Comments comments);
}
