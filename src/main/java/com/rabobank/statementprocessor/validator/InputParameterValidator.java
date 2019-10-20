package com.rabobank.statementprocessor.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputParameterValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String fileName = jobParameters.getString("inputFile");

        if (StringUtils.isEmpty(fileName)) {
            throw new JobParametersInvalidException(
                    "The inputFile parameter is required. Specify input file as a parameter [inputFile=<path to file>]");
        }

        if (!fileName.toLowerCase().endsWith(".xml") && !fileName.toLowerCase().endsWith(".csv")) {
            throw new JobParametersInvalidException("Only csv & xml files are expected");
        }

        try {
            Path file = Paths.get(fileName);
            if (!file.toFile().exists() || !Files.isReadable(file)) {
                throw new JobParametersInvalidException("File does not exist or was not readable");
            }
        } catch (Exception e) {
            throw new JobParametersInvalidException(
                    "The inputFile parameter needs to be a valid file location/ file.");
        }
    }
}
