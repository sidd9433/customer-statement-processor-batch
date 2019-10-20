package com.rabobank.statementprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        if (validateInput(args)) {
            application.run(args);
        }
    }

    private static boolean validateInput(String[] args) {
        if (args.length <= 0) {
            LOGGER.error("Specify input file as a parameter [inputFile=<path to file>]");
            return false;
        }

        String inputFileParameter = args[0];
        String parameterName = inputFileParameter.split("=")[0];

        if (!"inputFile".equals(parameterName) || inputFileParameter.split("=").length < 2) {
            LOGGER.error("Specify correct parameter name and value [inputFile=<path to file>]");
            return false;
        }
        String parameterValue = inputFileParameter.split("=")[1];

        if (!parameterValue.toLowerCase().endsWith(".xml") && !parameterValue.toLowerCase().endsWith(".csv")) {
            LOGGER.error("Only csv & xml files are expected");
            return false;
        }

        File inputFile = new File(parameterValue);
        if (!inputFile.exists()) {
            LOGGER.error("Input file [" + parameterValue + "] doesn't exists");
            return false;
        }
        return true;
    }
}
