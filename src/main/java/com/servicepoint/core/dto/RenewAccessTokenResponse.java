package com.servicepoint.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenewAccessTokenResponse {
    private String accessToken;
    private Date accessTokenExpiresAt;
    private Long accessTokenExpiresIn; // in milliseconds
}
