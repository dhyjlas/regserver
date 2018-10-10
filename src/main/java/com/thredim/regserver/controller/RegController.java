package com.thredim.regserver.controller;

import com.thredim.regserver.exception.BusinessException;
import com.thredim.regserver.service.RegService;
import com.thredim.regserver.utils.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册接口
 */
@Api(tags = {"注册接口"})
@RestController
public class RegController {
    @Autowired
    private RegService regService;

    /**
     * 注册接口
     * @param data
     * @return
     */
    @ApiOperation("注册接口")
    @PostMapping("registrar")
    public RestResult registrar(@ApiParam("加密后注册消息") @RequestBody String data){
        boolean flag;

        try {
            flag = regService.registrar(data);
        } catch (BusinessException e) {
//            e.printStackTrace();
            return RestResult.getFailed(e.getMessage()).setObject("Failed");
        } catch (Exception e) {
//            e.printStackTrace();
            return RestResult.getFailed("消息解析失败").setObject("Failed");
        }

        return flag ? RestResult.getSuccess("激活成功").setObject("Success") : RestResult.getFailed("激活失败").setObject("Failed");
    }

}
