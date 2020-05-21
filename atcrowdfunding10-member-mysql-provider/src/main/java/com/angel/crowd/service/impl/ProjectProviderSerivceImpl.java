package com.angel.crowd.service.impl;

import com.angel.crowd.entity.po.MemberConfirmInfoPO;
import com.angel.crowd.entity.po.MemberLaunchInfoPO;
import com.angel.crowd.entity.po.ProjectPO;
import com.angel.crowd.entity.po.ReturnPO;
import com.angel.crowd.entity.vo.*;
import com.angel.crowd.mapper.*;
import com.angel.crowd.service.api.ProjectProviderSerivce;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--06 9:17
 */
@Transactional(readOnly = true)
@Service
public class ProjectProviderSerivceImpl implements ProjectProviderSerivce {
    @Autowired
    private  ProjectPOMapper projectPOMapper;

    @Autowired
    private ProjectItemPicPOMapper projectItemPicPOMapper;

    @Autowired
    private MemberLaunchInfoPOMapper memberLaunchInfoPOMapper;

    @Autowired
    private ReturnPOMapper returnPOMapper;

    @Autowired
    private MemberConfirmInfoPOMapper memberConfirmInfoPOMapper;
    Logger logger=LoggerFactory.getLogger(ProjectProviderSerivceImpl.class);
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void saveProject(ProjectVO projectVO, Integer memberId) {
        // 一.保存ProjectPo对象
        // 1.创建空的ProjectPo对象
        ProjectPO projectPO=new ProjectPO();

        // 2.把projectVo中的属性复制过来
        BeanUtils.copyProperties(projectVO,projectPO);

        // 把memberID设置到projectPo中
        projectPO.setMemberid(memberId);

        // 保存创建时间
        String createDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        projectPO.setCreatedate(createDate);

        // status设置成0，表示即将开始
        projectPO.setStatus(0);

        // 3.保存projectPo
        // 为了能获取到projectPo保存后的自增主键，需要到ProjectPOMapper.xml中进行相关设置
       projectPOMapper.insertSelective(projectPO);

       // 4.从projectPo对象这里获取自增主键
        Integer projectId=  projectPO.getId();

        // 二、保存项目、分类中的关联信息
        // 1.从ProjectVo对象中获取typeIdList
        List<Integer> typeIdList= projectVO.getTypeIdList();
        projectPOMapper.insertTypeRelationship(typeIdList,projectId);

        // 三、保存项目、标签的管理关系信息
       List<Integer> tagIdList= projectVO.getTagIdList();
        projectPOMapper.insertTagRelationship(tagIdList,projectId);

        // 四、保存项目中详情图片路径信息
        List<String> detailPicturePathList=projectVO.getDetailPicturePathList();
        projectItemPicPOMapper.insertPathList(projectId,detailPicturePathList);

        // 五、保存项目发起人信息
        MemberLauchInfoVO memberLaunchInfoVO=projectVO.getMemberLauchInfoVO();
        MemberLaunchInfoPO memberLaunchInfoPO=new MemberLaunchInfoPO();
        BeanUtils.copyProperties(memberLaunchInfoVO,memberLaunchInfoPO);
        memberLaunchInfoPO.setMemberid(memberId);

        memberLaunchInfoPOMapper.insert(memberLaunchInfoPO);

        // 六 保存项目回报信息
        List<ReturnVO> returnVOList=projectVO.getReturnVOList();
        List<ReturnPO> returnPOList=new ArrayList<ReturnPO>();

        for (ReturnVO returnVO: returnVOList){
            ReturnPO returnPO=new ReturnPO();
            BeanUtils.copyProperties(returnVO,returnPO);
            returnPOList.add(returnPO);
        }
        returnPOMapper.insertReturnPOBatch(returnPOList,projectId);

        // 七、保存项目确认信息
        MemberConfirmInfoVO memberConfirmInfoVO=projectVO.getMemberConfirmInfoVO();
        MemberConfirmInfoPO memberConfirmInfoPO=new MemberConfirmInfoPO();
        BeanUtils.copyProperties(memberConfirmInfoVO,memberConfirmInfoPO);
        memberConfirmInfoPOMapper.insert(memberConfirmInfoPO);
    }

    public List<PortalTypeVO> getPortalTypeVo() {
        return projectPOMapper.selectPortalTypeVOList();
    }

    public DetailProjectVO selectDetailProjectVO(Integer projectId) {
        // 查询的得到的DetailProjectVO对象
        DetailProjectVO detailProjectVO = projectPOMapper.selectDetailProjectVO(projectId);
        // 根据status确定statusText
        Integer status=detailProjectVO.getStatus();
        switch (status){
            case 0:
                detailProjectVO.setStatusText("审核中");
                break;
            case 1:
                detailProjectVO.setStatusText("众筹中");
                break;
            case 2:
                detailProjectVO.setStatusText("众筹成功");
                break;
            case 3:
                detailProjectVO.setStatusText("已关闭");
                break;
            default:
                break;
        }

        // 3.根据deployeDate计算lastDay
        // 2020-10-15
        String deployDate = detailProjectVO.getDeployDate();

        // 获取当前日期
        Date currentDay=new Date();

        // 把众筹日期解析成 Date 类型
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date deployDay=format.parse(deployDate);

            // 获取当前日期时间戳
            long CurrentTimeStamp=currentDay.getTime();

            // 获取众筹时间戳
            long deployTimeStamp=deployDay.getTime();

            // 两个时间相减计算一过去的时间
            long pastDays=(CurrentTimeStamp-deployTimeStamp)/1000/60/60/24;

            // 获取总的众筹天数
            Integer totalDays=detailProjectVO.getDay();
            // 使用总的众筹天数减去已经过去的天数得到剩余天数
            Integer lastDay=(int) (totalDays-pastDays);
            detailProjectVO.setLastDay(lastDay);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return detailProjectVO;
    }


}
