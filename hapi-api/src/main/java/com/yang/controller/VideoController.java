package com.yang.controller;

import com.yang.enums.VideoStatusEnum;
import com.yang.pojo.Bgm;
import com.yang.pojo.Comments;
import com.yang.pojo.Users;
import com.yang.pojo.Videos;
import com.yang.service.BgmService;
import com.yang.service.VideoService;
import com.yang.utils.FetchVideoCover;
import com.yang.utils.IMoocJSONResult;
import com.yang.utils.MergeVideoMp3;
import com.yang.utils.PagedResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController extends BasicController {

	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideoService videoService;

	@ApiOperation("上传视频")
	@ApiImplicitParams({
			@ApiImplicitParam(name="userId", value="用户id", required=true,
					dataType="string", paramType="form"),
			@ApiImplicitParam(name="bgmId", value="背景音乐id", required=false,
					dataType="string", paramType="form"),
			@ApiImplicitParam(name="videoSeconds", value="背景音乐播放长度", required=true,
					dataType="string", paramType="form"),
			@ApiImplicitParam(name="videoWidth", value="视频宽度", required=true,
					dataType="string", paramType="form"),
			@ApiImplicitParam(name="videoHeight", value="视频高度", required=true,
					dataType="string", paramType="form"),
			@ApiImplicitParam(name="desc", value="视频描述", required=false,
					dataType="string", paramType="form")
	})
	@PostMapping(value = "/uploadVideo", headers = "content-type=multipart/form-data")
	public IMoocJSONResult uploadVideo(String userId,
									   String bgmId,
									   double videoSeconds,
									   int videoHeight,
									   int videoWidth,
									   String desc,
									   @ApiParam(value = "短视频", required = true)
									   MultipartFile files) throws Exception {
		if (StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.errorMsg("用户id不能为空");
		}
		//文件保存的命名空间
//		String fileSpace = "D:\\workspace\\workspace_03\\video_dev";
		//保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		String finalVideoPath = "";
		try {
			if (files != null) {
				String filename = files.getOriginalFilename();
				String fileNamePrefix = filename.split("\\.")[0];
				if (!StringUtils.isEmpty(filename)) {
					// 文件上传的最终保存路径
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + filename;
					// 设置数据库保存的路径
					uploadPathDB += ("/" + filename);
					coverPathDB += "/" + fileNamePrefix + ".jpg";

					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !Objects.requireNonNull(outFile.getParentFile()).isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return IMoocJSONResult.errorMsg("上传出错");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}

		// 判断bgmId是否为空
		if (!StringUtils.isEmpty(bgmId)) {
			Bgm bgm = bgmService.getBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();

			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;
			String VideoOutputName1 = UUID.randomUUID().toString() + ".mp4";
			String VideoOutputName = UUID.randomUUID().toString() + ".mp4";
			String uploadPathDB1 = "/" + userId + "/video" + "/" + VideoOutputName1;
			uploadPathDB = "/" + userId + "/video" + "/" + VideoOutputName;
			String finalVideoPath1 = FILE_SPACE + uploadPathDB1;
			finalVideoPath = FILE_SPACE + uploadPathDB;
			String convertor1 = tool.convertor1(videoInputPath, finalVideoPath1);
			tool.convertor2(convertor1, mp3InputPath, videoSeconds, finalVideoPath);

		}
//		System.out.println("finalVideoPath:" + finalVideoPath);
//		System.out.println("uploadPathDB:" + uploadPathDB);

		//对视频进行截图
		FetchVideoCover fetchVideoCover = new FetchVideoCover(FFMPEG_EXE);
		fetchVideoCover.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

		// 保存视频到数据库
		Videos videos = new Videos();
		videos.setAudioId(bgmId);
		videos.setUserId(userId);
		videos.setVideoSeconds((float)videoSeconds);
		videos.setVideoHeight(videoHeight);
		videos.setVideoWidth(videoWidth);
		videos.setVideoDesc(desc);
		videos.setVideoPath(uploadPathDB);
		videos.setCoverPath(coverPathDB);
		videos.setStatus(VideoStatusEnum.SUCCESS.getValue());
		videos.setCreateTime(new Date());
		String videoId = videoService.saveVideo(videos);

		return IMoocJSONResult.ok(videoId);
	}

	@ApiOperation("上传封面")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "string", paramType = "form"),
			@ApiImplicitParam(name = "videoId", value = "视频ID", required = true, dataType = "string", paramType = "form")
	})
	@PostMapping(value = "/updateVideoCover", headers = "content-type=multipart/form-data")
	public IMoocJSONResult updateVideoCover(String userId, String videoId,
											@ApiParam(value = "短视频封面", required = true)
													MultipartFile files) throws Exception {
		if (StringUtils.isEmpty(videoId) || StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.errorMsg("视频id和userId不能为空");
		}
		//文件保存的命名空间
//		String fileSpace = "D:\\workspace\\workspace_03\\video_dev";
		//保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";


		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		String finalCoverPath = "";
		try {
			if (files != null) {
				String filename = files.getOriginalFilename();

				if (!StringUtils.isEmpty(filename)) {
					// 文件上传的最终保存路径
					finalCoverPath = FILE_SPACE + uploadPathDB + "/" + filename;
					// 设置数据库保存的路径
					uploadPathDB += ("/" + filename);


					File outFile = new File(finalCoverPath);
					if (outFile.getParentFile() != null || !Objects.requireNonNull(outFile.getParentFile()).isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return IMoocJSONResult.errorMsg("上传出错");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}

		videoService.updateVideo(videoId, uploadPathDB);


		return IMoocJSONResult.ok();
	}

	/**
	 *
	 * @Description: 分页和搜索查询视频列表
	 * isSaveRecord：1 - 需要保存
	 * 				 0 - 不需要保存 ，或者为空的时候
	 */
	@PostMapping(value="/showAll")
	public IMoocJSONResult showAll(@RequestBody Videos video,
								   Integer isSaveRecord,
								   Integer page) {
		if (page == null) {
			page = 1;
		}
		PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE);

		return IMoocJSONResult.ok(result);
	}

	@PostMapping(value="/hot")
	public IMoocJSONResult hot() {
		return IMoocJSONResult.ok(videoService.getHotWords());
	}

	@PostMapping(value="/usersLike")
	public IMoocJSONResult usersLike(String userId, String videoId, String videoCreatorId) {
		videoService.userLikeVideo(userId, videoId, videoCreatorId);
		return IMoocJSONResult.ok();
	}

	@PostMapping(value="/usersUnLike")
	public IMoocJSONResult usersUnLike(String userId, String videoId, String videoCreatorId) {
		videoService.userUnLikeVideo(userId, videoId, videoCreatorId);
		return IMoocJSONResult.ok();
	}


	/**
	 * @Description: 我收藏(点赞)过的视频列表
	 */
	@PostMapping("/showMyLike")
	public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

		if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
			return IMoocJSONResult.ok();
		}

		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}

		PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

		return IMoocJSONResult.ok(videosList);
	}

	/**
	 * @Description: 我关注的人发的视频
	 */
	@PostMapping("/showMyFollow")
	public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception {

		if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
			return IMoocJSONResult.ok();
		}

		if (page == null) {
			page = 1;
		}

		int pageSize = 6;

		PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

		return IMoocJSONResult.ok(videosList);
	}

	@PostMapping("/saveComment")
	public IMoocJSONResult saveComment(@RequestBody Comments comments, String fatherCommentId, String toUserId) {
		comments.setFatherCommentId(fatherCommentId);
		comments.setToUserId(toUserId);
		videoService.saveComment(comments);
		return IMoocJSONResult.ok();
	}

	@GetMapping("/getVideoComments")
	public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) {
		if (StringUtils.isEmpty(videoId)) {
			return IMoocJSONResult.errorMsg("视频不能为空");
		}

		if (page == null) {
			page = 1 ;
		}
		if (pageSize == null ) {
			pageSize = 10 ;
		}

		PagedResult list = videoService.getAllComments(videoId, page, pageSize);
		return IMoocJSONResult.ok(list);
	}

}
