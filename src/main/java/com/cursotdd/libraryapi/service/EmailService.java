package com.cursotdd.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendMails(List<String> mailsList, String msg);
}
