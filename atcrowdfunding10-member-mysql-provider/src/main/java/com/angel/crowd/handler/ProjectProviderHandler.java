package com.angel.crowd.handler;

import com.angel.crowd.entity.vo.DetailProjectVO;
import com.angel.crowd.entity.vo.PortalTypeVO;
import com.angel.crowd.entity.vo.ProjectVO;
import com.angel.crowd.service.api.ProjectProviderSerivce;
import com.angel.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 刘振河
 * @create 2020--05--06 9:17
 */
@RestController
public class ProjectProviderHandler {
    @Autowired
    private ProjectProviderSerivce projectService;

    @RequestMapping("/save/project/vo/remote")
    public ResultEntity<String> saveProjectVORemote(
            @RequestBody ProjectVO projectVO,
            @RequestParam("memberId") Integer memberId) {

        try {
            // 调用“本地”Service执行保存
            projectService.saveProject(projectVO, memberId);

            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            e.printStackTrace();

            return ResultEntity.failed(e.getMessage());
        }

    }

    @RequestMapping("/get/portal/type/project/data/remote")
    public ResultEntity<List<PortalTypeVO>> getPortalTypeProjectDataRemote(){
        try {
            List<PortalTypeVO> typeVOList=   projectService.getPortalTypeVo();
            return ResultEntity.successWithData(typeVOList);
        }catch (Exception e){
        e.printStackTrace();
        return ResultEntity.failed(e.getMessage());
        }
    }
    @RequestMapping("/get/project/detail/remote/{projectId}")
    public ResultEntity<DetailProjectVO> getDetailProjectVORemote(@PathVariable("projectId") Integer projectId){
   try {
       DetailProjectVO detailProjectVO = projectService.selectDetailProjectVO(projectId);
       return ResultEntity.successWithData(detailProjectVO);
   }catch (Exception e){
       e.printStackTrace();
       return ResultEntity.failed(e.getMessage());
   }
    }

}
