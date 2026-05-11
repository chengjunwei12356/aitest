package com.example.aitest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 配置
 * 访问地址: http://localhost:8080/swagger-ui.html
 * API文档: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("银行信贷管理系统 API")
                        .version("1.0.0")
                        .description("银行信贷管理系统 REST API 文档\n\n" +
                                "**主要功能模块:**\n" +
                                "- 认证管理 (登录、验证码)\n" +
                                "- 用户管理\n" +
                                "- 角色管理\n" +
                                "- 贷款申请管理\n" +
                                "- 审批管理\n" +
                                "- 客户管理\n" +
                                "- 客户经理助手\n" +
                                "- 公告管理\n" +
                                "- 文档管理\n" +
                                "- 工作台统计\n")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
