package com.yang.controller;

import com.yang.pojo.Users;
import com.yang.pojo.UsersReport;
import com.yang.pojo.vo.PublisherVo;
import com.yang.pojo.vo.UsersVo;
import com.yang.service.UserService;
import com.yang.utils.IMoocJSONResult;
import com.yang.utils.MD5Utils;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * @author yg
 * @date 2020/8/11 19:50
 */
@RestController
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;


    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "string", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {
        if (StringUtils.isEmpty(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }
        //文件保存的命名空间
        String fileSpace = "D:\\workspace\\workspace_03\\video_dev";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {
                String filename = files[0].getOriginalFilename();
                if (!StringUtils.isEmpty(filename)) {
                    // 文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + filename;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + filename);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !Objects.requireNonNull(outFile.getParentFile()).isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
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

        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        userService.updateUserInfo(users);
        return IMoocJSONResult.ok(uploadPathDB);
    }

    @GetMapping("/query")
    public IMoocJSONResult query(@RequestParam String userId, @RequestParam String fanId) {
        if (StringUtils.isEmpty(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }
        Users users = userService.queryUser(userId);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(users, usersVo);

        usersVo.setFollow(userService.queryIfFollow(userId, fanId));
        return IMoocJSONResult.ok(usersVo);
    }

    @GetMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(@RequestParam String loginUserId, @RequestParam String videoId, @RequestParam String publisherUserId) {
        if (StringUtils.isEmpty(publisherUserId)) {
            return IMoocJSONResult.errorMsg("数据不能为空");
        }

        // 1. 查询视频发布者的信息
        Users users = userService.queryUser(publisherUserId);
        UsersVo publisher = new UsersVo();
        BeanUtils.copyProperties(users, publisher);

        // 2. 查询和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
        PublisherVo publisherVo = new PublisherVo();
        publisherVo.setPublisher(publisher);
        publisherVo.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(publisherVo);
    }

    @PostMapping("/beYourFans")
    public IMoocJSONResult beYourFans(String userId, String fanId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)) {
            return IMoocJSONResult.errorMsg("数据不能为空");
        }

        userService.saveUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("关注成功");
    }

    @PostMapping("/cancelYourFans")
    public IMoocJSONResult cancelYourFans(String userId, String fanId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)) {
            return IMoocJSONResult.errorMsg("数据不能为空");
        }

        userService.removeUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("取消关注成功");
    }

    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersreport) {
        // 保存举报信息
        userService.reportUser(usersreport);
        return IMoocJSONResult.ok("举报成功");
    }

}
