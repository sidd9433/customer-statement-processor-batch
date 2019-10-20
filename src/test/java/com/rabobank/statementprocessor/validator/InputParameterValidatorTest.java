package com.rabobank.statementprocessor.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputParameterValidator.class)
public class InputParameterValidatorTest {

    @Autowired
    public InputParameterValidator validator;

    private JobParameters jobParameters;

    @Test(expected = JobParametersInvalidException.class)
    public void validateNoFile() throws Exception {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter(""));
        jobParameters = new JobParameters(params);

        validator.validate(jobParameters);
    }

    @Test(expected = JobParametersInvalidException.class)
    public void validateWrongFile() throws Exception {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter("test.pdf"));
        jobParameters = new JobParameters(params);

        validator.validate(jobParameters);
    }

    @Test(expected = JobParametersInvalidException.class)
    public void validateFileDoesNotExist() throws Exception {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter("test.csv"));
        jobParameters = new JobParameters(params);

        validator.validate(jobParameters);
    }

    @Test
    public void validationSuccess() throws Exception {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter(this.getClass().getResource("/data/records.csv").getFile()));
        jobParameters = new JobParameters(params);

        validator.validate(jobParameters);
    }
}
