package com.yang.mapper;

import com.yang.pojo.Comments;
import com.yang.pojo.vo.CommentsVO;
import com.yang.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}