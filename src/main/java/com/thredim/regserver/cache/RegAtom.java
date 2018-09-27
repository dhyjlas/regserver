package com.thredim.regserver.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 注册信息原子
 */
@Getter
@Setter
@ToString
public class RegAtom {
    private long id;
    private String customerNo;
    private String pollCode;
    private AtomicInteger surplus;
    private CopyOnWriteArrayList<String> equipmentList = new CopyOnWriteArrayList<>();

    public RegAtom(long id, String customerNo, String pollCode, int surplus){
        this.id = id;
        this.customerNo = customerNo;
        this.pollCode = pollCode;
        this.surplus = new AtomicInteger(surplus);
    }

    /**
     * 将surplus减1，结果小于0时返回false，大于或等于0时返回成功
     */
    public boolean decrementAndGet(){
        int residue = surplus.decrementAndGet();
        if(residue < 0)
            return false;
        return true;
    }

    /**
     * 校验设备ID是否在缓存中
     * @param equipmentId
     * @return
     */
    public synchronized boolean checkEquipment(String equipmentId){
        if(equipmentList.contains(equipmentId)) {
            return false;
        }
        equipmentList.add(equipmentId);
        return true;
    }
}
