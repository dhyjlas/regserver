package com.thredim.regserver.controller;

import com.thredim.regserver.entity.RegInfo;
import com.thredim.regserver.service.OrderService;
import com.thredim.regserver.utils.ExcelUtils;
import com.thredim.regserver.utils.ResponseUtils;
import com.thredim.regserver.utils.RestResult;
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
import java.util.Date;
import java.util.List;

/**
 * 订单相关接口
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Value("${download.path}")
    private String downloadPath;

    /**
     * 获取订单列表
     * @param page
     * @param size
     * @param sort
     * @param direction
     * @param customerNo
     * @param companyName
     * @param orderNumber
     * @return
     */
    @GetMapping("server/order/list")
    public RestResult list(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "sort", defaultValue = "id") String sort,
                           @RequestParam(value = "direction", defaultValue = "asc") String direction,
                           @RequestParam(value = "customerNo", defaultValue = "") String customerNo,
                           @RequestParam(value = "companyName", defaultValue = "") String companyName,
                           @RequestParam(value = "orderNumber", defaultValue = "") String orderNumber){

        Page<RegInfo> dataPage = orderService.list(page, size, sort, direction, customerNo, companyName, orderNumber);

        return RestResult.getSuccess("查询成功").setObject(dataPage);
    }

    /**
     * 新增订单
     * @return
     */
    @PostMapping("server/order")
    public RestResult add(@RequestBody RegInfo regInfo){
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
     * 删除订单
     * @return
     */
    @DeleteMapping("server/order/{id}")
    public RestResult delete(@PathVariable long id){
        orderService.delete(id);
        return RestResult.getSuccess("删除成功");
    }

    /**
     * 修改订单
     * @return
     */
    @PutMapping("server/order")
    public RestResult update(@RequestBody RegInfo regInfo){
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
    @GetMapping("server/order/download")
    public ResponseEntity<FileSystemResource> download(@RequestParam(value = "sort", defaultValue = "id") String sort,
                                                       @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                       @RequestParam(value = "customerNo", defaultValue = "") String customerNo,
                                                       @RequestParam(value = "companyName", defaultValue = "") String companyName,
                                                       @RequestParam(value = "orderNumber", defaultValue = "") String orderNumber) throws IOException {

        Page<RegInfo> dataPage = orderService.list(sort, direction, customerNo, companyName, orderNumber);

        File path = new File(downloadPath);
        if(!path.exists() || !path.isDirectory()){
            path.mkdir();
        }

        File file = new File(downloadPath + new Date().getTime() + ".xlsx");
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
}
