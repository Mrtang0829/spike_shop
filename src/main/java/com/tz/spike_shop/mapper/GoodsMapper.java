package com.tz.spike_shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tz.spike_shop.pojo.Goods;
import com.tz.spike_shop.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findAllGoodsVo();

    GoodsVo findGoodById(Long id);
}
