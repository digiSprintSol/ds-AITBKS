package com.digisprint.security;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 
import com.digisprint.filter.JwtRequestFilter;
import com.digisprint.utils.ApplicationConstants;
 
 
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
 
	@Autowired
	private UserDetailsService jwtUserDetailsService;
 
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
 
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService);
	}
 
	@SuppressWarnings("deprecation")
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/**
		 * Generating and validating unique tokens for each user session to ensure that requests originate from the 
		 * legitimate website.Setting the SameSite attribute for cookies to restrict their usage to same-site requests 
		 * only. We don't need this as of now, that's the reason this is setup to disable.
		 */
		httpSecurity.cors().and().csrf().disable()
				.authorizeRequests().antMatchers("/**")
				.permitAll()
				.anyRequest() .authenticated()
				.and().
				exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
 
		/* Adding a filter to validate the tokens with every request */
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		System.out.println("Added filter");
	}
}