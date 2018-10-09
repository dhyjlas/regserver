package com.thredim.regserver.service;

import com.alibaba.fastjson.JSON;
import com.thredim.regserver.cache.RegAtom;
import com.thredim.regserver.cache.RegSyncCache;
import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.entity.RegList;
import com.thredim.regserver.exception.BusinessException;
import com.thredim.regserver.repository.RegInfoRepository;
import com.thredim.regserver.repository.RegListRepository;
import com.thredim.regserver.utils.RSAUtils2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RegService {
    private static final Logger log = LoggerFactory.getLogger(RegService.class);

    @Autowired
    private RegInfoRepository regInfoRepository;

    @Autowired
    private RegListRepository regListRepository;

    @Value("${ras.privateKey:-1}")
    private String privateKey;

    /**
     * 接口注册
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public boolean registrar(String data) throws Exception{
        String json = RSAUtils2.privateDecrypt(data, RSAUtils2.getPrivateKey(privateKey));

        log.info(json);

        Map<String, String> dateInfo = (Map<String, String>) JSON.parse(json);
        String pollCode = dateInfo.get("pollCode");
        String equipmentId = dateInfo.get("equipmentId");
        String customerNo = dateInfo.get("customerNo");

        if(StringUtils.isEmpty(equipmentId))
            throw new BusinessException("设备号不能为空");

        //同步注册码使用次数
        synchronized (RegService.class){
            //检测设备是否已注册
            //对于已经注册的设备，直接
            List<RegList> regLists = regListRepository.findAllByPollCodeAndEquipmentId(pollCode, equipmentId);
            if(regLists.size() > 0) {
                regLists.get(0).setLastRegTime(new Date());
                return true;
            }

            //检测注册码是否正确
            List<RegInfo> regInfos = regInfoRepository.findAllByCustomerNoAndPollCode(customerNo, pollCode);
            if(regInfos.size() < 1)
                throw new BusinessException("激活码不正确");

            RegInfo regInfo = regInfos.get(0);

            int regTotal = regInfo.getRegTotal();
            int activeNum = regInfo.getActiveNum();
            if(regTotal <= activeNum)
                throw new BusinessException("激活码使用次数已达上线");
            regInfoRepository.addActiveNumFromRegInfoById(regInfo.getId());

            //注册设备
            RegList regList = new RegList();
            regList.setCustomerNo(customerNo);
            regList.setEquipmentId(equipmentId);
            regList.setPollCode(pollCode);
            regList.setFirstRegTime(new Date());
            regListRepository.saveAndFlush(regList);
        }

        return true;
    }

    /**
     * 接口注册
     * 同步缓存
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public boolean registrar2(String data) throws Exception{
        String json = RSAUtils2.privateDecrypt(data, RSAUtils2.getPrivateKey(privateKey));

        log.info(json);

        Map<String, String> dateInfo = (Map<String, String>) JSON.parse(json);
        String pollCode = dateInfo.get("pollCode");
        String equipmentId = dateInfo.get("equipmentId");
        String customerNo = dateInfo.get("customerNo");

        if(StringUtils.isEmpty(equipmentId))
            throw new BusinessException("设备号不能为空");

        RegAtom regAtom = RegSyncCache.getInstance().get(pollCode);
        if(regAtom == null){
            throw new BusinessException("激活码不正确");
        }

        if(!regAtom.getCustomerNo().equals(customerNo)){
            throw new BusinessException("激活码与客户号不匹配");
        }

        if(!regAtom.checkEquipment(equipmentId)){
            //设备号已经在缓存中
            List<RegList> regLists = regListRepository.findAllByPollCodeAndEquipmentId(pollCode, equipmentId);
            if(regLists.size() > 0) {
                regLists.get(0).setLastRegTime(new Date());
            }
            return true;
        }

        //检测设备号是否已记录在数据库中
        List<RegList> regLists = regListRepository.findAllByPollCodeAndEquipmentId(pollCode, equipmentId);
        if(regLists.size() > 0) {
            regLists.get(0).setLastRegTime(new Date());
            return true;
        }

        //从缓存中检测剩余激活信息
        if(regAtom.decrementAndGet()){
            //检测注册码是否正确
            List<RegInfo> regInfos = regInfoRepository.findAllByCustomerNoAndPollCode(customerNo, pollCode);
            if(regInfos.size() < 1)
                throw new BusinessException("激活码不正确");

            RegInfo regInfo = regInfos.get(0);

            int regTotal = regInfo.getRegTotal();
            int activeNum = regInfo.getActiveNum();
            if(regTotal <= activeNum)
                throw new BusinessException("激活码使用次数已达上线");
            regInfoRepository.addActiveNumFromRegInfoById(regInfo.getId());

            //注册设备
            RegList regList = new RegList();
            regList.setCustomerNo(customerNo);
            regList.setEquipmentId(equipmentId);
            regList.setPollCode(pollCode);
            regList.setFirstRegTime(new Date());
            regListRepository.save(regList);
            return true;
        }else{
            throw new BusinessException("激活码使用次数已达上线");
        }
    }
}
