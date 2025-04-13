package com.bishe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers(
//                    "/swagger-ui/**",
//                    "/swagger-resources/**",
//                    "/v2/api-docs/**",
//                    "/v3/api-docs/**",
//                    "/doc.html",
//                    "/webjars/**"
//                ).permitAll()  // 允许 Swagger 访问
//                .anyRequest().authenticated()
//            .and()
//                .formLogin()
//            .and()
//                .csrf().disable(); // 关闭 CSRF，否则可能影响 Swagger
//    }
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll() // 允许所有请求
                    .and()
                    .csrf().disable(); // 禁用 CSRF 保护
        }
}
