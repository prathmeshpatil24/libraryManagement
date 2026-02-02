package com.library.common.utils.service;

import com.library.user.entity.Roles;
import com.library.user.repository.RolesRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class RoleCache {


    private final RolesRepository rolesRepository;
    private final Map<String, Roles> rolesMap = new HashMap<>();
    private final Map<Long, Roles> roleId = new HashMap<>();


    @PostConstruct
    public void loadRoles(){
        System.out.println("🔄 Loading roles into cache... 🔄");
//        for (Roles roles : rolesRepository.findAll()) {
//            System.out.println("Role loaded: " + roles.getRoleName());
//        }

        rolesRepository.findAll()
                .forEach(role->{
                        System.out.println("Role loaded: " + role.getRoleName() + " with ID: " + role.getId());
                        rolesMap.put(role.getRoleName(), role);
                        roleId.put(role.getId(), role);
                });
    }

    public Roles getRoleByName(String roleName){
        // from frontend send only ROLE_NAME  e.g., ROLE_ADMIN

        Roles role = rolesMap.get(roleName);
        if (role == null){
            throw new RuntimeException("Role not found in cache: " + roleName);
        }
        return role;
    }
}
