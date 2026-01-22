package com.library.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleActionRequest {

    private Long userId;
    private String role;     // ROLE_LIBRARIAN, ROLE_ADMIN
    private String action;   // ASSIGN or REVOKE
}
