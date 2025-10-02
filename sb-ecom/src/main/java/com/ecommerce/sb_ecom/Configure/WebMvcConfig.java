package com.ecommerce.sb_ecom.Configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/images/**").addResourceLocations("file:images/");
        String imagePath = System.getProperty("user.dir") + "/images/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath);
    }
}
