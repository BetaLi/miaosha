package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.PromoDOMapper;
import com.miaoshaproject.dataobject.PromoDO;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        // 获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        // DO->model
        PromoModel promoModel = convertFromDO(promoDO);
        if(promoModel==null){
            return null;
        }

        //判断秒杀活动是否即将开始或者正在进行
        DateTime now = new DateTime();
        if(promoModel.getStartDate().isAfter(now)){
            promoModel.setStatus(1);
        }else if(promoModel.getStartDate().isBefore(now)){
            promoModel.setStatus(2);
        }else{
            promoModel.setStatus(3);
        }

        return promoModel;
    }

    private PromoModel convertFromDO(PromoDO promoDO){
        if(promoDO==null){
            return null;
        }

        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        return promoModel;
    }
}
