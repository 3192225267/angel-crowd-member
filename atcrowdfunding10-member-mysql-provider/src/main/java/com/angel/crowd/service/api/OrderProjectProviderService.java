package com.angel.crowd.service.api;

import com.angel.crowd.entity.vo.AddressVO;
import com.angel.crowd.entity.vo.OrderProjectVO;
import com.angel.crowd.entity.vo.OrderVO;

import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--15 11:43
 */

public interface OrderProjectProviderService {
    OrderProjectVO getOrderProjectVO( Integer returnId);

    List<AddressVO> getAddressVOList(Integer memberId);

    void saveAddress(AddressVO addressVO);

    void saveOrder(OrderVO orderVO);
}
