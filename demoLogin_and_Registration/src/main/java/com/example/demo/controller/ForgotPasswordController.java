package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.impl.CustomerNotFoundException;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.util.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import net.bytebuddy.utility.RandomString;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("pageTitle", "ForgotPassword");
        return "forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordFrom(HttpServletRequest request,Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(45);
        try {
            userService.updateResetPasswordToken(token, email);
            //geneate reset password link
            String resetPasswordLink= Utility.getSiteURL(request)+"/reset_password?token="+token;
//            String resetPasswordLink="/reset_password?token="+token;
//            System.out.println(resetPasswordLink);

            //send email
            sendEmail(email,resetPasswordLink);
            model.addAttribute("message","We have sent a reset password link to your email. Please check!");
        } catch (Exception | CustomerNotFoundException e) {

            model.addAttribute("error",e.getMessage());
        }

//        System.out.println("Email"+email);
//        System.out.println("Token"+token);
        model.addAttribute("pageTitle", "ForgotPassword");
        return "forgot_password_form";
    }

    private void sendEmail(String recipientEmail, String link) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);

        helper.setFrom("ducnguyen24htd@gmail.com","shopme Support");
        helper.setTo(recipientEmail);

        String subject="Here's the link to reset your password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model){
        User user=userService.getByResetPasswordToken(token);
        model.addAttribute("token",token);
        if (user==null){
            model.addAttribute("message","Invalid Token");
            return "message";
        }
        return "reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request,Model model){
        String token=request.getParameter("token");
        String password=request.getParameter("password");
        User user=userService.getByResetPasswordToken(token);
        if (user==null){
            model.addAttribute("title","Reset your password");
            model.addAttribute("message","Invalid Token");
            return "message";
        }else{
            userService.updatePassword(user,password);
            model.addAttribute("message","You have successfully change your password.");

        }
        return "message";

    }


}
