package com.tui.Dietetyk_Plus.apiserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserToken {
    private String token;
    private String userId;
    private Date expires;
}
