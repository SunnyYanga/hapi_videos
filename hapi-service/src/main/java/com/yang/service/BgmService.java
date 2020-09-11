package com.yang.service;

import com.yang.pojo.Bgm;

import java.util.List;

/**
 * @author yg
 * @date 2020/8/12 15:41
 */
public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    List<Bgm> queryBgmList();

    /**
     * 根据Id查询bgm
     * @param bgmId
     * @return
     */
    Bgm getBgmById(String bgmId);
}
