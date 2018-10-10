package com.thredim.regserver.controller;

import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.service.OrderService;
import com.thredim.regserver.utils.ExcelUtils;
import com.thredim.regserver.utils.ResponseUtils;
import com.thredim.regserver.utils.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 密钥相关接口
 */
@Api(tags = {"密钥信息相关接口"})
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Value("${download.path}")
    private String downloadPath;

    /**
     * 获取密钥列表
     * @param page
     * @param size
     * @param sort
     * @param direction
     * @param customerNo
     * @param companyName
     * @param orderNumber
     * @return
     */
    @ApiOperation("获取密钥列表")
    @GetMapping("server/order/list")
    public RestResult list(@ApiParam("页码") @RequestParam(value = "page", defaultValue = "0") int page,
                           @ApiParam("每页数量") @RequestParam(value = "size", defaultValue = "10") int size,
                           @ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                           @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "desc") String direction,
                           @ApiParam("客户号筛选") @RequestParam(value = "customerNo", defaultValue = "") String customerNo,
                           @ApiParam("客户名筛选") @RequestParam(value = "companyName", defaultValue = "") String companyName,
                           @ApiParam("订单号筛选") @RequestParam(value = "orderNumber", defaultValue = "") String orderNumber){

        Page<RegInfo> dataPage = orderService.list(page, size, sort, direction, customerNo, companyName, orderNumber);

        long serial = 1;
        for(RegInfo regInfo : dataPage){
            regInfo.setSerial((serial++) + dataPage.getNumber() * dataPage.getSize());
        }

        return RestResult.getSuccess("查询成功").setObject(dataPage);
    }

    /**
     * 新增密钥
     * @return
     */
    @ApiOperation("密钥申请")
    @PostMapping("server/order")
    public RestResult add(@ApiParam("申请信息") @RequestBody RegInfo regInfo){
        if(StringUtils.isEmpty(regInfo.getCustomerNo())){
            return RestResult.getFailed("客户号不能位空");
        }
        if(StringUtils.isEmpty(regInfo.getCompanyName())){
            return RestResult.getFailed("客户名不能位空");
        }
        if(StringUtils.isEmpty(regInfo.getOrderNumber())){
            return RestResult.getFailed("订单号不能为空");
        }
        if(regInfo.getRegTotal() < 1){
            return RestResult.getFailed("激活数量不能小于1");
        }
        if(orderService.findAllByOrderNumber(regInfo.getOrderNumber()).size() > 0){
            return RestResult.getFailed("订单号已存在，请更换新的订单号");
        }
        orderService.add(regInfo);
        return RestResult.getSuccess("新增成功").setObject(regInfo);
    }

    /**
     * 删除密钥
     * @return
     */
    @ApiOperation("删除密钥")
    @DeleteMapping("server/order/{id}")
    public RestResult delete(@ApiParam("ID") @PathVariable long id){
        orderService.delete(id);
        return RestResult.getSuccess("删除成功");
    }

    /**
     * 修改密钥
     * @return
     */
    @ApiOperation("修改密钥信息")
    @PutMapping("server/order")
    public RestResult update(@ApiParam("修改信息") @RequestBody RegInfo regInfo){
        if(StringUtils.isEmpty(regInfo.getCompanyName())){
            return RestResult.getFailed("客户名不能位空");
        }
        if(StringUtils.isEmpty(regInfo.getOrderNumber())){
            return RestResult.getFailed("订单号不能为空");
        }
        if(regInfo.getRegTotal() < 0){
            return RestResult.getFailed("激活数量不能小于0");
        }
        List<RegInfo> regInfoList = orderService.findAllByOrderNumber(regInfo.getOrderNumber());
        if((regInfoList.size() > 1) || (regInfoList.size() == 1 && regInfoList.get(0).getId() != regInfo.getId())){
            return RestResult.getFailed("订单号已存在，请更换新的订单号");
        }
        orderService.update(regInfo);
        return RestResult.getSuccess("更新成功");
    }

    /**
     * 导出
     * @param sort
     * @param direction
     * @param customerNo
     * @param companyName
     * @param orderNumber
     */
    @ApiOperation("导出密钥信息至EXCEL文档")
    @GetMapping("server/order/download")
    public ResponseEntity<FileSystemResource> download(@ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                                                       @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "desc") String direction,
                                                       @ApiParam("客户号筛选") @RequestParam(value = "customerNo", defaultValue = "") String customerNo,
                                                       @ApiParam("客户名筛选") @RequestParam(value = "companyName", defaultValue = "") String companyName,
                                                       @ApiParam("订单号筛选") @RequestParam(value = "orderNumber", defaultValue = "") String orderNumber) throws IOException {

        Page<RegInfo> dataPage = orderService.list(sort, direction, customerNo, companyName, orderNumber);

        File path = new File(downloadPath);
        if(!path.exists() || !path.isDirectory()){
            path.mkdir();
        }

        File file = new File(downloadPath + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date()) + ".xlsx");
        ExcelUtils<RegInfo> utils = new ExcelUtils<RegInfo>(file.toString(), dataPage.getContent()){
            @Override
            public void setHeader(Row row){
                row.createCell(0).setCellValue("客户号");
                row.createCell(1).setCellValue("客户名");
                row.createCell(2).setCellValue("订单号");
                row.createCell(3).setCellValue("密钥");
                row.createCell(4).setCellValue("可激活总数");
                row.createCell(5).setCellValue("已激活数量");
            }

            @Override
            public void setCell(Row row, RegInfo entity){
                row.createCell(0).setCellValue(entity.getCustomerNo());
                row.createCell(1).setCellValue(entity.getCompanyName());
                row.createCell(2).setCellValue(entity.getOrderNumber());
                row.createCell(3).setCellValue(entity.getPollCode());
                row.createCell(4).setCellValue(entity.getRegTotal());
                row.createCell(5).setCellValue(entity.getActiveNum());
            }
        };

        utils.output();

        return ResponseUtils.download(file);
    }

    @ApiOperation("导出密钥文件")
    @GetMapping("server/order/key")
    public ResponseEntity<FileSystemResource> getKeyFile(@ApiParam("ID") @RequestParam long id) throws Exception {
        RegInfo regInfo = orderService.getRegInfoById(id);

        File path = new File(downloadPath);
        if(!path.exists() || !path.isDirectory()){
            path.mkdir();
        }

        File file = new File(downloadPath + new Date().getTime() + ".key");
        orderService.setKeyFile(file, regInfo);
        return ResponseUtils.download(file, "ThreDim.key");
    }
}
