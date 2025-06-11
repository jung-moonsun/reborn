package com.ms.reborn.global.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebForwardController {

    @RequestMapping(value = { "/", "/products/**", "/chat/**", "/my/**", "/login", "/signup" })
    public String forward() {
        return "forward:/index.html";
    }
}