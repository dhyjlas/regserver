package com.thredim.regserver.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类
 */
@Entity
@Table(name = "t_user")
public class User {
    private long id;
    private String password;
    private String token;
    private Date lastAuthTime;

    public User(){}

    public User(String token, Date lastAuthTime){
        this.token = token;
        this.lastAuthTime = lastAuthTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="password", columnDefinition="varchar(255) COMMENT '密码'")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Transient
    public Date getLastAuthTime() {
        return lastAuthTime;
    }

    public void setLastAuthTime(Date lastAuthTime) {
        this.lastAuthTime = lastAuthTime;
    }
}
