package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.AccountRequest.EmailDetail;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.OrderItem;
import com.ToyRentalService.entity.OrderRent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    TemplateEngine  templateEngine;

    @Autowired
    JavaMailSender javaMailSender;

    public void sendMail(EmailDetail emailDetail){
        try{
            Context context = new Context();
            context.setVariable("name",emailDetail.getReceiver().getEmail());
            context.setVariable("button","Go to Home Page");
            context.setVariable("link",emailDetail.getLink());

            String template = templateEngine.process("welcome-template", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("anhltse170584@fpt.edu.vn");
            mimeMessageHelper.setTo(emailDetail.getReceiver().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        }catch (MessagingException ex){
            System.out.println("Sent mail error!!");
        }
    }
    public void sendOrderConfirmationEmail(String recipientEmail, String subject, String templateName, Map<String, Object> templateModel) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariables(templateModel);
        String html = templateEngine.process(templateName, context);

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(html, true);

        javaMailSender.send(message);
    }
    public void sendOrderEmails(String buyerEmail, String sellerEmail, String subject, String templateName, Map<String, Object> buyerTemplateModel, Map<String, Object> sellerTemplateModel) throws MessagingException {
        sendOrderConfirmationEmail(buyerEmail, subject, templateName, buyerTemplateModel);

        sendOrderConfirmationEmail(sellerEmail, subject, templateName, sellerTemplateModel);
    }
}
