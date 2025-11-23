# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.3/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.3/gradle-plugin/reference/html/#build-image)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-spring-mvc-template-engines)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Java Mail Sender](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-email)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-security)
* [Spring Batch](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#howto-batch-applications)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#production-ready)
* [Apache Freemarker](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-spring-mvc-template-engines)
* [Validation](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#boot-features-validation)
* [Rest Repositories](https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#howto-use-exposing-spring-data-repositories-rest-endpoint)

### Guides

The following guides illustrate how to use some features concretely:

* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Accessing JPA Data with REST](https://spring.io/guides/gs/accessing-data-rest/)
* [Accessing Neo4j Data with REST](https://spring.io/guides/gs/accessing-neo4j-data-rest/)
* [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

---

## H2 Database Access Guide

### How to Access H2 Console

#### 1. Start the Application
Make sure your Spring Boot application is running.

#### 2. Access H2 Console via Web Browser
- **URL**: `http://localhost:8080/routine-checkup/h2`
- **JDBC URL**: `jdbc:h2:file:~/Downloads/H2DB/RoutineCheckup`
- **Username**: `sa`
- **Password**: (leave empty)

#### 3. Check Existing Tables

Once connected, run this SQL query to see all tables:

```sql
SHOW TABLES;
```

Or to see table details:

```sql
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'PUBLIC';
```

#### 4. Expected Tables

Based on the entity classes, you should have these 6 tables:

1. **users** - User accounts
2. **roles** - User roles (ADMIN, USER)
3. **doctors** - Doctor information
4. **patients** - Patient information
5. **services_schedule** - Service scheduling
6. **user_files** - User file storage

#### 5. Check Table Structure

To see the structure of a specific table:

```sql
DESCRIBE users;
```

Or:

```sql
SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'users';
```

#### 6. Check if Tables are Empty

```sql
SELECT 
    'users' as table_name, COUNT(*) as row_count FROM users
UNION ALL
SELECT 'roles', COUNT(*) FROM roles
UNION ALL
SELECT 'doctors', COUNT(*) FROM doctors
UNION ALL
SELECT 'patients', COUNT(*) FROM patients
UNION ALL
SELECT 'services_schedule', COUNT(*) FROM services_schedule
UNION ALL
SELECT 'user_files', COUNT(*) FROM user_files;
```

#### 7. Database File Location

The database file is stored at:
- **Path**: `~/Downloads/H2DB/RoutineCheckup.mv.db`
- **Full path on Mac/Linux**: `/Users/rslakra/Downloads/H2DB/RoutineCheckup.mv.db`

#### 8. Important Notes

- The current configuration uses `spring.jpa.hibernate.ddl-auto = create-drop`, which means:
  - Tables are **created** when the application starts
  - Tables are **dropped** when the application stops
  - Data is **lost** when the application shuts down

- To persist data, change to:
  ```properties
  spring.jpa.hibernate.ddl-auto = update
  ```

#### 9. Troubleshooting

If you can't access the H2 console:
1. Make sure the application is running
2. Check that `spring.h2.console.enabled = true` in application.properties
3. Verify the URL includes the context path: `/routine-checkup/h2`
4. Check browser console for any errors
