package com.haleluque.ecommerce.controller;

import com.haleluque.ecommerce.model.AppRole;
import com.haleluque.ecommerce.model.Role;
import com.haleluque.ecommerce.model.User;
import com.haleluque.ecommerce.repositories.RoleRepository;
import com.haleluque.ecommerce.repositories.UserRepository;
import com.haleluque.ecommerce.security.jwt.JwtUtils;
import com.haleluque.ecommerce.security.request.LoginRequest;
import com.haleluque.ecommerce.security.request.SignupRequest;
import com.haleluque.ecommerce.security.response.MessageResponse;
import com.haleluque.ecommerce.security.response.UserInfoResponse;
import com.haleluque.ecommerce.security.services.UserDetailsImpl;
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
import org.springframework.security.core.GrantedAuthority;
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

    /**
     * Extracts the userName from the token, using spring security authentication principal
     */
    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }

    /**
     * Extracts the user details from the token, using spring security authentication principal
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            //Authenticates against Spring security
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        //Setting the authentication into the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //retrieve the users details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //generate the jwt and return it as a cookie
        ResponseCookie jwtCookieToken = jwtUtils.generateJwtCookie(userDetails);

        //extracts the role and create the response
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);

        //We need to send the cookie as a header
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                jwtCookieToken.toString())
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

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

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = jwtUtils.cleanJwtCookie();
        //It is needed to update the cookie with the null value
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}
