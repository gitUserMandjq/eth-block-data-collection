package com.eth.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/index")
    public String index(){
        return "home"; //当浏览器输入/index时，会返回 /static/home.html的页面
    }
}
