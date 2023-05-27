package com.mogakko.be_final.domain.members.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @GetMapping("/api/main")
    @Operation(summary = "Test API", description = "소셜로그인 후에 마땅한 엔드포인트가 없어서 간단히 만들어뒀습니다. 삭제예정.")
    public String hello() {
        return "{ \"message\" : \"Hello\" }";
    }

}