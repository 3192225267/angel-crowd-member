package com.angel.crowd.service.api;

import com.angel.crowd.entity.vo.DetailProjectVO;
import com.angel.crowd.entity.vo.PortalTypeVO;
import com.angel.crowd.entity.vo.ProjectVO;

import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--06 9:17
 */
public interface ProjectProviderSerivce {

   void saveProject(ProjectVO projectVO,Integer memberId);
   List<PortalTypeVO> getPortalTypeVo();
   DetailProjectVO selectDetailProjectVO(Integer projectId);
}
