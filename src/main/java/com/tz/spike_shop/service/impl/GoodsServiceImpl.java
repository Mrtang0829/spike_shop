package com.tz.spike_shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tz.spike_shop.mapper.GoodsMapper;
import com.tz.spike_shop.pojo.Goods;
import com.tz.spike_shop.service.IGoodsService;
import com.tz.spike_shop.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findAllGoodsVo() {
        return goodsMapper.findAllGoodsVo();
    }

    @Override
    public GoodsVo findGoodById(Long id) {
        return goodsMapper.findGoodById(id);
    }
}
