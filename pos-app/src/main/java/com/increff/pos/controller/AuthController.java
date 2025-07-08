package com.increff.pos.controller;

import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.util.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(tags = "Authentication")
@RestController
@RequestMapping("api/auth")
public class AuthController {

   private final AuthenticationManager authManager;

   public AuthController(AuthenticationManager authManager) {
       this.authManager = authManager;
   }

   @ApiOperation("User login")
   @PostMapping("/login")
   public ResponseEntity<LoginData> login(@RequestBody LoginForm form, HttpSession session) {
       UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
               form.getEmail(), form.getPassword()
       );

       Authentication auth = authManager.authenticate(token);
       SecurityContextHolder.getContext().setAuthentication(auth);
       session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

       LoginData data = new LoginData();
       
       // Handle both UserPrincipal and default User types
       if (auth.getPrincipal() instanceof UserPrincipal) {
           UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
           data.setEmail(principal.getEmail());
           data.setRole(principal.getRole());
       } else if (auth.getPrincipal() instanceof User) {
           User user = (User) auth.getPrincipal();
           data.setEmail(user.getUsername());
           data.setRole(user.getAuthorities().iterator().next().getAuthority());
       } else {
           data.setEmail(auth.getName());
           data.setRole(auth.getAuthorities().iterator().next().getAuthority());
       }
       
       return ResponseEntity.ok(data);
   }

   @ApiOperation("User logout")
   @PostMapping("/logout")
   public ResponseEntity<String> logout(HttpSession session) {
       session.invalidate();
       SecurityContextHolder.clearContext();
       return ResponseEntity.ok("Logged out successfully");
   }
}
