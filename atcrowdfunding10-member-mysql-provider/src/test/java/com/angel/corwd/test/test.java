package com.angel.corwd.test;

import com.angel.crowd.entity.vo.DetailProjectVO;
import com.angel.crowd.mapper.ProjectPOMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author 刘振河
 * @create 2020--05--11 11:09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes =ProjectPOMapper.class )
public class test {
    @Autowired
    private ProjectPOMapper mapper;

    Logger logger=LoggerFactory.getLogger(test.class);
    @Test
    public void  test(){
        DetailProjectVO detailProjectVO = mapper.selectDetailProjectVO(7);
        logger.error(detailProjectVO.getProjectName()+detailProjectVO.getDeployDate()+detailProjectVO.getLastDay());
    }
}
