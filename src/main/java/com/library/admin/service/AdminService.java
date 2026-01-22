package com.library.admin.service;

import com.library.admin.dto.RoleActionRequest;
import com.library.common.exception.RoleNotFoundException;


public interface AdminService {

    public String updateRoles(RoleActionRequest request) throws RoleNotFoundException;
}
