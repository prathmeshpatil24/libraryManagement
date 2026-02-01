package com.library.auth.service;


import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.LoginResponse;
import com.library.auth.dto.RegisterRequest;
import com.library.auth.enums.EmailPurpose;
import com.library.common.exception.DuplicateResourceException;
import com.library.common.exception.EmailException;
import com.library.common.exception.UserNotFoundException;
import com.library.common.security.CustomUserDetails;
import com.library.common.security.JWTService;
import com.library.common.utils.service.EmailServiceImpl;
import com.library.user.entity.Roles;
import com.library.user.entity.Users;
import com.library.user.repository.RolesRepository;
import com.library.user.repository.UsersRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{


    private final UsersRepository usersRepository;

    private final RolesRepository rolesRepository;

    private final EmailServiceImpl emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;
    
    
    @Override
    public Users registerUser(RegisterRequest request) throws BadRequestException {
        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("user with this email is already present, please try new mail id or login with valid credentials");
        }

        if (usersRepository.findByMobileNo(request.getMobileNo()).isPresent()) {
            throw new BadRequestException("user with this mobileNo is already present, please try new mobile no");
        }

        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }


        try {
            // Create new user entity
            Users user = new Users();
            user.setName(request.getName());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setMobileNo(request.getMobileNo());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPasswordSet(true);
            user.setProvider("LOCAL");

            //for testing purpose
         /*
         user.setIsActive(true);
        String code = UUID.randomUUID().toString().substring(0, 6); // 6-digit code
        user.setVerificationCode(null);
        */
            // for varification link
            String token = UUID.randomUUID().toString();
            user.setVerificationCode(token);
            user.setIsActive(false);

            // Assign default role
            Roles roleUser = rolesRepository.findByRoleName("ROLE_USER")
                    //.findById(1)
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));

            user.getRoles().add(roleUser);

            Users savedUser = usersRepository.save(user);

            //now send email with verification link
            sendVerificationLink(savedUser, EmailPurpose.EMAIL_VERIFICATION);

            return savedUser;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new DataIntegrityViolationException(e.getMessage()); // add proper exception handling here while prod
        }
    }

    //Admin registration with role upgrade if email exists
    @Override
    public Map<String, Object> registerAdmin(RegisterRequest request) throws BadRequestException {

        Map<String, Object> result = new HashMap<>();

        //check mail is present ot not
        Optional<Users> existing = usersRepository.findByEmail(request.getEmail());

        if (existing.isPresent()) {

            Users user = existing.get();

            // User already exists → upgrade role
            Roles adminRole = rolesRepository.findByRoleName("ROLE_ADMIN")
                    //.findById(1)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

            user.getRoles().add(adminRole);

            usersRepository.save(user);
            result.put("user", user);
            result.put("message", "Existing user upgraded to admin successfully.");

            return result;
        }

        // NEW ADMIN (never registered before)
        Users admin = new Users();

        admin.setName(request.getName());
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setMobileNo(request.getMobileNo());
//        admin.setPassword(dto.getPassword());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPasswordSet(true);
        admin.setProvider("LOCAL");

        // Must verify
        String token = UUID.randomUUID().toString();
        admin.setVerificationCode(token);
        admin.setIsActive(false);

        // Assign admin role
        Roles adminRole = rolesRepository.findByRoleName("ROLE_ADMIN")
                //.findById(2)
                .orElseThrow(() -> new BadRequestException("Role ADMIN not found"));

        admin.getRoles().add(adminRole);

        Users savedAdmin = usersRepository.save(admin);

        // Send verification email
        sendVerificationLink(savedAdmin, EmailPurpose.EMAIL_VERIFICATION);

        result.put("user", savedAdmin);
        result.put("message", "Admin registered successfully! Please check your email for verification link.");


        return result;
    }

    @Override
    public Map<String, Object> registerLibrarian(RegisterRequest request) throws BadRequestException {
        Map<String, Object> result = new HashMap<>();

        //check mail is present ot not
        Optional<Users> existing = usersRepository.findByEmail(request.getEmail());

        if (existing.isPresent()) {

            Users user = existing.get();

            // User already exists → upgrade role
            Roles librarianRole = rolesRepository.findByRoleName("ROLE_LIBRARIAN")
                    //.findById(1)
                    .orElseThrow(() -> new RuntimeException("Role ROLE_LIBRARIAN not found"));

            user.getRoles().add(librarianRole);

            usersRepository.save(user);
            result.put("user", user);
            result.put("message", "Existing user upgraded to Librarian successfully.");

            return result;
        }

        // NEW ADMIN (never registered before)
        Users lib = new Users();

        lib.setName(request.getName());
        lib.setUsername(request.getUsername());
        lib.setEmail(request.getEmail());
        lib.setMobileNo(request.getMobileNo());
//        admin.setPassword(dto.getPassword());
        lib.setPassword(passwordEncoder.encode(request.getPassword()));
        lib.setPasswordSet(true);
        lib.setProvider("LOCAL");

        // Must verify
        String token = UUID.randomUUID().toString();
        lib.setVerificationCode(token);
        lib.setIsActive(false);

        // Assign admin role
        Roles librarianRole = rolesRepository.findByRoleName("ROLE_LIBRARIAN")
                //.findById(2)
                .orElseThrow(() -> new BadRequestException("Role LIBRARIAN not found"));

        lib.getRoles().add(librarianRole);

        Users savedLib = usersRepository.save(lib);

        // Send verification email
        sendVerificationLink(savedLib, EmailPurpose.EMAIL_VERIFICATION);

        result.put("user", savedLib);
        result.put("message", "Librarian registered successfully! Please check your email for verification link.");


        return result;
    }

    @Override
    public String verifyLink(String email, String code) throws BadRequestException {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new EmailException("Email not found"));

        try {
            if (users.getIsActive())
                return "Email already verified";

            if (!code.equals(users.getVerificationCode()))
                throw new BadRequestException("Invalid verification link");

            users.setIsActive(true);
            users.setVerificationCode(null);
            usersRepository.save(users);

            String loginUrl = "http://localhost:8089/api/auth/login";

            String message = String.format(
                    "Email verified successfully!%nPlease login to continue: %n%s",
                    loginUrl
            );

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            throw new  BadRequestException(e.getMessage());
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        // 1. Authenticate user (Spring Security handles validation)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Get authenticated principal
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // 3. Fetch full user entity (needed for JWT claims)
        Users userEntity = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email: " + userDetails.getUsername())
                );

        // 4. Generate JWT token
        String token = jwtService.generateAccessToken(userEntity);

        // 5. Extract roles
        List<String> roleList = userEntity.getRoles()
                .stream()
                .map(Roles::getRoleName)
                .toList();

        // 6. Build response
        LoginResponse response = new LoginResponse();
        response.setEmail(userEntity.getEmail());
        response.setToken(token);
        response.setRoles(roleList);

        try {
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forgetPassword(String email) {

        System.out.println("User mail:- " + email);
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("mail not found, enter valid mail id"));

        try {
            String token = UUID.randomUUID().toString();
            System.out.println(token);

            users.setVerificationCode(token);

            usersRepository.save(users);

            sendVerificationLink(users, EmailPurpose.PASSWORD_RESET);

        } catch (Exception e) {
            e.printStackTrace(); // This will show the root cause
            throw new  RuntimeException(e);
        }

    }

    @Override
    public String forgetPasswordReset(String code, String newPassword) {
        try {

            Users user = usersRepository
                    .findByVerificationCode(code)
                    .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);

            usersRepository.save(user);

            return "Password reset successfully. You can login now.";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void sendVerificationLink(Users user, EmailPurpose emailPurpose) {

        switch (emailPurpose) {
            case EMAIL_VERIFICATION:
                String link1 = "http://localhost:8089/api/auth/verify?email="
                        + user.getEmail()
                        + "&code="
                        + user.getVerificationCode();

                String subject1 = "Verify your email address";

                String body1 = verificationMailBody(user.getEmail(), link1);

                try {
                    emailService.mimeEmailForm(user.getEmail(), subject1, body1);

                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to send verification email");
                }
                break;

            case PASSWORD_RESET:
                String link2 = "http://localhost:8089/api/auth/forget-pwd?code="
                        + user.getVerificationCode();

                String subject2 = "Password Reset For Library Management";

                String body2 = resetPasswordMailBody(user.getEmail(), link2);

                try {
                    emailService.mimeEmailForm(user.getEmail(), subject2, body2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;

            default:
                System.out.println("Enter valid case!");
        }

    }

    // for mail verification user and admin
    private String verificationMailBody(String userName, String link){

        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;\">" +

                "<div style=\"max-width: 600px; margin: auto; background: #ffffff; padding: 25px; " +
                "border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +

                "<h2 style=\"color: #333;\">Welcome " + userName + " 👋</h2>" +
                "<p style=\"font-size: 15px; color: #555;\">" +
                "Thank you for registering with us. Please verify your email address by clicking the button below:" +
                "</p>" +

                // Button
                "<a href=\"" + link + "\" " +
                "style=\"display: inline-block; background-color: #4CAF50; color: white; padding: 12px 20px; " +
                "margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 5px;\">" +
                "Verify Email</a>" +

                "<p style=\"font-size: 14px; color: #888;\">" +
                "If the button doesn’t work, copy and paste the following link in your browser:<br>" +
                "<a href=\"" + link + "\" style=\"color: #4CAF50;\">" + link + "</a>" +
                "</p>" +

                "<br/>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "If you did not request this registration, please ignore this email." +
                "</p>" +

                "<br/>" +

                "<p style=\"font-size: 15px; color: #333; font-weight: bold;\">Regards,</p>" +
                "<p style=\"font-size: 15px; color: #555;\">The Support Team</p>" +

                "<hr style=\"margin-top: 30px; border: none; border-top: 1px solid #eee;\"/>" +
                "<p style=\"font-size: 12px; color: #aaa; text-align: center;\">" +
                "This is an automated email. Please do not reply." +
                "</p>" +

                "</div>" +
                "</body>" +
                "</html>";

    }

    //reset password mail for user and admin
    private String resetPasswordMailBody(String userName, String link){
        return  "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;\">" +

                "<div style=\"max-width: 600px; margin: auto; background: #ffffff; padding: 25px; " +
                "border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +

                "<h2 style=\"color: #333;\">Hello " + userName + " 👋</h2>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "We received a request to reset the password for your account." +
                "</p>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "Click the button below to reset your password:" +
                "</p>" +

                // Button
                "<a href=\"" + link + "\" " +
                "style=\"display: inline-block; background-color: #4CAF50; color: white; padding: 12px 20px; " +
                "margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 5px;\">" +
                "Reset Password</a>" +

                "<p style=\"font-size: 14px; color: #888;\">" +
                "If the button doesn’t work, copy and paste the following link in your browser:<br>" +
                "<a href=\"" + link + "\" style=\"color: #4CAF50;\">" + link + "</a>" +
                "</p>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "⏳ This link is valid for 15 minutes." +
                "</p>" +

                "<br/>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "If you did not request this password reset, please ignore this email." +
                "</p>" +

                "<br/>" +

                "<p style=\"font-size: 15px; color: #333; font-weight: bold;\">Regards,</p>" +
                "<p style=\"font-size: 15px; color: #555;\">The Support Team</p>" +

                "<hr style=\"margin-top: 30px; border: none; border-top: 1px solid #eee;\"/>" +
                "<p style=\"font-size: 12px; color: #aaa; text-align: center;\">" +
                "This is an automated email. Please do not reply." +
                "</p>" +

                "</div>" +
                "</body>" +
                "</html>";
    }
}
