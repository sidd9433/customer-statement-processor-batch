package com.rabobank.statementprocessor.processor;

import com.rabobank.statementprocessor.model.StatementRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class StatementRecordProcessor implements ItemProcessor<StatementRecord, StatementRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatementRecordProcessor.class);

    private Set<String> referenceSet = new HashSet<>();

    @Override
    public StatementRecord process(StatementRecord statementRecord) {

        if (!validateReference(statementRecord)) {
            LOGGER.warn("Unique reference validation failed: {}", statementRecord);
            return statementRecord;
        }

        if (!validateEndBalance(statementRecord)) {
            LOGGER.warn("End balance validation failed: {}", statementRecord);
            return statementRecord;
        }
        return null;
    }

    private boolean validateReference(StatementRecord statementRecord) {
        return referenceSet.add(statementRecord.getReference());
    }

    private boolean validateEndBalance(StatementRecord statementRecord) {
        return (new BigDecimal(statementRecord.getStartBalance()).add(new BigDecimal(statementRecord.getMutation()))
                .equals(new BigDecimal(statementRecord.getEndBalance())));
    }
}
