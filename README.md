# Currency Exchange and Discount Calculator

## Overview
This Spring Boot application calculates the payable amount for a bill by applying discounts and converting the amount to the target currency using real-time exchange rates.

### Features
- Retrieve exchange rates from a third-party API ()
- Apply discounts based on user type and bill amount
- Convert total to the desired target currency

### Requirements
- Java 17+
- Gradle 7.0+
- Sonarqube server
- 

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

### Running Tests

To run unit tests:
```bash
./gradlew test
