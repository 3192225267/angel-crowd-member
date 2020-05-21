package com.angel.crowd.handler;

import com.angel.crowd.constant.CrowdConstant;
import com.angel.crowd.service.api.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angel.crowd.entity.po.MemberPO;
import com.angel.crowd.util.ResultEntity;

import javax.xml.transform.Result;
import java.lang.reflect.Member;

@RestController
public class MemberProviderHandler {
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("/get/memberpo/by/login/acct/remote")
	public ResultEntity<MemberPO>getMemberPOByLoginAcctRemote(@RequestParam("loginacct")String loginacct){

		try {
			MemberPO memberPO=memberService.getMemberPOByLoginAcct(loginacct);
			return ResultEntity.successWithData(memberPO);
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}

	// 注册

	@RequestMapping("/save/member/remote")
	public ResultEntity<String> saveMember(@RequestBody MemberPO memberPO){

		try {
		memberService.saveMember(memberPO);
		return ResultEntity.successWithoutData();
		}catch (Exception e){
			if (e instanceof DuplicateKeyException ){
				return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
			}
			return ResultEntity.failed(e.getMessage());
		}
	}

}
