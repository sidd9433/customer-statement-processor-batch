package com.rabobank.statementprocessor.processor;

import com.rabobank.statementprocessor.model.StatementRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StatementRecordProcessor.class)
public class StatementRecordProcessorTest {

    @Autowired
    private StatementRecordProcessor statementProcessor;

    @Test
    public void testIncorrrectEndBalance() throws Exception {

        StatementRecord record = new StatementRecord();
        record.setReference("154270");
        record.setAccountNumber("NL56RABO0149876948");
        record.setDescription("Candy for Peter de Vries");
        record.setStartBalance("5429");
        record.setMutation("-939");
        record.setEndBalance("6368");

        StatementRecord recordOut = statementProcessor.process(record);
        assertNotNull(recordOut);
        assertEquals("154270", recordOut.getReference());
    }

    @Test
    public void testDuplicateReference() throws Exception {

        StatementRecord record1 = new StatementRecord();
        record1.setReference("876543");
        record1.setAccountNumber("NL56RABO0146543948");
        record1.setDescription("Something for someone - one");
        record1.setStartBalance("500");
        record1.setMutation("-150");
        record1.setEndBalance("350");

        StatementRecord record2 = new StatementRecord();
        record2.setReference("876543");
        record2.setAccountNumber("NL56RABO0147654948");
        record2.setDescription("Something for someone - two");
        record2.setStartBalance("500");
        record2.setMutation("-150");
        record2.setEndBalance("350");

        StatementRecord recordOut = statementProcessor.process(record1);
        assertNull(recordOut);
        recordOut = statementProcessor.process(record2);
        assertEquals("876543", recordOut.getReference());
    }
}
