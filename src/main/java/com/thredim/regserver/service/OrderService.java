package com.thredim.regserver.service;

import com.alibaba.fastjson.JSON;
import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.exception.BusinessException;
import com.thredim.regserver.repository.RegInfoRepository;
import com.thredim.regserver.utils.Base64Utils;
import com.thredim.regserver.utils.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.util.*;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private RegInfoRepository regInfoRepository;

    /**
     * 分页查询订单信息
     * @param page          页码
     * @param size          显示个数
     * @param sort          排序字段
     * @param direction     排序方式
     * @param customerNo    客户号
     * @param companyName   客户名
     * @param orderNumber   订单号
     * @return
     */
    public Page<RegInfo> list(int page, int size, String sort, String direction, String customerNo, String companyName, String orderNumber){
        Sort sortDate = new Sort("desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        Pageable pageable = PageRequest.of(page, size, sortDate);

        Specification<RegInfo> example = (Specification<RegInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //客户号
            if(!StringUtils.isEmpty(customerNo)){
                Predicate predicate = criteriaBuilder.like(root.get("customerNo").as(String.class), "%" + customerNo + "%");
                predicates.add(predicate);
            }
            //公司名
            if(!StringUtils.isEmpty(companyName)){
                Predicate predicate = criteriaBuilder.like(root.get("companyName").as(String.class), "%" + companyName + "%");
                predicates.add(predicate);
            }
            //订单号
            if(!StringUtils.isEmpty(orderNumber)){
                Predicate predicate = criteriaBuilder.like(root.get("orderNumber").as(String.class), "%" + orderNumber + "%");
                predicates.add(predicate);
            }

            if (predicates.size() == 0) {
                return null;
            }

            Predicate[] predicateArr = new Predicate[predicates.size()];
            predicateArr = predicates.toArray(predicateArr);

            return criteriaBuilder.and(predicateArr);
        };

        return regInfoRepository.findAll(example, pageable);
    }

    /**
     * 分页查询订单信息
     * @param sort          排序字段
     * @param direction     排序方式
     * @param customerNo    客户号
     * @param companyName   客户名
     * @param orderNumber   订单号
     * @return
     */
    public Page<RegInfo> list(String sort, String direction, String customerNo, String companyName, String orderNumber){
        return list(0, 1048575, sort, direction, customerNo, companyName, orderNumber);
    }

    /**
     * 保存订单信息
     * @param regInfo
     */
    @Transactional
    public void add(RegInfo regInfo){
        regInfo.setActiveNum(0);
        regInfo.setPollCode(generateShortUuid());

        regInfoRepository.saveAndFlush(regInfo);
    }

    public List<RegInfo> findAllByOrderNumber(String orderNumber){
        return regInfoRepository.findAllByOrderNumber(orderNumber);
    }
    /**
     * 修改订单
     * @param data
     * @throws BusinessException
     */
    @Transactional
    public void update(RegInfo data) throws BusinessException{
        Optional<RegInfo> regInfoOptional = regInfoRepository.findById(data.getId());
        RegInfo regInfo = regInfoOptional.orElse(null);
        if(regInfo == null)
            throw new BusinessException("ID不存在");

        if(!regInfo.getCompanyName().equals(data.getCompanyName())) {
            regInfo.setCompanyName(data.getCompanyName());
        }

        if(!regInfo.getOrderNumber().equals(data.getOrderNumber())) {
            regInfo.setOrderNumber(data.getOrderNumber());
        }

        if(regInfo.getRegTotal() != data.getRegTotal()) {
            regInfo.setRegTotal(data.getRegTotal());
        }
    }

    /**
     * 删除订单
     * @param id
     * @throws BusinessException
     */
    @Transactional
    public void delete(long id){
        regInfoRepository.deleteById(id);
    }

    /**
     * 通过ID获取订单信息
     * @param id
     * @return
     */
    public RegInfo getRegInfoById(long id){
        return regInfoRepository.findById(id).orElse(null);
    }

    @Value("${ras.publicKey:-1}")
    private String PUBLIC_KEY;

    /**
     * 生成密钥文件
     * @param file
     * @param regInfo
     */
    public void setKeyFile(File file, RegInfo regInfo) throws Exception {
        if (!file.exists()) {	//文件不存在则创建文件
            file.createNewFile();
        }else{
            file.delete();
            file.createNewFile();
        }

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("customerNo", regInfo.getCustomerNo());
        keyMap.put("pollCode", regInfo.getPollCode());

        String keyData = JSON.toJSONString(keyMap);
        PublicKey publicKey = RSAUtils.loadPublicKey(PUBLIC_KEY);
        byte[] encryptByte = RSAUtils.encryptData(keyData.getBytes(), publicKey);
        String encodedData = Base64Utils.encode(encryptByte);

        FileOutputStream outStream = new FileOutputStream(file);	//文件输出流用于将数据写入文件
        outStream.write(encodedData.getBytes());
        outStream.close();
    }

    /**
     * 激活码所用字符
     * 去掉了I,J,O
     */
    public static String[] chars = new String[] { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
            "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    /**
     * 生成随机激活码
     * 生成规则：
     * 参数一个UUID
     * 以2位合为一位，生成激活码前16位
     * 以4位合为一位，生成激活码中间8位
     * 以全部的32为为信息，生成激活码最后1位
     * 总共25位激活码
     * @return
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 16; i++) {
            String str1 = uuid.substring(i * 2, i * 2 + 1);
            String str2 = uuid.substring(i * 2 + 1, i * 2 + 2);
            int x = Integer.parseInt(str1, 16) + Integer.parseInt(str2, 16);
            shortBuffer.append(chars[x % 0x21]);
        }
        for (int i = 0 ; i < 8; i++){
            String str1 = uuid.substring(i * 4, i * 4 + 1);
            String str2 = uuid.substring(i * 4 + 1, i * 4 + 2);
            String str3 = uuid.substring(i * 4 + 2, i * 4 + 3);
            String str4 = uuid.substring(i * 4 + 3, i * 4 + 4);
            int x = Integer.parseInt(str1, 16) + Integer.parseInt(str2, 16)
                    + Integer.parseInt(str3, 16) + Integer.parseInt(str4, 16);
            shortBuffer.append(chars[x % 0x21]);
        }
        int total = 0;
        for (int i = 0 ; i < 32; i++){
            String str1 = uuid.substring(i, i + 1);
            total += Integer.parseInt(str1, 16);
        }
        shortBuffer.append(chars[total % 0x21]);

        String str = shortBuffer.toString();
        return str.substring(0, 5) + "-" + str.substring(5, 10) + "-" + str.substring(10, 15) + "-" + str.substring(15, 20) + "-" + str.substring(20, 25);
    }

    public static void main (String[] args) throws Exception {
        RegInfo regInfo = new RegInfo();
        regInfo.setCustomerNo("ThreDim");
        regInfo.setPollCode("700A-12358-D2500-E9DC5-AEB99");
        new OrderService().setKeyFile(new File("D://123.key"), regInfo);
    }
}
