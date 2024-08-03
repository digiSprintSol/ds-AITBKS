package com.digisprint.filter;

import java.io.IOException;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.digisprint.bean.AccessBean;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import io.jsonwebtoken.impl.crypto.JwtSignatureValidator;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class JwtTokenValidator implements Filter {

	AccessBeanRepository accessBeanRepository;

	@Value("${config.secretKey}")
	private String secretKey;

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
		log.debug("Cutom Filter- doFilter");
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse=(HttpServletResponse) response;
		httpServletResponse.setHeader(ApplicationConstants.ALLOW_CROS_ORIGIN, ApplicationConstants.ALLOW_ORIGINS);

		String token = httpServletRequest.getHeader(ApplicationConstants.TOKEN);
		if (token != null && !((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.LOGIN) 
				&& !((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.SWAGGER)
				&& !((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.API_DOCS)
				&& !((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.ACTUATOR)) {
			Base64.Decoder decoder = Base64.getUrlDecoder();
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
			JwtSignatureValidator validator = new DefaultJwtSignatureValidator(SignatureAlgorithm.HS256, secretKeySpec);
			String[] chunks = token.split("\\.");
			if (chunks.length < 2) {
				((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
				((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(),ErrorResponseConstants.NOT_AUTHORISED);
			} else {
				String tokenWithoutSignature = chunks[0] + "." + chunks[1];
				String signature = chunks[2];
				if (!validator.isValid(tokenWithoutSignature, signature)) {
					Exception e = new Exception(ErrorResponseConstants.USER_ALREADY_LOGGEDIN);
					ObjectMapper mapper = new ObjectMapper();
					response.getWriter().write(mapper.writeValueAsString(e));
					((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(),ErrorResponseConstants.NOT_AUTHORISED);
				}else {
					String body = new String(decoder.decode(chunks[1]));
					JSONObject jsonObject = new JSONObject(body);
					JSONArray array = (JSONArray) jsonObject.get(ApplicationConstants.ACCESS);

					chain.doFilter(request, response);
				}
			}
		}else if (((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.LOGIN) 
				|| ((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.SWAGGER)
				|| ((HttpServletRequest) request).getRequestURI().contains(ApplicationConstants.API_DOCS)) {
			chain.doFilter(request, response);
		} else {
			((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
			((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(),ErrorResponseConstants.NOT_AUTHORISED);
		}
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
