package com.library.admin.service;

import com.library.admin.dto.RoleActionRequest;
import com.library.common.exception.RoleNotFoundException;
import com.library.common.exception.UserNotFoundException;
import com.library.common.utils.service.RoleCache;
import com.library.user.entity.Roles;
import com.library.user.entity.Users;
import com.library.user.repository.RolesRepository;
import com.library.user.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



@AllArgsConstructor
@Service
public class AdminServiceImpl implements AdminService{

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final RoleCache roleCache;

    @Override
    public String updateRoles(RoleActionRequest request) {

        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + request.getUserId()
                        )
                );

//        Roles role = rolesRepository.findByRoleName(request.getRole())
//                .orElseThrow(() ->
//                        new RoleNotFoundException(
//                                "Role not found" + request.getRole()
//                        )
//                );
        Roles role = roleCache.getRoleByName(request.getRole());

        String action = request.getAction().toUpperCase();

        switch (action) {

            case "ASSIGN" -> {
                if (user.getRoles().contains(role)) {
                    return "User already has role: " + role.getRoleName();
                }
                user.getRoles().add(role);
                usersRepository.save(user);
                return "Role " + role.getRoleName() + " assigned successfully";
            }

            case "REVOKE" -> {
                if (!user.getRoles().contains(role)) {
                    return "User does not have role: " + role.getRoleName();
                }
                user.getRoles().remove(role);
                usersRepository.save(user);
                return "Role " + role.getRoleName() + " revoked successfully";
            }

            default -> throw new IllegalArgumentException(
                    "Invalid action: " + request.getAction()
            );
        }
    }

}
