package com.developersarena.ecommerce.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
}
