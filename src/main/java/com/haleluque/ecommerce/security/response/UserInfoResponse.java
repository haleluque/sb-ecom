package com.haleluque.ecommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private List<String> roles;
    //private String jwtToken; //No longer needed as we implemented cookie based auth
}
