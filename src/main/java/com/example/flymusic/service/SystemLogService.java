package com.example.flymusic.service;

import com.example.flymusic.entity.SystemLog;
import com.example.flymusic.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    public void logAction(Long userId, String username, String action, String targetType,
                         Long targetId, String method, String ipAddress, String userAgent,
                         String details, Integer status, String errorMessage) {
        try {
            SystemLog log = new SystemLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setAction(action);
            log.setTargetType(targetType);
            log.setTargetId(targetId);
            log.setMethod(method);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setDetails(details);
            log.setStatus(status);
            log.setErrorMessage(errorMessage);
            log.setCreatedAt(new Date());
            systemLogRepository.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAll();
    }

    public List<SystemLog> getLogsByUserId(Long userId) {
        return systemLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<SystemLog> getLogsByAction(String action) {
        return systemLogRepository.findByAction(action);
    }
}
