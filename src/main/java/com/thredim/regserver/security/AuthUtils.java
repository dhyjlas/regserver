package com.thredim.regserver.security;

import com.thredim.regserver.entity.User;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuthUtils {
    private static AuthUtils instance = new AuthUtils();

    private CopyOnWriteArrayList<User> liveUserList = new CopyOnWriteArrayList<>();

    private AuthUtils(){}

    public static AuthUtils getInstance(){
        return instance;
    }

    /**
     * 验证token是否有效
     * @param token
     * @return
     */
    public boolean cheak(String token){
        for(User user : liveUserList){
            if(token.equals(user.getToken())){
                Date date = new Date();
                if(date.getTime() - user.getLastAuthTime().getTime() < 60 * 60 * 1000){
                    user.setLastAuthTime(date);  //刷新操作时间
                    return true;
                }else{
                    liveUserList.remove(user);  //去掉失效的token
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 新增一个token，同时返回该token
     * @return
     */
    public String setToken(){
        String token = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");

        liveUserList.add(new User(token, new Date()));
        return token;
    }

    /**
     * 清除token
     * @param token
     */
    public void delete(String token){
        for(User user : liveUserList){
            if(token.equals(user.getToken())){
                liveUserList.remove(user);
            }
        }
    }
}
