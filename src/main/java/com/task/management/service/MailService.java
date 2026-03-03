package com.task.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendAccountCreationMail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your account has been created successfully - TaskFlow");

        String body = "Hello " + username + ",\n\n"
                + "Welcome to TaskFlow! Your account has been created successfully.\n\n"
                + "Start adding your tasks and keep progressing them.\n\n"
                + "Warm regards,\nTaskFlow Team";

        message.setText(body);
        javaMailSender.send(message);
    }


    @Async
    public void sendTaskCreatedMail(String toEmail, String username, String taskTitle) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Task Added - TaskFlow");

        String body = "Hello " + username + ",\n\n"
                + "A new task has been added successfully.\n\n"
                + "Task: " + taskTitle + "\n\n"
                + "Stay productive!\n\n"
                + "Regards,\nTaskFlow Team";

        message.setText(body);
        javaMailSender.send(message);
    }

    @Async
    public void sendTaskReminderMail(String toEmail,
                                     String username,
                                     String taskTitle,
                                     LocalDateTime endTime) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Task Reminder - TaskFlow");

        String body = "Hello " + username + ",\n\n"
                + "Reminder: Your task is ending soon.\n\n"
                + "Task: " + taskTitle + "\n"
                + "Ends At: " + formattedIndianTime(endTime) + "\n\n"
                + "You have only 15 minutes remaining.\n\n"
                + "Regards,\nTaskFlow Team";

        message.setText(body);
        javaMailSender.send(message);
    }

    public String formattedIndianTime(LocalDateTime endTime){
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");

        ZonedDateTime indianTime = endTime
                .atZone(ZoneId.systemDefault())   // your server zone
                .withZoneSameInstant(indiaZone);  // convert to IST

        // Format to 12-hour format
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        String formattedTime = indianTime.format(formatter);
        return formattedTime;
    }


}
