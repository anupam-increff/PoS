package com.increff.pos.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
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
               .cors()
               .and()
               .csrf().disable()
               .authorizeRequests()
               .antMatchers("/api/auth/login").permitAll()
               // Upload endpoints - admin only
               .antMatchers("/api/product/upload-tsv").hasAuthority("supervisor")
               .antMatchers("/api/inventory/upload").hasAuthority("supervisor")
               // Admin endpoints
               .antMatchers("/api/admin/**").hasAuthority("supervisor")
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
               .and()
               .logout()
               .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
               .logoutSuccessHandler((request, response, auth) -> response.setStatus(200))
               .invalidateHttpSession(true)
               .deleteCookies("JSESSIONID");
   }
   
   @Bean
   public AccessDeniedHandler accessDeniedHandler() {
       return new AccessDeniedHandler() {
           @Override
           public void handle(HttpServletRequest request, HttpServletResponse response,
                           org.springframework.security.access.AccessDeniedException accessDeniedException)
                   throws IOException, ServletException {
               response.setStatus(HttpServletResponse.SC_FORBIDDEN);
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               response.setCharacterEncoding("UTF-8");
               response.getWriter().write("{\"message\":\"Access denied: You don't have permission to perform this action\"}");
           }
       };
   }
   
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();
       configuration.addAllowedOrigin("http://localhost:4200"); // Frontend URL
       configuration.addAllowedMethod("*"); // GET, POST, PUT, etc.
       configuration.addAllowedHeader("*");
       configuration.setAllowCredentials(true); // Important for cookies (JSESSIONID)
       configuration.setMaxAge(3600L); // Cache preflight for 1 hour

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", configuration);
       return source;
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
