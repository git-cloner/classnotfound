package com.gitclone.classnotfound.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.gitclone.classnotfound.model.Response;

@Controller
public class TestController {

	@GetMapping("/test")
    public Response<Map<String, Object>> get(){
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> test = new HashMap<>();
        test.put("test", "1234567890");
        response.setData(test);
        return  response;
    }
}
