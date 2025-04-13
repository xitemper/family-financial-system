package com.bishe.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 *
 * @author mac
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig extends SuperSwaggerConfig {


    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
        		//设置Swagger扫描的Controller路径，只有扫描到了才会生成接口文档
                .apiBasePackage("com.bishe.controller")
                .title("家庭财务系统后端接口文档")
                .description("家庭财务系统后端接口文档")
                .contactName("lzh")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }

}

