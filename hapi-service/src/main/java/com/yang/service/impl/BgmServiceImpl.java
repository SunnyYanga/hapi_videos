package com.yang.service.impl;

import com.yang.mapper.BgmMapper;
import com.yang.pojo.Bgm;
import com.yang.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yg
 * @date 2020/8/12 15:41
 */
@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm getBgmById(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}
