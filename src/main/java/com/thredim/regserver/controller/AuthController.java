package com.thredim.regserver.controller;

import com.thredim.regserver.entity.User;
import com.thredim.regserver.repository.UserRepository;
import com.thredim.regserver.security.AuthUtils;
import com.thredim.regserver.utils.MD5Utils;
import com.thredim.regserver.utils.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 身份认证
 */
@Api(tags = {"身份认证"})
@RestController
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @ApiOperation("口令验证")
    @GetMapping("auth")
    public RestResult auth(@ApiParam("加密口令") @RequestParam String password){
        List<User> userList = userRepository.findAll();

        for(User user : userList){
            String pwd = user.getPassword();
            if(password.equals(MD5Utils.MD5(pwd + (new Date().getTime() + "").substring(0, 8)))){
                String token = AuthUtils.getInstance().setToken();
                return RestResult.getSuccess("口令通过").setObject(token);
            }
            if(password.equals(MD5Utils.MD5(pwd + (new Date().getTime() - 100000 + "").substring(0, 8)))){
                String token = AuthUtils.getInstance().setToken();
                return RestResult.getSuccess("口令通过").setObject(token);
            }
            if(password.equals(MD5Utils.MD5(pwd + (new Date().getTime() + 100000 + "").substring(0, 8)))){
                String token = AuthUtils.getInstance().setToken();
                return RestResult.getSuccess("口令通过").setObject(token);
            }
        }
//        if(userRepository.findAllByPassword(password).size() > 0){
//            String token = AuthUtils.getInstance().setToken();
//            return RestResult.getSuccess("口令通过").setObject(token);
//        }else{
//            return RestResult.getFailed("口令错误");
//        }
        return RestResult.getFailed("口令错误");
    }

    @ApiOperation("获取登录状态")
    @GetMapping("check")
    public RestResult check(){
        return RestResult.getSuccess();
    }
}
