package com.imooc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/*
 @Controller - 静态页面
             - ModelAndView：返回页面和相应数据
 json字符串：如果某个类设计初衷就是返回json字符串，
 那么该类就可以使用@Controller + @ResponseBody
 简写为  @RestController
 */
// @RestController 返回 REST 接口对象也就是 json
@RestController
public class HelloWorldController {

	@RequestMapping("/hello")
	public String Hello() {
		return "Hello Spring Boot~";
	}
	
}
