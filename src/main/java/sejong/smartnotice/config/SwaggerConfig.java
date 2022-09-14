package sejong.smartnotice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 연결 링크 >>> http://localhost:8080/swagger-ui/index.html
@EnableSwagger2 // Enables Springfox swagger 2
@Configuration
public class SwaggerConfig {

    // Docket: Springfox's primary api configuration mechanism
    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select() // returns an instance of ApiSelectorBuilder to give fine grained control over the endpoints exposed via swagger.
                .apis(RequestHandlerSelectors.basePackage("sejong.smartnotice.restController")) // 해당 패키지 이하에 있는 api만 보겠다.
                .paths(PathSelectors.ant("/api/**")) // 해당 경로에 해당하는 api만 보겠다.
                .build() // The selector needs to be built after configuring the api and path selectors.
                .useDefaultResponseMessages(false)
                .apiInfo(customApiInfo());
    }

    private ApiInfo customApiInfo() {
        return new ApiInfoBuilder()
                .title("SmartNotice API List")
                .description("스마트 마을알림 시스템 API 목록입니다.")
                .version("0.0.1")
                .contact(new Contact("eheh12321", "#", "eheh12321@gmail.com"))
                .build();
    }
}
