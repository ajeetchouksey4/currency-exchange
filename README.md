# Currency Exchange and Discount Calculator

## Overview

This Spring Boot application calculates the payable amount for a bill by applying discounts and converting the amount to
the target currency using real-time exchange rates.

### Requirements

- Java 17+
- Gradle 8.10.0
- Sonarqube server

### Used Plugins
- Used **ArchUnit** for java architecture unit testing
- **PiTest** for mutation testing
- **Spotless** for auto code formatting
- **Sonarqube** local server for code analysis
- **Jacoco** for code coverage reports
- Swagger for API documentation
- JUnit 5 & Mockito for testing

### Features

- Retrieve exchange rates from a third-party API ()
- Apply discounts based on user type and bill amount
- Convert total to the desired target currency
- **License**: Placeholder for a license(copyright.txt)
- Enabled **Spring Cache** for minimize third party calls

### How to Run the Application

1. Clone the repository:
    ```bash
    git clone https://github.com/ajeetchouksey4/currency-exchange.git -b master
    cd currency-exchange
    ```

2. Build the project:
    ```bash
    ./gradlew build  - for Linux based OS
   gradle.bat build  - for windows bases OS
    ```

3. Run the application:
    ```bash
    ./gradlew bootRun
   gradle.bat bootRun
    ```

4. Access the API at `http://localhost:8080/api/calculate`

5. How to Access Reports
   ```bash
    build/reports/jacoco/test/html/index.html - jacoco code coverage report
    /build/reports/pitest/index.html - piTest Report for mutation testing
    ```

### Running Tests

To run unit tests:

```bash
./gradlew test - for Linux OS
gradle.bat test - for windows OS
