# QA-Brains Automation Project

## Project Overview
This project automates key functionalities of the QA Brains e-commerce website using Selenium WebDriver, Java, Maven, and TestNG. It follows the Page Object Model (POM) design pattern to improve code organization and maintainability.

## Technologies Used
- Java 21
- Selenium WebDriver 4.35.0
- TestNG 7.11.0
- Maven
- Eclipse IDE
- Git and GitHub

## Project Structure
```text
QA-Brains-Automation/
├── src/
│   └── test/
│       └── java/
│           ├── pages/
│           │   └── LoginPage.java
│           ├── tests/
│           │   └── LoginTest.java
│           └── utils/
│               └── BrowserUtils.java
├── pom.xml
└── README.md
```

## Test Scenarios
The project includes automated tests for:

1. Positive login
2. Negative login
3. Invalid login data using TestNG DataProvider
4. Product listing verification
5. Product sorting options
6. Product details verification
7. Add product to cart
8. Remove product from cart
9. Update product quantity
10. Checkout process
11. Order confirmation flow

## Automation Features
- Page Object Model (POM)
- Explicit waits
- Selenium WebDriver browser automation
- TestNG assertions
- Data-driven testing using `@DataProvider`
- Maven project configuration

## Test Execution Results
**Latest test execution: 12 passed, 1 failed.**

The tests were executed using TestNG in Eclipse. Results may vary depending on website availability and changes to the application.

## Application Under Test
[QA Brains E-commerce Practice Website](https://practice.qabrains.com/ecommerce)

## How to Run the Project
1. Clone or download this repository.
2. Import the project into Eclipse as a Maven project.
3. Ensure Java 21 and Maven are configured.
4. Allow Maven to download the required dependencies.
5. Open `LoginTest.java`.
6. Right-click the test file and select **Run As → TestNG Test**.
7. Review the results in the TestNG Results view.
## Author
Anjana Jithesh M
