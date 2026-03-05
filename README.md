-->🎓 College Fees Payment System

A secure College Fees Payment Web Application developed using Spring Boot, MySQL, and Razorpay Payment Gateway.
This system allows students to register, verify email via OTP, login securely, and pay their college fees online.

--> Features

--> Student Features

->Student Registration

->Email OTP Verification

->CAPTCHA Security

->Secure Login System

->Password Reset via Email

->Online Fees Payment

->Payment Status Tracking

->Dashboard to view payment details

👨‍💼 Admin Features

->Admin Login

->View Student Records

->Monitor Payment Status

-.Manage Fee Details

🔐 Security Features

->OTP Email Verification

->CAPTCHA Validation

->Spring Security Authentication

->Password Encryption

->Token-based authentication

->Protected Admin Access

💳 Payment Integration

->Integrated with Razorpay Payment Gateway

->Secure online fee payment

->Payment confirmation after transaction

->Payment status update in database

🛠 Technologies Used
Backend

* Java

* Spring Boot

* Spring Security

* JPA / Hibernate


--->Frontend


* HTML

* CSS

* Bootstrap

* JavaScript

* Database

* MySQL

* Payment Gateway

* Razorpay API

📂 Project Structure
college-fees-payment-app
│
├── controller
├── service
├── repository
├── entity
├── config
│
├── templates
│   ├── login.html
│   ├── register.html
│   ├── dashboard.html
│   ├── admin.html
│
├── application.properties
└── pom.xml


⚙ Installation & Setup
1️⃣ Clone the repository
git clone https://github.com/yourusername/college-fees-payment-app.git

2️⃣ Open in Spring Tool Suite / IntelliJ

Import as Maven Project

3️⃣ Configure MySQL Database

Update application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/fees_db
spring.datasource.username=root
spring.datasource.password=yourpassword

4️⃣ Run the application

Run the Spring Boot main class

Application will start at:

http://localhost:8080

📸 Screenshots
Login Page

Secure login with role-based access.

Registration Page

Student registration with OTP verification and CAPTCHA.

Dashboard

Students can view payment status.

Payment Page

Integrated Razorpay payment gateway.

🎯 Future Improvements

Student Profile Management

Payment History Reports

Admin Fee Management

SMS OTP Integration

Cloud Deployment

👨‍💻 Author

==>Dheeraj Kamsali

GitHub:
https://github.com/DheerajKamsali

⭐ If you like this project

Give a ⭐ on GitHub to support the project.
