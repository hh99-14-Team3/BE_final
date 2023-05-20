/**
 * TODO : swagger에 토큰 넣어서 진행하는 방법
 * 우리 프로젝트에 버전이 맞을지 몰라서 일단 주석처리하여 업로드함
 */



//package com.example.be_final.config;
//
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SwaggerConfig {
//    @Bean
//    public OpenAPI OpenAPI() {
//
//        Info info = new Info()
//                .version("v1.0.0")
//                .title("프로젝트 명")
//                .description("프로젝트 설명");
//
//        //SecurityScheme명
//        String jwtSchemeName = "jwtAuth";
//        //API 요청헤더에 인증정보 포함
//        SecurityRequirement securityRequirement = new io.swagger.v3.oas.models.security.SecurityRequirement().addList(jwtSchemeName);
//        //SecuritySchemes 등록
//        Components components = new Components()
//                .addSecuritySchemes(jwtSchemeName, new io.swagger.v3.oas.models.security.SecurityScheme()
//                        .name(jwtSchemeName)
//                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP) //HTTP방식
//                        .scheme("bearer")
//                        .bearerFormat("JWT")); //토큰 형식을 지정하는 임의의 문자
//
//        return new OpenAPI()
//                .info(info)
//                .addSecurityItem(securityRequirement)
//                .components(components);
//    }
//}