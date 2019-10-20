package com.rabobank.statementprocessor.config;

import com.rabobank.statementprocessor.model.StatementRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@ContextConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BatchConfig.class)
public class BatchConfigTest {

    @Autowired
    private Job processJob;

    @Autowired
    private AbstractItemCountingItemStreamItemReader<StatementRecord> reader;

    @Autowired
    private ItemProcessor<StatementRecord, StatementRecord> statementProcessor;

    @Autowired
    private FlatFileItemWriter<StatementRecord> writer;

    private JobParameters jobParameters;

    @Test
    public void testJobConfiguration() {
        assertNotNull(processJob);
        assertEquals("processJob", processJob.getName());
    }

    @Test
    public void testReaderCsv() throws Exception {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter(this.getClass().getResource("/data/records.csv").getFile()));
        jobParameters = new JobParameters(params);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
        int count = 0;
        try {
            count = StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                int numRecords = 0;
                StatementRecord record;
                try {
                    reader.open(stepExecution.getExecutionContext());
                    while ((record = reader.read()) != null) {
                        assertNotNull(record);
                        numRecords++;
                    }
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        fail(e.toString());
                    }
                }
                return numRecords;
            });
        } catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(10, count);
    }

    @Test
    public void testReaderXml() throws Exception {

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter(this.getClass().getResource("/data/records.xml").getFile()));
        jobParameters = new JobParameters(params);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
        int count = 0;
        try {
            count = StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                int numRecords = 0;
                StatementRecord record;
                try {
                    reader.open(stepExecution.getExecutionContext());
                    while ((record = reader.read()) != null) {
                        assertNotNull(record);
                        numRecords++;
                    }
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        fail(e.toString());
                    }
                }
                return numRecords;
            });
        } catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(10, count);
    }

    @Test
    public void testWriter() throws Exception {
        StatementRecord record = new StatementRecord();
        record.setReference("154270");
        record.setAccountNumber("NL56RABO0149876948");
        record.setDescription("Candy for Peter de Vries");
        record.setStartBalance("5429");
        record.setMutation("-939");
        record.setEndBalance("6368");

        Map<String, JobParameter> params = new HashMap<>();
        params.put("inputFile", new JobParameter(this.getClass().getResource("/data/records.xml").getFile()));
        jobParameters = new JobParameters(params);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);

        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            writer.open(stepExecution.getExecutionContext());
            writer.write(Collections.singletonList(record));
            return null;
        });
        assertTrue((new File(this.getClass().getResource("/data/validated-report.txt").getFile())).exists());
    }
}
