package com.library.admin.controller;

import com.library.admin.dto.RoleActionRequest;
import com.library.admin.service.AdminServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RoleNotFoundException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminServiceImpl adminService;

    @PostMapping("/manage-roles")
    public ResponseEntity<?> manageRoles(@RequestBody RoleActionRequest request) throws RoleNotFoundException {

     adminService.updateRoles(request);

        return ResponseEntity.ok("Role updated successfully");

    }
}
