package com.angel.crowd.service.impl;

import com.angel.crowd.entity.po.AddressPO;
import com.angel.crowd.entity.po.AddressPOExample;
import com.angel.crowd.entity.po.OrderPO;
import com.angel.crowd.entity.po.OrderProjectPO;
import com.angel.crowd.entity.vo.AddressVO;
import com.angel.crowd.entity.vo.OrderProjectVO;
import com.angel.crowd.entity.vo.OrderVO;
import com.angel.crowd.mapper.AddressPOMapper;
import com.angel.crowd.mapper.OrderPOMapper;
import com.angel.crowd.mapper.OrderProjectPOMapper;
import com.angel.crowd.mapper.ReturnPOMapper;
import com.angel.crowd.service.api.OrderProjectProviderService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--15 11:43
 */
@Service
@Transactional(readOnly = true)
public class OrderProjectProviderServiceImpl implements OrderProjectProviderService {
    @Autowired
    private OrderProjectPOMapper orderProjectPOMapper;

    @Autowired
    private AddressPOMapper addressPOMapper;

    @Autowired
    private ReturnPOMapper returnPOMapper;

    @Autowired
    private OrderPOMapper orderPOMapper;
    public OrderProjectVO getOrderProjectVO(Integer returnId) {

        return orderProjectPOMapper.getOrderProjectVO(returnId);
    }

    public List<AddressVO> getAddressVOList(Integer memberId) {
        AddressPOExample addressPOExample = new AddressPOExample();
        AddressPOExample.Criteria criteria = addressPOExample.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        List<AddressPO> addressPOList = addressPOMapper.selectByExample(addressPOExample);
        List<AddressVO> addressVOList = new ArrayList<AddressVO>();
        for (AddressPO addressPO : addressPOList
                ) {
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(addressPO, addressVO);
            addressVOList.add(addressVO);
        }
        return addressVOList;
    }
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void saveAddress(AddressVO addressVO) {
        AddressPO addressPO = new AddressPO();
        BeanUtils.copyProperties(addressVO, addressPO);
        addressPOMapper.insertSelective(addressPO);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveOrder(OrderVO orderVO) {

        OrderPO orderPO = new OrderPO();

        BeanUtils.copyProperties(orderVO, orderPO);

        OrderProjectPO orderProjectPO = new OrderProjectPO();

        BeanUtils.copyProperties(orderVO.getOrderProjectVO(), orderProjectPO);

        // 保存orderPO自动生成的主键是orderProjectPO需要用到的外键
        orderPOMapper.insert(orderPO);

        // 从orderPO中获取orderId
        Integer id = orderPO.getId();

        // 将orderId设置到orderProjectPO
        orderProjectPO.setOrderId(id);

        orderProjectPOMapper.insert(orderProjectPO);
    }

}
