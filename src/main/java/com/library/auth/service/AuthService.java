package com.library.auth.service;

import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.LoginResponse;
import com.library.auth.dto.RegisterRequest;
import com.library.auth.dto.RegisteredResponse;
import com.library.user.entity.Users;
import org.apache.coyote.BadRequestException;

import java.util.Map;

public interface AuthService {

    //register user only
    public Users registerUser(RegisterRequest request) throws BadRequestException;

    //admin registration only
    Map<String, Object> registerAdmin(RegisterRequest request) throws BadRequestException;

    //Librarian registration only
    Map<String, Object> registerLibrarian (RegisterRequest request) throws BadRequestException;

    //verification link/password reset sending via mail
    String verifyLink(String email, String code) throws BadRequestException;

    //login for all roles
    LoginResponse login (LoginRequest loginRequest);

    //forget pwd for all roles with mail checking
    void forgetPassword(String email);

    //reset pwd for all roles
    String forgetPasswordReset(String code, String newPassword);
}


