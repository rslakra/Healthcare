HealthSuite
===============

A Spring Boot healthcare application used for tracking exercise goals and minutes. Base project used for Pluralsight Spring JPA and Spring Security courses among others.

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.3.0
- **Spring Security**: 6.1.5
- **Database**: H2 (default) / MySQL
- **Build Tool**: Maven
- **Packaging**: WAR

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.x

### Running the Application

1. **Using the run script:**
   ```bash
   ./runMaven.sh
   ```

2. **Using Maven directly:**
   ```bash
   mvn clean spring-boot:run
   ```

3. **Building the WAR file:**
   ```bash
   ./buildMaven.sh
   # or
   mvn clean package
   ```

### Access URLs

- **Application**: http://localhost:8080/HealthSuite
- **H2 Console**: http://localhost:8080/HealthSuite/h2

### Default Credentials

The application comes with the following default users pre-configured in the database:

| Username   | Password     | Roles                 |
|------------|--------------|-----------------------|
| `rlakra`   | `password`   | ROLE_USER             |
| `rslakra`  | `secret`     | ROLE_USER, ROLE_ADMIN |
| `lakra`    | `password`   | ROLE_USER             |

**Note**: The default admin user is `rslakra` with password `secret`.

**Important**: The database schema and users are automatically initialized from `src/main/resources/db/schema.sql` on first startup. If you encounter authentication issues:

1. Ensure the H2 database file exists at `~/Downloads/H2DB/HealthSuite`
2. Check the H2 console at http://localhost:8080/HealthSuite/h2 to verify users exist
3. The security configuration uses the H2 database (not MySQL) and plain text passwords

### Database Configuration

- **Default Database**: H2 (in-memory/file-based)
- **H2 Database Location**: `~/Downloads/H2DB/HealthSuite`
- **H2 Username**: `sa`
- **H2 Password**: (empty)

The database schema and default users are automatically created from `src/main/resources/db/schema.sql`.

## Course References

- [Maven Fundamentals](http://pluralsight.com/training/Courses/TableOfContents/maven-fundamentals)
- [Spring MVC Introduction](http://pluralsight.com/training/Courses/TableOfContents/springmvc-intro)

## Deployment References

- [Deploy a Spring Boot WAR into a Tomcat Server](https://www.baeldung.com/spring-boot-war-tomcat-deploy)
- [How to Deploy a WAR File to Tomcat](https://www.baeldung.com/tomcat-deploy-war)
