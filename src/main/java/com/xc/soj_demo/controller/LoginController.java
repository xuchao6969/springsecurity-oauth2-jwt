package com.xc.soj_demo.controller;

import com.xc.soj_demo.entity.User;
import com.xc.soj_demo.service.UserService;
import com.xc.soj_demo.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/sys")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private UserService userService;

    //测试页面
    @RequestMapping("/test")
    public String testThymeleaf(ModelMap model) {
        User user = new User();
        user.setUsername("盖聂");
        user.setUserRole("大叔");
        model.addAttribute("user", user);
        return "/viewTest";
    }

    //登录页面
    @RequestMapping("/login")
    public String login(ModelMap model) {
        return "/login";
    }

    @RequestMapping(value = "/doLogin")
    @ResponseBody
    public String login(User user) {
        Map<String, Object> resultMap = userService.login(user.getUsername(), user.getPassword());
        String str = JsonUtil.map2Json(resultMap);
        logger.info(str);
        return str;
    }


    //测试页面
    @RequestMapping("/getList")
    @ResponseBody
    public List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");

        return list;
    }
}
