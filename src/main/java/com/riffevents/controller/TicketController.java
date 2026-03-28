package com.riffevents.controller;

import com.riffevents.model.Ticket;
import com.riffevents.repository.TicketRepository;
import com.riffevents.service.QRCodeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.riffevents.model.User;

import java.util.List;

@Controller
public class TicketController {

    private final TicketRepository ticketRepository;
    private final QRCodeService qrCodeService;

    public TicketController(TicketRepository ticketRepository, QRCodeService qrCodeService) {
        this.ticketRepository = ticketRepository;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/my-tickets")
    public String getUserTickets(@AuthenticationPrincipal User user, Model model) {
        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());

        for (Ticket ticket : tickets) {
            try {
                String base64 = qrCodeService.getQRCodeBase64(ticket.getQrCode());
                ticket.setQrCodeBase64(base64);
            } catch (Exception e) {
                System.err.println("Error generating QR ticket: " + ticket.getId());
            }
        }

        model.addAttribute("tickets", tickets);
        return "my-tickets";
    }
}
