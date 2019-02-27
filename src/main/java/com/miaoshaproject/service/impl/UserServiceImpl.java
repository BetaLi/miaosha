package com.miaoshaproject.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPassword;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidatorImpl validator;


    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO =  userDOMapper.selectByPrimaryKey(id);
        if(userDO ==null){
            return null;
        }
        // 通过用户id获取用户的加密密码信息
        UserPassword userPassword = userPasswordMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPassword);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender()==null
//                || userModel.getAge()==null
//                || StringUtils.isEmpty(userModel.getTelphone())){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        // 实现model->dataObject方法
        UserDO userDO = convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        }catch(DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已重复注册");
        }

        UserPassword userPassword = convertPasswordFromModel(userModel);
        userPassword.setUserId(userDO.getId());
        userPasswordMapper.insertSelective(userPassword);

        return;
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        // 通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO==null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPassword);

        //比对用户信息内加密的密码是否匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserPassword convertPasswordFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserPassword userPassword = new UserPassword();
        BeanUtils.copyProperties(userModel,userPassword);
        return userPassword;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserDO userDO = new UserDO();

        BeanUtils.copyProperties(userModel,userDO);

        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPassword userPassword){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPassword != null){
            userModel.setEncrptPassword(userPassword.getEncrptPassword());
        }

        return userModel;
    }
}
