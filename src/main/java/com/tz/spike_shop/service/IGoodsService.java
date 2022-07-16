package com.tz.spike_shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tz.spike_shop.pojo.Goods;
import com.tz.spike_shop.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findAllGoodsVo();

    GoodsVo findGoodById(Long id);

}
