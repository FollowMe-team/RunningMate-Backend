package com.follow_me.running_mate.config;

import java.lang.reflect.Method;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcRegistrations webMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new CustomRequestMappingHandlerMapping();
            }
        };
    }

    private static class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
            RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
            if(info != null) {
                if(AnnotatedElementUtils.hasAnnotation(handlerType, RestController.class)
                    && !handlerType.getPackageName().contains("springdoc")) {
                    info = RequestMappingInfo.paths("/api")
                        .build()
                        .combine(info);
                }
            }
            return info;
        }
    }
}
