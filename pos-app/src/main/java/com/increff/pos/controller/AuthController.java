package com.increff.pos.controller;

import com.increff.pos.dto.AuthDto;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.data.LoginData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(tags = "Authentication")
@RestController
@RequestMapping("api/auth")
public class AuthController {

   @Autowired
   private AuthDto authDto;

   @ApiOperation("User login")
   @PostMapping("/login")
   public ResponseEntity<LoginData> login(@RequestBody LoginForm form, HttpSession session) {
       LoginData data = authDto.login(form, session);
       return ResponseEntity.ok(data);
   }

   @ApiOperation("User logout")
   @PostMapping("/logout")
   public ResponseEntity<String> logout(HttpSession session) {
       authDto.logout(session);
       return ResponseEntity.ok("Logged out successfully");
   }

   @ApiOperation("Check session validity")
   @GetMapping("/session-check")
   public ResponseEntity<Boolean> checkSession(HttpSession session) {
       boolean isValid = authDto.isSessionValid(session);
       return ResponseEntity.ok(isValid);
   }
}
