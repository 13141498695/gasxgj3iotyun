package com.jzkj.controller;


import com.jzkj.annotation.Login;
import com.jzkj.common.utils.ReturnResult;
import com.jzkj.common.validator.ValidatorUtils;
import com.jzkj.form.LoginForm;
import com.jzkj.service.TokenService;
import com.jzkj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 登录接口
 */
@RestController
@RequestMapping("/api")
@Api(tags="登录接口")
public class ApiLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;


    /**
     * @param form
     * @return
     * 	{
		  "code": 0,
		  "data": {
		    "expire": 0,
		    "token": "string"
		  },
		  "msg": "string"
		}
     */
    @PostMapping("login")
    @ApiOperation("登录")
    public ReturnResult login(@RequestBody LoginForm form){
        //表单校验


        //用户登录
        Map<String, Object> map = userService.login(form);
        return ReturnResult.ok(map);
    }

    @Login
    @PostMapping("logout")
    @ApiOperation("退出")
    public ReturnResult logout(@ApiIgnore @RequestAttribute("userId") long userId){
        tokenService.expireToken(userId);
        return ReturnResult.ok();
    }

}
