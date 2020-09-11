package com.yang;

import com.yang.interceptor.MyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yg
 * @date 2020/8/12 11:03
 */
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classPath:/META-INF/resources/")
                .addResourceLocations("file:D:/workspace/workspace_03/video_dev/");
    }

    @Bean
    public MyInterceptor myInterceptor() {
        return new MyInterceptor();
    }

    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient() {
        return new ZKCuratorClient();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor())
                .addPathPatterns("/user/**")
                .addPathPatterns("/bgm/**")
                .addPathPatterns("/video/uploadVideo", "/video/uploadVideoCover")
                .addPathPatterns("/video/usersLike", "/video/usersUnLike", "/video/saveComment")
                .excludePathPatterns("/user/queryPublisher");
    }
}
