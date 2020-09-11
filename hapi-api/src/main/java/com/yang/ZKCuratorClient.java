package com.yang;

import com.yang.config.ResourceConfig;
import com.yang.enums.BGMOperatorTypeEnum;
import com.yang.service.BgmService;
import com.yang.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author yg
 * @date 2020/8/18 16:28
 */
@Component
public class ZKCuratorClient {

//    @Autowired
//    private BgmService bgmService;

    @Autowired
    private ResourceConfig resourceConfig;

    private CuratorFramework client = null;

    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);




    public void init() {
        if (client != null) {
            return;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 5);
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("admin")
                .build();
        client.start();

        try {
//            String  s = new String(client.getData().forPath("/bgm/200818BGBKYK15KP"));
//            log.info("s:{}" + s);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
//                    System.out.println("监听到事件：CHILD_ADDED");
                    // 1 从数据库查询bgm对象， 获取路径path
                    String path = pathChildrenCacheEvent.getData().getPath();
                    String type = new String(pathChildrenCacheEvent.getData().getData(), "utf-8");
                    Map<String, String> map = JsonUtils.jsonToPojo(type, Map.class);
                    String operType = map.get("opertype");
                    String bgmPath = map.get("path");

//                    String[] split = path.split("/");
//                    String bgmId = split[split.length - 1];


//                    Bgm bgmById = bgmService.getBgmById(bgmId);
//                    if (bgmById == null) {
//                        return;
//                    }

//                    String bgmPath = bgmById.getPath();
                    // 2 定义保存到本地的bgm路径
                    String filePath = resourceConfig.getFileSpace() + bgmPath;

                    // 3 定义下载的路径(播放url)
                    String arrPath[] = bgmPath.split("\\\\");
                    String finalPath = "";
                    for (int i = 0; i < arrPath.length; i++) {
                        if (!StringUtils.isEmpty(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8");
                        }
                    }

                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;

                    // 下载bgm到springboot服务器
                    if (operType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                    } else if(operType.equals(BGMOperatorTypeEnum.DELETE.type)) {
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });

    }
}
