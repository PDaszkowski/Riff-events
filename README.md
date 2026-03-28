# Riff Events

A concert ticket booking platform for rock and metal music fans. Built as a university project to explore real-world integrations like online payments, QR code generation, and transactional email.

---

## Screenshots

> Home Page
<img width="1881" height="815" alt="image" src="https://github.com/user-attachments/assets/c800590a-62ad-4de0-924b-7d6268c35322" />
<img width="1887" height="593" alt="image" src="https://github.com/user-attachments/assets/ff0f2432-a3ed-4406-8c78-2caee44a8257" />

> Event List
<img width="1887" height="914" alt="image" src="https://github.com/user-attachments/assets/38013c36-c94c-4031-956d-691414004fb5" />
<img width="1884" height="871" alt="image" src="https://github.com/user-attachments/assets/61832f13-c966-4140-ac6d-a91dff05c346" />

> Artists
<img width="1883" height="910" alt="image" src="https://github.com/user-attachments/assets/0272d0d0-0960-40fe-8f98-0f0c08082947" />


> Ticket & Checkout
<img width="1902" height="912" alt="image" src="https://github.com/user-attachments/assets/5d48d07c-caf1-4ab4-82f3-fab9e1e43648" />
<img width="1881" height="907" alt="image" src="https://github.com/user-attachments/assets/4be0ffe2-6e54-4b5a-b6a0-6f990060262e" />
After successfull payment, user receives email message with QR-code ticket attached:
<img width="1516" height="644" alt="image" src="https://github.com/user-attachments/assets/a85ef892-60f5-4494-bb8a-f9608f1c2531" />

> Reviews
<img width="595" height="582" alt="image" src="https://github.com/user-attachments/assets/f31bc171-ebce-43ee-b08b-05f876cf4e27" />

> Venues with dynamic Google maps links
![google-maps](https://github.com/user-attachments/assets/85062fec-3481-42b6-9d12-70210a06e49f)


---

## Tech Stack

**Backend**
- Java 21
- Spring Boot 4.0
- Spring Security
- Spring Data JPA (Hibernate)
- Spring Session (JDBC)
- Spring Mail

**Frontend**
- Thymeleaf
- HTML / CSS / JavaScript

**Database**
- PostgreSQL

**Integrations**
- 💳 [Stripe](https://stripe.com) — online payment processing and webhook handling
- 📷 [ZXing](https://github.com/zxing/zxing) — QR code generation for purchased tickets
- 📧 JavaMail — email confirmations sent after successful booking

---

ERD Diagram:
<img width="1446" height="1088" alt="image" src="https://github.com/user-attachments/assets/badffd9a-9759-48b2-8f94-ea3f1d879c3b" />


**Build**
- Gradle

---

## Key Features

- Browse and filter rock & metal concerts
- User registration and login (Spring Security)
- Role-based access (User / Organizer / Admin)
- Event and venue management for organizers
- Ticket purchasing with Stripe Checkout
- QR code generated and attached to each ticket
- Email confirmation sent after successful payment
- Stripe webhook integration for payment event handling
- Favorites and reviews system

---

## What I Learned

This project was an opportunity to work with integrations I hadn't used before:

- **Stripe** — implementing Checkout Sessions, handling webhooks (`checkout.session.completed`), and verifying webhook signatures
- **QR Code Generation** — using the ZXing library to generate QR codes embedded in ticket confirmations
- **Email in Spring** — configuring Spring Mail with Gmail SMTP, sending HTML emails with ticket details after payment
- **Google maps links** — dynamic links, which shows exact location of an event

---

## Setup

1. Clone the repository
2. Create `src/main/resources/application.yaml` based on the template below
3. Run with Gradle:

```bash
./gradlew bootRun
```

### application.yaml template

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/riff_events
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD
  mail:
    host: smtp.gmail.com
    port: 587
    username: YOUR_EMAIL
    password: YOUR_APP_PASSWORD
  security:
    user:
      name: user
      password: password

stripe:
  secret-key: YOUR_STRIPE_SECRET_KEY
  webhook:
    signing-secret: YOUR_WEBHOOK_SECRET
```


---

## License
 
[Apache 2.0](LICENSE)
