package com.servicepoint.core.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter
public class UserInfo {
     private Integer id;
     private String username;
     private String email;
     private String role;
     private String phoneNumber;
     private String profilePicture;

     public  UserInfo(Integer id, String username, String email, String role, String phoneNumber){
         this.id = id;
         this.username = username;
         this.email = email;
         this.phoneNumber = phoneNumber;

     }

}