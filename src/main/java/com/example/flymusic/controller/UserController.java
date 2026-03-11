package com.example.flymusic.controller;

import com.example.flymusic.entity.User;
import com.example.flymusic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户Controller
 * 提供用户相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(createSuccessResponse("注册成功", registeredUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            User user = userService.login(username, password);
            return ResponseEntity.ok(createSuccessResponse("登录成功", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(createSuccessResponse("获取成功", null));
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(createSuccessResponse("更新成功", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            boolean success = userService.changePassword(id, oldPassword, newPassword);
            if (success) {
                return ResponseEntity.ok(createSuccessResponse("密码修改成功", null));
            }
            return ResponseEntity.badRequest().body(createErrorResponse("原密码错误"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> data) {
        try {
            String email = data.get("email");
            boolean success = userService.resetPassword(email);
            if (success) {
                return ResponseEntity.ok(createSuccessResponse("重置邮件已发送", null));
            }
            return ResponseEntity.badRequest().body(createErrorResponse("邮箱不存在"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 通过令牌更新密码
     */
    @PostMapping("/update-password")
    public ResponseEntity<Map<String, Object>> updatePasswordByToken(@RequestBody Map<String, String> data) {
        try {
            String token = data.get("token");
            String newPassword = data.get("newPassword");
            boolean success = userService.updatePasswordByToken(token, newPassword);
            if (success) {
                return ResponseEntity.ok(createSuccessResponse("密码更新成功", null));
            }
            return ResponseEntity.badRequest().body(createErrorResponse("令牌无效或已过期"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(createSuccessResponse("检查成功", exists));
    }

    /**
     * 获取所有用户（管理员）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(createSuccessResponse("获取成功", users));
    }

    /**
     * 根据角色获取用户（管理员）
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable Integer role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(createSuccessResponse("获取成功", users));
    }

    /**
     * 启用/禁用用户（管理员）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ResponseEntity.ok(createSuccessResponse("状态更新成功", null));
    }

    /**
     * 设置VIP（管理员）
     */
    @PostMapping("/{id}/vip")
    public ResponseEntity<Map<String, Object>> setVip(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Date expireDate = (Date) data.get("expireDate");
        userService.setVip(id, expireDate);
        return ResponseEntity.ok(createSuccessResponse("VIP设置成功", null));
    }

    /**
     * 辅助方法：创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 辅助方法：创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        return response;
    }
}