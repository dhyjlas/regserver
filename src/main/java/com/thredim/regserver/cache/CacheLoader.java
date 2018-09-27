package com.thredim.regserver.cache;

import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.repository.RegInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/****************************************************
 * 激活时的验证缓存，打开注释@Component可激活缓存
 * 将/registrar接口方法中的regService.registrar更改为regService.registrar2生效
 * 可提升同时激活数
 * 默认不开启
 ****************************************************/

//@Component
public class CacheLoader implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(CacheLoader.class);

    @Autowired
    private RegInfoRepository regInfoRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<RegInfo> regInfoList = regInfoRepository.findAll();
        for(RegInfo regInfo : regInfoList){
            RegAtom regAtom = new RegAtom(regInfo.getId(), regInfo.getCustomerNo(), regInfo.getPollCode(), regInfo.getRegTotal() - regInfo.getActiveNum());
            RegSyncCache.getInstance().add(regAtom);
        }
        RegSyncCache.getInstance().done();
        log.info("缓存已加载完成");
    }
}
