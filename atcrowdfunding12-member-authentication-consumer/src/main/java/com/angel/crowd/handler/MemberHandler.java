package com.angel.crowd.handler;

import com.angel.crowd.config.ShortMessageProperites;

import com.angel.crowd.constant.CrowdConstant;
import com.angel.crowd.entity.po.MemberPO;
import com.angel.crowd.entity.vo.MemberLoginVO;
import com.angel.crowd.entity.vo.MemberVO;
import com.angel.crowd.service.MySQLRemoteService;
import com.angel.crowd.service.RedisRemoteService;

import com.angel.crowd.util.CrowdUtil;
import com.angel.crowd.util.ResultEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 刘振河
 * @create 2020--05--02 16:51
 */
@Controller
public class MemberHandler {
    @Autowired
    private ShortMessageProperites shortMessageProperites;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService  mySQLRemoteService;

    @RequestMapping("/auth/member/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:localhost/";
    }
    @RequestMapping("auth/member/do/login")
    public String authlogin(@RequestParam("loginacct")String loginacct,
                            @RequestParam("userpswd")String userpswd,
                            ModelMap modelMap,
                            HttpSession session){
      ResultEntity<MemberPO> resultEntity=  mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
      if(ResultEntity.FAILED.equals(resultEntity.getResult())){
          modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
          return "member-login";
      }
      // 取出查询到的数据
      MemberPO memberPO=resultEntity.getData();

      // 判断数据是否有效
        if(memberPO==null){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }
        // 验证密码
        String userpswdDataBase=memberPO.getUserpswd();
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        boolean matcheResult=passwordEncoder.matches(userpswd,userpswdDataBase);
        if(!matcheResult){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }
        MemberLoginVO memberLoginVO=new MemberLoginVO(memberPO.getId(),memberPO.getUsername(),memberPO.getEmail());
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER,memberLoginVO);
        // 防止表单重复提交，重定向到个人主页页面
        return "redirect:http://localhost/auth/member/to/center/page";
    }
    // 提交注册
    @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, ModelMap modelMap){
        // 1.获取用户输入的邮箱
        String email=memberVO.getEmail();

        // 2.拼Redis中存储验证码的Key
        String key=CrowdConstant.REDIS_CODE_PREFIX+email;

        // 3.从Redis读取Key对应的value
        ResultEntity<String> resultEntity=redisRemoteService.getRedisStringValueByKeyRemote(key);
        // 4.检查查询操作是否有效
        // 如果读取错误（或者用户输错的验证码以至于查不到）
        String result=resultEntity.getResult();
        if(ResultEntity.FAILED.equals(result)){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
            return "member-reg";
        }
        // 查到了但是数据为空，原因：（验证码可能已经使用过，被删除了）
        String redisCode=resultEntity.getData();
        if(redisCode==null){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
            return "member-reg";
        }
        // 5.如果从Redis能够查询到value则比较表单验证码和Redis验证码
            String formCode=memberVO.getCode();
        // 输入的和查到的不一致
        if(!Objects.equals(formCode, redisCode)){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
            return "member-reg";
        }
        // 6.如果验证码一致，则从Redis删除
        // 待完善（）
        redisRemoteService.removeRedisKeyRemote(key);
        // 7.执行密码加密
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        String userpswdBeforeEncode=memberVO.getUserpswd();

        String userpswdAfterEncode=passwordEncoder.encode(userpswdBeforeEncode);

        memberVO.setUserpswd(userpswdAfterEncode);

        // 8.执行保存
        //  ①创建空的MemberPO对象
        MemberPO memberPO=new MemberPO();
        // ②复制属性
        BeanUtils.copyProperties(memberVO,memberPO);

        // ③调用远程方法
       ResultEntity<String> saveMemberResultEntity= mySQLRemoteService.saveMember(memberPO);
       if(ResultEntity.FAILED.equals(saveMemberResultEntity)){
           modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,saveMemberResultEntity.getMessage());
       }
        // 使用重定向避免刷新浏览器导致重新执行注册流程
        return "redirect://auth/member/to/login/page";
    }




// 发送验证码并保存到Redis
@RequestMapping("/auth/member/send/short/message.json")
@ResponseBody
public ResultEntity<String> sendMessage(@RequestParam("email") String email) throws MessagingException {
    ResultEntity<String> sendMennageResultEntity= CrowdUtil.send_mail(email);
    // 2.判断短信发送结果
    if(ResultEntity.SUCCESS.equals(sendMennageResultEntity.getResult())){
        // 3. 如果发送成功，则将验证码存入Redis
        // 从上一部操作的结果中获取随机生成的验证码
        // 如果发送成功，则将验证码存入Redis
        String code=sendMennageResultEntity.getData();

        // 拼接一个用于在Redis中存储数据的Key
        String key=CrowdConstant.REDIS_CODE_PREFIX+email;

        // 调用远程接口存入Reids
        ResultEntity<String> saveCodeResultEntity= redisRemoteService.setRedisKeyValueRemoteWithTimeout(key,code,15,TimeUnit.MINUTES);

        // 判断结果
        if(ResultEntity.SUCCESS.equals(saveCodeResultEntity)){
            return ResultEntity.successWithoutData();
        }else{
            return sendMennageResultEntity;
        }
    }else{
        return sendMennageResultEntity;
    }
}

}
