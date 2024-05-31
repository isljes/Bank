# Bank Application

#### Requires:

* [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
* [Docker](https://www.docker.com/)

### How to run application

#### 1. Clone repository

> `git clone https://github.com/isljes/Bank.git`

#### 2. (Optional) Customize`compose.yml`

#### 3. (Optional) Customize`application.properties`
* spring.mail.*


#### 4. Build Jar
> `mvn clean package`

#### 5. Run Application
> `docker compose up`

#### 6. Login Or Register
* ROLE_ADMIN
  * Email: admin@gmail.com
  * Password: admin123

    
### Functional
* **UNCONFIRMED_USER**
  * Email confirmation
* **USER**
  * Card issue
  * Money transfer between cards
* **ADMIN**
  * Card management
  * User management

### Stack
* **Backend**: Java 17`, Spring(Boot, MVC, Data, Security, AOP);
* **SQL**: PostgreSQL;
* **NoSQL**: Redis;
* **Frontend**: Thymeleaf, Bootstrap, JS(jQuery, Ajax)
* **Tools**: Maven, Docker, Docker Compose, Git;

