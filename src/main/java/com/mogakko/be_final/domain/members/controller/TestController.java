package com.mogakko.be_final.domain.members.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @GetMapping("/")
    @Operation(summary = "ELB 엔드포인트 API", description = "로드밸런서 health check 위한 엔드포인트로 사용")
    public String hello() {
        return "{ \"message\" : \"Hello\" }";
    }
}