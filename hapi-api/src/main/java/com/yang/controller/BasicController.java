package com.yang.controller;

import com.yang.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

	@Autowired
	public RedisOperator redisOperator;

	public static final String USER_REDIS_SESSION = "user-redis-session";

	// 文件保存命名空间
	public static final String FILE_SPACE = "D:\\workspace\\workspace_03\\video_dev";

	public static final String FFMPEG_EXE = "D:\\apps\\ffmpeg\\bin\\ffmpeg.exe";

	public static final Integer PAGE_SIZE = 5;

}
