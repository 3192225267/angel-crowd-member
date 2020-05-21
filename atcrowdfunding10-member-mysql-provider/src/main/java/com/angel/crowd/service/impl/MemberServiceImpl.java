package com.angel.crowd.service.impl;

import java.util.List;

import com.angel.crowd.service.api.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.angel.crowd.entity.po.MemberPO;
import com.angel.crowd.entity.po.MemberPOExample;
import com.angel.crowd.entity.po.MemberPOExample.Criteria;
import com.angel.crowd.mapper.MemberPOMapper;

// 在类上使用@Transactional(readOnly = true)针对查询操作设置事务属性
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	private MemberPOMapper memberPOMapper;

	public MemberPO getMemberPOByLoginAcct(String loginacct) {
		
		// 1.创建Example对象
		MemberPOExample example = new MemberPOExample();
		
		// 2.创建Criteria对象
		Criteria criteria = example.createCriteria();
		
		// 3.封装查询条件
		criteria.andLoginacctEqualTo(loginacct);
		
		// 4.执行查询
		List<MemberPO> list = memberPOMapper.selectByExample(example);

		if(list==null ||list.size()==0){
			return null;
		}
		// 5.获取结果
		return list.get(0);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW,
					rollbackFor = Exception.class,
					readOnly = false)
	public void saveMember(MemberPO memberPO) {
		memberPOMapper.insertSelective(memberPO);
	}

}
