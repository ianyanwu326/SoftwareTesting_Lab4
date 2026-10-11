# Lab Four: Library Alert System

## Overview

This project implements a Library Alert System using Java. The `LibraryService` handles checking out library resources and notifying members by email when a checkout is successful.

## Features

- Checks whether a resource ID is valid.
- Checks whether a resource is available.
- Updates the resource status when it is checked out.
- Sends an email notification after a successful status update.
- Handles database and email failures using exceptions.
- Uses Mockito to mock dependencies during unit testing.

## Technologies Used

- Java 21
- JUnit 5
- Mockito
- Maven
- IntelliJ IDEA

## Testing

The `LibraryServiceTest` class tests the checkout process using mocked `EmailProvider` and `ResourceRepository` dependencies.

The tests cover:
- Null resource IDs
- Unavailable resources
- Successful checkouts
- Database update failures
- Email sending failures

## Project Structure

```text
Lab4/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── Library/
│   │           ├── DatabaseFailureException.java
│   │           ├── EmailFailureException.java
│   │           ├── EmailProvider.java
│   │           ├── LibraryService.java
│   │           └── ResourceRepository.java
│   └── test/
│       └── java/
│           └── Library/
│               └── LibraryServiceTest.java
├── pom.xml
└── README.md
```

## Running the Tests

Make sure Java 21 and Maven are configured, then run:

```bash
mvn clean test
```

## Purpose

This lab demonstrates unit testing with JUnit 5 and Mockito, including how to test service logic without connecting to a real database or email server.
