package com.rspl.meal.Booking.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

//    public void sendOtpEmail(String to,String otp) {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
//
//        try {
//            helper.setTo(to);
//            helper.setSubject("Your OTP Code");
//            helper.setText("Your OTP code is: " + otp);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }

    public void sendOtpEmail(String to, String name,String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        Context context = new Context();
        context.setVariable("name",name);
        context.setVariable("otp",otp);

        String htmlBody = templateEngine.process("otp-email-template", context);

        helper.setTo(to);
        helper.setSubject("Your OTP Code");
        helper.setText(htmlBody, true);

        mailSender.send(message);

    }


}
