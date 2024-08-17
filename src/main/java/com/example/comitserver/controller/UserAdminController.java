package com.example.comitserver.controller;

import com.example.comitserver.entity.Role;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.service.UserAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userAdminService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable Long id) {
        return userAdminService.getUserById(id);
    }

    @PatchMapping("/{id}/role")
    public void updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        Role role = Role.valueOf(requestBody.get("role"));
        userAdminService.updateUserRole(id, role);
    }

    @PatchMapping("/{id}/isStaff")
    public void updateUserIsStaff(@PathVariable Long id, @RequestBody Map<String, Boolean> requestBody) {
        Boolean isStaff = requestBody.get("isStaff");
        userAdminService.updateUserIsStaff(id, isStaff);
    }
}