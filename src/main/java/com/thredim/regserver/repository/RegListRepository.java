package com.thredim.regserver.repository;

import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.entity.RegList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegListRepository extends JpaRepository<RegList, Long> {
    List<RegList> findAllByPollCodeAndEquipmentId(String pollCode, String equipmentId);

    Page<RegList> findAll(Specification<RegInfo> example, Pageable pageable);
}
