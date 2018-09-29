package com.thredim.regserver.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_reg_list")
public class RegList {
    private long id;
    private String customerNo;
    private String pollCode;
    private String equipmentId;
    private Date firstRegTime;
    private Date lastRegTime;

    private long serial;
    private String firstRegStr;
    private String lastRegStr;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="customer_no")
    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    @Column(name="poll_code")
    public String getPollCode() {
        return pollCode;
    }

    public void setPollCode(String pollCode) {
        this.pollCode = pollCode;
    }

    @Column(name="equipment_id")
    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Column(name="first_reg_time")
    public Date getFirstRegTime() {
        return firstRegTime;
    }

    public void setFirstRegTime(Date firstRegTime) {
        this.firstRegTime = firstRegTime;
    }

    @Column(name="last_reg_time")
    public Date getLastRegTime() {
        return lastRegTime;
    }

    public void setLastRegTime(Date lastRegTime) {
        this.lastRegTime = lastRegTime;
    }

    @Transient
    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }

    @Transient
    public String getFirstRegStr() {
        return firstRegStr;
    }

    public void setFirstRegStr(String firstRegStr) {
        this.firstRegStr = firstRegStr;
    }

    @Transient
    public String getLastRegStr() {
        return lastRegStr;
    }

    public void setLastRegStr(String lastRegStr) {
        this.lastRegStr = lastRegStr;
    }
}
