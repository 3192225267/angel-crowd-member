package com.angel.crowd.handler;

import com.angel.crowd.constant.CrowdConstant;
import com.angel.crowd.entity.vo.DetailProjectVO;
import com.angel.crowd.entity.vo.PortalTypeVO;
import com.angel.crowd.service.MySQLRemoteService;
import com.angel.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--01 10:31
 */
@Controller
public class PortalHandler {
    @Autowired
    private MySQLRemoteService mySQLRemoteServicel;
    @RequestMapping("/")
    public String showPotalPage(ModelMap modelMap){
        // 1. 调用mysqlRemoteService
        ResultEntity<List<PortalTypeVO>>  resultEntity= mySQLRemoteServicel.getPortalTypeProjectDataRemote();

        // 2.检查查询结果状态
        String result=resultEntity.getResult();
        if(ResultEntity.SUCCESS.equals(result)){
            // 获取查询结果数据
            List<PortalTypeVO> typeVOList=resultEntity.getData();
            // 存入模型
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_PORTAL_DATA,typeVOList);
        }
        return "portal";
    }



}
