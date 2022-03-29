package com.cursotdd.libraryapi.service.impl;

import com.cursotdd.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private String remetent = "mail@library-api.com";

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(List<String> mailsList, String msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        String[] mails = mailsList.toArray(new String[mailsList.size()]);

        message.setFrom(remetent);
        message.setSubject("Livro com a devolução atrasada");
        message.setText(msg);
        message.setTo(mails);;

        javaMailSender.send(message);
    }
}
