package com.digisprint.EmailUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

@Configuration
public class LoadHtmlTemplates {

	@Autowired
	ResourceLoader resourceLoader;
	
	public String loadTemplate(String filePath) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:"+filePath);
		String body = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		return body;
    }

}
