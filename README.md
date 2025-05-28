🧱 Project Template – Product & Category Management

This is a model project designed as a foundation for future developments. It includes essential features commonly found in web applications:

![image](https://github.com/user-attachments/assets/eba9e365-e180-49af-b8e8-357038a99181)

✅ Main Features

    Basic CRUD operations for:

        Product (with fields: name, description, price, image URL)

        Category (with name only)

    User authentication and authorization:

        Role-based access control

        Password encryption

    Password recovery via email

    Unit testing with:

        JUnit

        Mockito

📦 Technologies Used

    Java (Spring Boot)

    JPA / Hibernate

    REST API

    JUnit & Mockito for testing

    Mail sender for password recovery

🔒 Authentication Flow

    User registration and login

    JWT-based authentication

    Role assignment (e.g., ADMIN, USER)

🗂️ Domain Model

The diagram includes:

    User, Role, Product, and Category entities

    Relationships:

        One-to-many between User and Role

        Many-to-many between Product and Category
