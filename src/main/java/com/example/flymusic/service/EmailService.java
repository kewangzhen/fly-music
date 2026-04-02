package com.example.flymusic.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}
