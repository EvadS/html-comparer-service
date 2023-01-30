package com.se.sample.config;


import com.se.sample.controller.FileController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ProjectConstants.PATH_PREFIX, HandlerTypePredicate.forBasePackageClass(FileController.class));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(ProjectConstants.COMPARISON_API_PATH + "**")
                .addResourceLocations("file:" + "/");
    }
}

