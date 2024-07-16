package com.digisprint.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.digisprint.bean.AccessBean;
import com.digisprint.repository.AccessBeanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class JwtTokenValidator implements Filter {

	AccessBeanRepository accessBeanRepository;

	@Value("${config.secretKey}")
	private String secretKey;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	private static final String[] EXCLUDED_PATHS = { "/login", "/swagger", "/api-docs", "/actuator" };


	public JwtTokenValidator(AccessBeanRepository accessBeanRepository) {
		super();
		this.accessBeanRepository = accessBeanRepository;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		insertAdminInitialData();
		Filter.super.init(filterConfig);
	}

		@Override
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	            throws IOException, ServletException {
	        log.debug("Custom Filter - doFilter");
	        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
	        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
	        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");

	        String token = httpServletRequest.getHeader("token");
	        if (token != null && !isExcludedPath(httpServletRequest.getRequestURI())) {
	            try {
	            	jwtTokenUtil.decodeUserToken(token);

	                chain.doFilter(request, response);
	            } catch (Exception e) {
	                handleUnauthorized(httpServletResponse, "Invalid token: " + e.getMessage());
	            }
	        } else if (isExcludedPath(httpServletRequest.getRequestURI())) {
	            chain.doFilter(request, response);
	        } else {
	            handleUnauthorized(httpServletResponse, "Token is missing");
	        }
	    }

	    private boolean isExcludedPath(String uri) {
	        for (String path : EXCLUDED_PATHS) {
	            if (uri.contains(path)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.sendError(HttpStatus.UNAUTHORIZED.value(), message);
	    }

	private void insertAdminInitialData() {
		log.info("initial Data loaded");
		if(!accessBeanRepository.findById("Admin").isPresent()) {
			AccessBean admin = new AccessBean();
			admin.setAccessId("Admin");
			admin.setEmail("admin@xyz.com");
			admin.setPassword("Admin123");
			admin.setPresident(true);
			admin.setCommitee(true);
			admin.setAccountant(true);
			admin.setUser(true);
			accessBeanRepository.save(admin);
		}
	}
}
