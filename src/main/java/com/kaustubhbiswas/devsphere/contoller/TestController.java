package com.kaustubhbiswas.devsphere.contoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaustubhbiswas.devsphere.common.response.ApiResponse;


@RestController
@RequestMapping("/api")

public class TestController {

    @GetMapping("/test")

    public ApiResponse<String> test(){
        return ApiResponse.success("API is working", "Hello DevSphere");
    }


}
