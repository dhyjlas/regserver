package com.thredim.regserver.repository;

import com.thredim.regserver.entity.RegInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegInfoRepository extends JpaRepository<RegInfo, Long>{
    List<RegInfo> findAllByCustomerNoAndPollCode(String customerNo, String pollCode);
    List<RegInfo> findAllByOrderNumber(String orderNumber);

    @Modifying
    @Query("update RegInfo set activeNum=activeNum+1 where id=:id")
    void addActiveNumFromRegInfoById(@Param(value = "id") long id);

    @Modifying
    @Query("update RegInfo set activeNum=activeNum-1 where id=:id")
    void reduceActiveNumFromRegInfoById(@Param(value = "id") long id);

    Page<RegInfo> findAll(Specification<RegInfo> example, Pageable pageable);
}
