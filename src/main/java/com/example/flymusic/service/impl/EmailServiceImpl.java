package com.example.flymusic.service.impl;

import com.example.flymusic.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String content) {
        System.out.println("========== 发送邮件 ==========");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
        System.out.println("==============================");
    }
}
