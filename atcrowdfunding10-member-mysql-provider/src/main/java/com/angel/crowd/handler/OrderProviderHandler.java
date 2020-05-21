package com.angel.crowd.handler;

import com.angel.crowd.entity.vo.AddressVO;
import com.angel.crowd.entity.vo.OrderProjectVO;
import com.angel.crowd.entity.vo.OrderVO;
import com.angel.crowd.service.api.OrderProjectProviderService;
import com.angel.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--15 11:39
 */
@RestController
public class OrderProviderHandler {
    @Autowired
    private OrderProjectProviderService orderService;

    @RequestMapping("/save/order/remote")
    ResultEntity<String> saveOrderRemote(@RequestBody OrderVO orderVO) {

        try {
            orderService.saveOrder(orderVO);

            return ResultEntity.successWithoutData();

        } catch (Exception e) {
            e.printStackTrace();

            return ResultEntity.failed(e.getMessage());
        }

    }

    @RequestMapping("/save/address/remote")
    ResultEntity<String> saveAddressRemote(@RequestBody AddressVO addressVO){
        try {
            orderService.saveAddress(addressVO);
            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }
    @RequestMapping("confirm/return/info/{returnId}")
    public ResultEntity<OrderProjectVO> getOrderProjectVORemote(@PathVariable("returnId") Integer returnId){
        try {
            OrderProjectVO orderProjectVO = orderService.getOrderProjectVO(returnId);
            return  ResultEntity.successWithData(orderProjectVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    // 查询确认订单页面所需的数据
    @RequestMapping("/get/address/vo/remote")
    ResultEntity<List<AddressVO>> getAddressVORemote(@RequestParam("memberId") Integer memberId) {

        try {
            List<AddressVO> addressVOList = orderService.getAddressVOList(memberId);
            return ResultEntity.successWithData(addressVOList);
        }catch (Exception e){
            return ResultEntity.failed(e.getMessage());
        }

    }
}
