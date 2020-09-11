package com.yang.mapper;

import com.yang.pojo.Videos;
import com.yang.pojo.vo.VideosVo;
import com.yang.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    List<VideosVo> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);

    void addVideoLikeCount(String videoId);

    void reduceVideoLikeCount(String videoId);

    /**
     * @Description: 查询关注的视频
     */
    List<VideosVo> queryMyFollowVideos(String userId);

    /**
     * @Description: 查询点赞视频
     */
    List<VideosVo> queryMyLikeVideos(@Param("userId") String userId);
}