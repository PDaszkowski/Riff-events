package com.riffevents.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final QRCodeService qrCodeService;

    public EmailService(JavaMailSender mailSender, QRCodeService qrCodeService) {
        this.mailSender = mailSender;
        this.qrCodeService = qrCodeService;
    }

    public void sendTicketEmail(String toEmail, String subject, String bodyText, String qrText) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(bodyText);

        BufferedImage qrImage = qrCodeService.generateQRCodeImage(qrText, 250, 250);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        helper.addAttachment("ticket.png", new ByteArrayResource(baos.toByteArray()));

        mailSender.send(message);
    }
}
