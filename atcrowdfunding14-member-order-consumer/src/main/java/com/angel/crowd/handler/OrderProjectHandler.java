package com.angel.crowd.handler;

import com.angel.crowd.constant.CrowdConstant;
import com.angel.crowd.entity.vo.AddressVO;
import com.angel.crowd.entity.vo.MemberLoginVO;
import com.angel.crowd.entity.vo.OrderProjectVO;
import com.angel.crowd.service.MySQLRemoteService;
import com.angel.crowd.util.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--15 11:57
 */
@Controller
public class OrderProjectHandler {
    @Autowired
    private MySQLRemoteService mySQLRemoteService;
    Logger logger=LoggerFactory.getLogger(OrderProjectHandler.class);
    // 支持项目跳转（去往确认汇报内容）
    @RequestMapping("/confirm/return/info/{returnId}")
    public String showReturnConfirmInfo(@PathVariable("returnId") Integer returnId,
                                        HttpSession session){

        ResultEntity<OrderProjectVO> orderProjectVORemote = mySQLRemoteService.getOrderProjectVORemote(returnId);
        if(ResultEntity.SUCCESS.equals(orderProjectVORemote.getResult())){
            session.setAttribute("orderProjectVO",orderProjectVORemote.getData());
            return "confirm_return";
        }
        session.setAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_TEMPLE_OVERDUE_MISSING);
        return "localhost:80";

    }
    // 去结算（去往确认订单）
    @RequestMapping("confirm/order/{returnCount}")
    public String showConfirmOrderInfo(@PathVariable("returnCount") Integer returnCount,
                                       HttpSession session){
        // 合并回报数量
        OrderProjectVO orderProjectVO=(OrderProjectVO)session.getAttribute("orderProjectVO");
        orderProjectVO.setReturnCount(returnCount);
        session.setAttribute("orderProjectVO",orderProjectVO);
        // 取出存在Session域的用户对象
      MemberLoginVO memberLoginVO=(MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);

      // 查询地址
        ResultEntity<List<AddressVO>> addressVORemote = mySQLRemoteService.getAddressVORemote(memberLoginVO.getId());
        if(ResultEntity.SUCCESS.equals(addressVORemote.getResult())) {
            List<AddressVO> list = addressVORemote.getData();
            session.setAttribute("addressVOList", list);
        }

        return "confirm_order";
    }

    @RequestMapping("/save/address")
    public String saveAddress(AddressVO addressVO, HttpSession session) {
        ResultEntity<String> resultEntity = mySQLRemoteService.saveAddressRemote(addressVO);
        OrderProjectVO orderProjectVO=(OrderProjectVO)session.getAttribute("orderProjectVO");
        if(ResultEntity.SUCCESS.equals(resultEntity.getResult())){

            return "redirect:localhost:confirm/order/"+orderProjectVO.getReturnCount();
        }else{
            logger.error("保存失败");
            return "redirect:http://localhost/order/confirm/order/"+orderProjectVO.getReturnCount();
        }
    }
}

