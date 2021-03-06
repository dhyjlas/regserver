package com.thredim.regserver.controller;

import com.thredim.regserver.entity.RegList;
import com.thredim.regserver.service.ActiveService;
import com.thredim.regserver.utils.ExcelUtils;
import com.thredim.regserver.utils.ResponseUtils;
import com.thredim.regserver.utils.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 设备信息相关接口
 */
@Api(tags = {"设备信息相关接口"})
@RestController
public class ActiveController {
    @Autowired
    private ActiveService activeService;

    @Value("${download.path}")
    private String downloadPath;

    /**
     * 获取激活信息
     * @return
     */
    @ApiOperation("获取激活信息")
    @GetMapping("server/active/list")
    public RestResult list(@ApiParam("页码") @RequestParam(value = "page", defaultValue = "0") int page,
                           @ApiParam("每页数量") @RequestParam(value = "size", defaultValue = "10") int size,
                           @ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                           @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "desc") String direction,
                           @ApiParam("客户号筛选") @RequestParam(value = "customerNo", defaultValue = "") String customerNo,
                           @ApiParam("激活码筛选") @RequestParam(value = "pollCode", defaultValue = "") String pollCode,
                           @ApiParam("设备号") @RequestParam(value = "equipmentId", defaultValue = "") String equipmentId){

        if("firstRegStr".equals(sort))
            sort = "firstRegTime";
        if("lastRegStr".equals(sort))
            sort = "lastRegTime";

        Page<RegList> dataPage = activeService.list(page, size, sort, direction, customerNo, pollCode, equipmentId);
        setTimeStr(dataPage);


        return RestResult.getSuccess("查询成功").setObject(dataPage);
    }

    /**
     * 删除激活信息
     * @return
     */
    @ApiOperation("删除激活信息")
    @DeleteMapping("server/active/{id}")
    public RestResult delete(@ApiParam("ID") @PathVariable long id){
        activeService.delete(id);
        return RestResult.getSuccess("删除成功");
    }

    /**
     * 导出
     * @param sort
     * @param direction
     * @param customerNo
     * @param pollCode
     * @param equipmentId
     * @return
     * @throws IOException
     */
    @ApiOperation("导出设备信息至EXCEL文档")
    @GetMapping("server/active/download")
    public ResponseEntity<FileSystemResource> download(@ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                                                       @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "desc") String direction,
                                                       @ApiParam("客户号筛选") @RequestParam(value = "customerNo", defaultValue = "") String customerNo,
                                                       @ApiParam("激活码筛选") @RequestParam(value = "pollCode", defaultValue = "") String pollCode,
                                                       @ApiParam("设备号") @RequestParam(value = "equipmentId", defaultValue = "") String equipmentId) throws IOException {

        if("firstRegStr".equals(sort))
            sort = "firstRegTime";
        if("lastRegStr".equals(sort))
            sort = "lastRegTime";

        Page<RegList> dataPage = activeService.list(sort, direction, customerNo, pollCode, equipmentId);
        setTimeStr(dataPage);

        File path = new File(downloadPath);
        if(!path.exists() || !path.isDirectory()){
            path.mkdir();
        }

        File file = new File(downloadPath + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date()) + ".xlsx");
        ExcelUtils<RegList> utils = new ExcelUtils<RegList>(file.toString(), dataPage.getContent()){
            @Override
            public void setHeader(Row row){
                row.createCell(0).setCellValue("客户号");
                row.createCell(1).setCellValue("密钥");
                row.createCell(2).setCellValue("设备号");
                row.createCell(3).setCellValue("首次激活时间");
                row.createCell(4).setCellValue("最后一次激活时间");
            }

            @Override
            public void setCell(Row row, RegList entity){
                row.createCell(0).setCellValue(entity.getCustomerNo());
                row.createCell(1).setCellValue(entity.getPollCode());
                row.createCell(2).setCellValue(entity.getEquipmentId());
                row.createCell(3).setCellValue(entity.getFirstRegStr());
                row.createCell(4).setCellValue(entity.getLastRegStr());
            }
        };

        utils.output();

        return ResponseUtils.download(file);
    }

    public void setTimeStr(Page<RegList> dataPage){
        long serial = 1;
        for(RegList regList : dataPage.getContent()){
            if(regList.getFirstRegTime() != null) {
                regList.setFirstRegStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(regList.getFirstRegTime()));
            }
            if(regList.getLastRegTime() != null) {
                regList.setLastRegStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(regList.getLastRegTime()));
            }
            regList.setSerial((serial++) + dataPage.getNumber() * dataPage.getSize());
        }
    }
}
