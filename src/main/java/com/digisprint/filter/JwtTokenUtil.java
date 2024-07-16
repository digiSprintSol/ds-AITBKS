package com.digisprint.filter;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	@Value("${config.secretKey}")
	private String secretKey;
	
	public static JSONObject decodeUserToken(String token) {
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String body = new String(decoder.decode(chunks[1]));
		JSONObject jsonObject = new JSONObject(body);
		System.out.println("======================jsonObject===============      29   "+jsonObject);
		return jsonObject;
	}
	
	public String getUserName(String token){
		JSONObject object = decodeUserToken(token);
		if(object != null)
			return object.getString("userName");
		else 
			return null;
	}
	
	public  List<Object> getAccessList(String token){
		JSONObject object = decodeUserToken(token);
		JSONArray accessList = null;
		if(object != null)
			accessList = object.getJSONArray("access");
		if(accessList != null)
			return accessList.toList();
		
		return null;	
	}
	
	public String generateToken(String userName, String userId,List<String> access, String type) {
		Calendar currentTimeNow = Calendar.getInstance();
		currentTimeNow.add(Calendar.MINUTE, 30);
		Date expireTime = currentTimeNow.getTime();
		JwtBuilder token = Jwts.builder()
				          .setSubject("access")
				          .setHeaderParam("typ", "JWT")
				          .claim("userName", userName)
				          .claim("userId", userId)
				          .claim("type", type)
				          .claim("access",access)				
				          .setIssuedAt(currentTimeNow.getTime())
				          .setExpiration(expireTime)
				          .signWith(SignatureAlgorithm.HS256, secretKey);
		return token.compact();
	}
}
