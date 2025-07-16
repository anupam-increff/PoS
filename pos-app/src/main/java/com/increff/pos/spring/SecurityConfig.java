package com.increff.pos.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   @Override
   protected void configure(HttpSecurity http) throws Exception {
       http
               .cors().configurationSource(corsConfigurationSource())
               .and()
               .csrf().disable()
               .authorizeRequests()
               .antMatchers("/api/auth/login").permitAll()
               // Update endpoints - supervisor only
               .antMatchers( "/api/invoice/**").hasAuthority("supervisor")
               .antMatchers(HttpMethod.PUT, "/api/product/**").hasAuthority("supervisor")
               .antMatchers(HttpMethod.PUT, "/api/inventory/**").hasAuthority("supervisor")
               // Admin endpoints
               .antMatchers("/api/reports/**").hasAuthority("supervisor")
               .antMatchers("/api/report/**").hasAuthority("supervisor")
               // General API access
               .antMatchers("/api/**").hasAnyAuthority("operator", "supervisor")
               .anyRequest().authenticated()
               .and()
               .formLogin().disable()
               .httpBasic().disable()
               .exceptionHandling()
               .accessDeniedHandler(accessDeniedHandler())
               .authenticationEntryPoint(authenticationEntryPoint())
               .and()
               .logout()
               .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
               .logoutSuccessHandler((request, response, auth) -> {
                   response.setStatus(200);
                   response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                   response.getWriter().write("{\"message\":\"Logged out successfully\"}");
               })
               .invalidateHttpSession(true)
               .deleteCookies("JSESSIONID")
               .and()
               .sessionManagement()
               .maximumSessions(1)
               .and()
               .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
               .invalidSessionUrl("/api/auth/session-expired")
               .sessionFixation().migrateSession();
   }
   
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();
       configuration.addAllowedOrigin("http://localhost:4200"); // Frontend URL
       configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS, etc.
       configuration.addAllowedHeader("*");
       configuration.setAllowCredentials(true); // Important for cookies (JSESSIONID)in producr
       configuration.setMaxAge(3600L); // Cache preflight for 1 hour
       configuration.addExposedHeader("Content-Disposition"); // For file downloads

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", configuration);
       return source;
   }
   
   @Bean
   public AccessDeniedHandler accessDeniedHandler() {
       return new AccessDeniedHandler() {
           @Override
           public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException)
                   throws IOException, ServletException {
               response.setStatus(HttpServletResponse.SC_FORBIDDEN);
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               response.setCharacterEncoding("UTF-8");
               response.getWriter().write("{\"message\":\"Access denied: You don't have permission to perform this action\"}");
           }
       };
   }
   
   @Bean
   public AuthenticationEntryPoint authenticationEntryPoint() {
       return new AuthenticationEntryPoint() {
           @Override
           public void commence(HttpServletRequest request, HttpServletResponse response,
                              AuthenticationException authException)
                   throws IOException, ServletException {
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               response.setCharacterEncoding("UTF-8");
               response.getWriter().write("{\"message\":\"Session expired or invalid. Please login again.\"}");
           }
       };
   }
   
   @Bean
   public AuthenticationFailureHandler authenticationFailureHandler() {
       return new AuthenticationFailureHandler() {
           @Override
           public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException exception)
                   throws IOException, ServletException {
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               response.setCharacterEncoding("UTF-8");
               response.getWriter().write("{\"message\":\"Invalid credentials\"}");
           }
       };
   }
   
   @Bean
   public AuthenticationSuccessHandler authenticationSuccessHandler() {
       return new AuthenticationSuccessHandler() {
           @Override
           public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication)
                   throws IOException, ServletException {
               response.setStatus(HttpServletResponse.SC_OK);
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               response.setCharacterEncoding("UTF-8");
               
               String email = authentication.getName();
               String role = authentication.getAuthorities().iterator().next().getAuthority();
               
               response.getWriter().write("{\"email\":\"" + email + "\",\"role\":\"" + role + "\"}");
           }
       };
   }

   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.inMemoryAuthentication()
               .passwordEncoder(NoOpPasswordEncoder.getInstance())
               .withUser("admin@example.com").password("adminpass").authorities("supervisor")
               .and()
               .withUser("user@example.com").password("userpass").authorities("operator");
   }
   
   @Bean
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
       return super.authenticationManagerBean();
   }

}
