package com.thredim.regserver.service;

import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.entity.RegList;
import com.thredim.regserver.repository.RegInfoRepository;
import com.thredim.regserver.repository.RegListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActiveService {
    private static final Logger log = LoggerFactory.getLogger(ActiveService.class);

    @Autowired
    private RegListRepository regListRepository;

    @Autowired
    private RegInfoRepository regInfoRepository;

    /**
     * 获取激活信息
     * @param page
     * @param size
     * @param sort
     * @param direction
     * @param customerNo
     * @param pollCode
     * @param equipmentId
     * @return
     */
    public Page<RegList> list(int page, int size, String sort, String direction, String customerNo, String pollCode, String equipmentId){
        Sort sortDate = new Sort("desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        Pageable pageable = PageRequest.of(page, size, sortDate);

        Specification<RegInfo> example = (Specification<RegInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //客户号
            if(!StringUtils.isEmpty(customerNo)){
                Predicate predicate = criteriaBuilder.like(root.get("customerNo").as(String.class), "%" + customerNo + "%");
                predicates.add(predicate);
            }
            //激活码
            if(!StringUtils.isEmpty(pollCode)){
                Predicate predicate = criteriaBuilder.like(root.get("pollCode").as(String.class), "%" + pollCode + "%");
                predicates.add(predicate);
            }
            //设备号
            if(!StringUtils.isEmpty(equipmentId)){
                Predicate predicate = criteriaBuilder.like(root.get("equipmentId").as(String.class), "%" + equipmentId + "%");
                predicates.add(predicate);
            }

            if (predicates.size() == 0) {
                return null;
            }

            Predicate[] predicateArr = new Predicate[predicates.size()];
            predicateArr = predicates.toArray(predicateArr);

            return criteriaBuilder.and(predicateArr);
        };

        return regListRepository.findAll(example, pageable);
    }

    /**
     * 获取所有的激活信息
     * @param sort
     * @param direction
     * @param customerNo
     * @param pollCode
     * @param equipmentId
     * @return
     */
    public Page<RegList> list(String sort, String direction, String customerNo, String pollCode, String equipmentId){
        return list(0, 1048575, sort, direction, customerNo, pollCode, equipmentId);
    }

    /**
     *
     * @param id
     */
    @Transactional
    public void delete(long id){
        regInfoRepository.reduceActiveNumFromRegInfoById(id);
        regListRepository.deleteById(id);
    }

}
