package com.miaoshaproject;

import static org.junit.Assert.assertTrue;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.service.model.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest 
{
    @Autowired
    private UserDOMapper userDOMapper;


    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        System.out.println("_________SelectAll method test__________");
        UserDO userDO= userDOMapper.selectByPrimaryKey(1);
        UserModel userModel = convertModelFromDO(userDO);
        System.out.println(userModel);
        assertTrue( true );
    }

    public UserModel convertModelFromDO(UserDO userDO){
        UserModel userModel = new UserModel();
        if(userDO==null){
            return null;
        }
        BeanUtils.copyProperties(userDO,userModel);
        return userModel;
    }
}
