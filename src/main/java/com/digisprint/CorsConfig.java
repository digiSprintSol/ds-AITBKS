package com.digisprint;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedHeaders("*").allowedMethods("HEAD","POST","GET","PUT","PATCH","DELETE").allowedOrigins("*");
//    }
    
	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**")
	        .allowedHeaders("*","http://localhost:3000")
	        .allowedMethods("HEAD","POST","GET","PUT","PATCH","DELETE")
	        .allowedOrigins("*")
	        .maxAge(3600);
	    }
}