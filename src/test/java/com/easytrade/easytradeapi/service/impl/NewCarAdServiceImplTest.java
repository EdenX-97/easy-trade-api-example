/**
 * @author: Hongzhang Liu
 * @description 新车广告服务测试
 * @date 14/4/2022 4:04 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.easytrade.easytradeapi.constant.enums.AdStatusEnum;
import com.easytrade.easytradeapi.model.NewCarAd;
import com.easytrade.easytradeapi.repository.NewCarAdRepository;
import com.easytrade.easytradeapi.service.intf.NewCarAdService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class NewCarAdServiceImplTest {

    @Mock
    private NewCarAdRepository newCarAdRepository;

    @Mock
    private NewCarAdService newCarAdService;

    @Test
    public void getPostedNewCarAds(){
        NewCarAd newCarAd1 = new NewCarAd();
        newCarAd1.setAdStatus(AdStatusEnum.POSTED);
        newCarAd1.setModel("Audi");
        List<NewCarAd> ans = new ArrayList<>();
        ans.add(newCarAd1);
        Mockito.when(newCarAdService.getPostedNewCarAds()).thenReturn(ans);
        Assert.assertEquals("Audi", newCarAdService.getPostedNewCarAds().get(0).getModel());
    }
}
