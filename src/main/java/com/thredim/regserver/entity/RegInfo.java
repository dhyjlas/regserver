package com.thredim.regserver.entity;

import javax.persistence.*;

/**
 * 密钥列表实体类
 */
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
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="customer_no", columnDefinition="varchar(255) COMMENT '客户号'")
    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    @Column(name="poll_code", columnDefinition="varchar(255) COMMENT '激活码'")
    public String getPollCode() {
        return pollCode;
    }

    public void setPollCode(String pollCode) {
        this.pollCode = pollCode;
    }

    @Column(name="company_name", columnDefinition="varchar(255) COMMENT '公司名'")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name="reg_total", columnDefinition="int COMMENT '注册总数'")
    public int getRegTotal() {
        return regTotal;
    }

    public void setRegTotal(int regTotal) {
        this.regTotal = regTotal;
    }

    @Column(name="active_num", columnDefinition="int COMMENT '已激活数'")
    public int getActiveNum() {
        return activeNum;
    }

    public void setActiveNum(int activeNum) {
        this.activeNum = activeNum;
    }

    @Column(name="order_number", columnDefinition="varchar(255) COMMENT '订单号'")
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
