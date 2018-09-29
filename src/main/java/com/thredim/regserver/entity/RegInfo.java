package com.thredim.regserver.entity;

import javax.persistence.*;

@Entity
@Table(name = "t_reg_info")
public class RegInfo {
    private long id;
    private String customerNo;
    private String pollCode;
    private String companyName;
    private int regTotal;
    private int activeNum;
    private String orderNumber;

    private long serial;

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

    @Column(name="company_name")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name="reg_total")
    public int getRegTotal() {
        return regTotal;
    }

    public void setRegTotal(int regTotal) {
        this.regTotal = regTotal;
    }

    @Column(name="active_num")
    public int getActiveNum() {
        return activeNum;
    }

    public void setActiveNum(int activeNum) {
        this.activeNum = activeNum;
    }

    @Column(name="order_number")
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Transient
    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }
}
