package com.ecommerce.sb_ecom.Controller;

import com.ecommerce.sb_ecom.Model.AppRole;
import com.ecommerce.sb_ecom.Model.Role;
import com.ecommerce.sb_ecom.Model.User;
import com.ecommerce.sb_ecom.Repositry.RoleRepository;
import com.ecommerce.sb_ecom.Repositry.UserRepository;
import com.ecommerce.sb_ecom.security.jwt.JwtUtils;

import com.ecommerce.sb_ecom.security.request.LoginRequest;
import com.ecommerce.sb_ecom.security.request.SignupRequest;
import com.ecommerce.sb_ecom.security.response.MessageResponse;
import com.ecommerce.sb_ecom.security.response.UserInfoResponse;
import com.ecommerce.sb_ecom.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;




      @Autowired
    PasswordEncoder encoder;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles,jwtCookie.toString());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                jwtCookie.toString())
                .body(response);
    }
          @PostMapping("/signup")
    public ResponseEntity<?>registerUser( @Valid @RequestBody SignupRequest signupRequest){
              if (userRepository.existsByUserName(signupRequest.getUsername())) {
                  return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
              }

              if (userRepository.existsByEmail(signupRequest.getEmail())) {
                  return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
              }

              User user= new User(
                      signupRequest.getUsername(),
                      signupRequest.getEmail(),
                    encoder.encode(signupRequest.getPassword())
              );

              Set<String> strRoles=signupRequest.getRole();
              Set<Role> roles= new HashSet<>();


              if (strRoles == null) {
                  Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                  roles.add(userRole);
              } else {
                  strRoles.forEach(role -> {
                      switch (role) {
                          case "admin":
                              Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                              roles.add(adminRole);

                              break;
                          case "seller":
                              Role modRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                              roles.add(modRole);

                              break;
                          default:
                              Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                              roles.add(userRole);
                      }
                  });
              }

              user.setRoles(roles);
              userRepository.save(user);

              return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
          }

          @GetMapping("/username")
          public String currentUsername(Authentication authentication){
              if(authentication!=null){
                  return authentication.getName();
              }
              else{
                  return "Null";
              }
          }
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(response);

    }
    @PostMapping("/signout")
    public ResponseEntity<?>signoutUser(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You have been sing Out"));
    }

}
