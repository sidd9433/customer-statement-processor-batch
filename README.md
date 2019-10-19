# Rabobank Customer Statement Processor #

### Assignment ###

* Quick summary

  Rabobank receives monthly deliveries of customer statement records. This information is delivered in two formats, CSV and XML. These records need to be validated. based on below conditions
  
     * all transaction references should be unique
     * end balance needs to be validated 
     * Return both the transaction reference and description of each of the failed records

## Quick Start

 * Clone this repository
 * Run `mvn clean package`
 * Run `mvn spring-boot:run`
 
     ##### Alternative

 * Import the project in your favourite IDE
 * Run the file `src/main/java/com/rabobank/statementprocessor/Application.java`
 
## Quick run
 
 * The API can be reached at http://localhost:8080/customer/api/v1/process-statement
 * Upload the csv or xml file as 'file' attribute in POST body

## Design principle

* All the modules ate loosely coupled & they have specific role to play.
* Models are immutable.
* Followed design patterns.
* Tried to keep the code as simple & readable as possible.

## Outline of classes

* Controller```StatementProcessorController``` handles th request & response.
* Service ```StatementProcessorService``` prepares the output using other modules.
* ```FileProcessorFactory``` is to create input file processor based on the file type
* ```StatementValidator``` is for the validations.

## Code quality

 * SonarQube scan: ``mvn clean verify -P sonar``
 * SonarQube dashboard https://sonarcloud.io/dashboard?id=sidd9433_customer-statement-processor

## Scope for betterment

* Validator could have been designed in a better way, may be using `Factory` or `Chain of responsibility`.
* JenkinsFile for CI/ CD pipeline.