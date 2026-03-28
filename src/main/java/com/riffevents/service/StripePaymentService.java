package com.riffevents.service;

import com.riffevents.model.Event;
import com.riffevents.model.Ticket;
import com.riffevents.model.User;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripePaymentService {

    public String createCheckoutSession(Event event, User user, int quantity) throws Exception {

        Map<String, Object> params = new HashMap<>();

        params.put("mode", "payment");
        params.put("success_url", "http://localhost:8080/events?payment=success");
        params.put("cancel_url", "http://localhost:8080/events?payment=cancel");

        Map<String, Object> priceData = Map.of(
                "currency", "pln",
                "unit_amount", event.getTicketPrice()
                        .multiply(BigDecimal.valueOf(100))
                        .intValue(),
                "product_data", Map.of(
                        "name", event.getTitle() + " – bilet"
                )
        );


        Map<String, Object> lineItem = Map.of(
                "price_data", priceData,
                "quantity", quantity
        );

        params.put("line_items", List.of(lineItem));

        params.put("metadata", Map.of(
                "eventId", event.getId().toString(),
                "userId", user.getId().toString(),
                "quantity", String.valueOf(quantity)
        ));

        Session session = Session.create(params);
        return session.getUrl();
    }
}

