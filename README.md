# Rabobank Customer Statement Processor #

### Assignment ###

* Quick summary

  Rabobank receives monthly deliveries of customer statement records. This information is delivered in two formats, CSV and XML. These records need to be validated. based on below conditions
  
     * all transaction references should be unique
     * end balance needs to be validated 
     * Return both the transaction reference and description of each of the failed records

## Quick Run

 * Clone this repository
 * Run `mvn clean package`
 * Navigate to `<project root>/target` folder
 * Run `java -jar customer-statement-processor-batch-0.0.1-SNAPSHOT.jar inputFile=<absolute path>/<any of the input files, csv or xml>`
 * Output report `validated-report.txt` with the validated data, will be generated on the same location of the input file.
 
     ##### Alternative

 * Import the project in your favourite IDE
 * Run the file `src/main/java/com/rabobank/statementprocessor/Application.java`, program argument `inputFile=<absolute path>/<any of the input files, csv or xml>` has to be set before running.


## Design principle

* It's a command line application built using Spring batch.
* All the modules ate loosely coupled & they have specific role to play.
* Tried to keep the code as simple & readable as possible.

## Outline of classes

* Job configuration -> ```BatchConfig``` controls the job.
* Record Processor ->  ```StatementRecordProcessor``` processes the data for the output.
* Validator ```InputParameterValidator``` validates the input

## Code quality

 * SonarQube scan: ``mvn clean verify -P sonar``
 * SonarCloud dashboard https://sonarcloud.io/dashboard?id=sidd9433_customer-statement-processor-batch
