package com.rabobank.statementprocessor.config;

import com.rabobank.statementprocessor.model.StatementRecord;
import com.rabobank.statementprocessor.processor.StatementRecordProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.io.File;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final String REFERENCE = "Reference";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String DESCRIPTION = "Description";
    private static final String START_BALANCE = "Start Balance";
    private static final String MUTATION = "Mutation";
    private static final String END_BALANCE = "End Balance";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job processJob() {
        return jobBuilderFactory
                .get("processJob")
                .incrementer(new RunIdIncrementer())
                .flow(processStep())
                .end()
                .build();
    }

    @Bean
    public Step processStep() {
        return stepBuilderFactory
                .get("processStep")
                .<StatementRecord, StatementRecord>chunk(5)
                .reader(reader("nofile"))
                .processor(statementProcessor())
                .writer(writer("nofile"))
                .build();
    }

    @Bean
    public ItemProcessor<StatementRecord, StatementRecord> statementProcessor() {
        return new StatementRecordProcessor();
    }

    @Bean
    @StepScope
    public AbstractItemCountingItemStreamItemReader<StatementRecord> reader(@Value("#{jobParameters[inputFile]}") String inputFile) {

        if (inputFile.endsWith(".csv")) {
            FlatFileItemReader<StatementRecord> itemReader = new FlatFileItemReader<>();
            itemReader.setLineMapper(csvLineMapper());
            itemReader.setLinesToSkip(1);
            itemReader.setResource(new FileSystemResource(inputFile));
            itemReader.setStrict(false);
            return itemReader;
        } else if (inputFile.endsWith(".xml")) {
            StaxEventItemReader<StatementRecord> xmlFileReader = new StaxEventItemReader<>();
            xmlFileReader.setResource(new FileSystemResource(inputFile));
            xmlFileReader.setFragmentRootElementName("record");

            Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
            studentMarshaller.setClassesToBeBound(StatementRecord.class);
            xmlFileReader.setUnmarshaller(studentMarshaller);

            return xmlFileReader;
        }
        return null;
    }

    @Bean
    public LineMapper<StatementRecord> csvLineMapper() {

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(REFERENCE, ACCOUNT_NUMBER, DESCRIPTION, START_BALANCE,
                MUTATION, END_BALANCE);

        BeanWrapperFieldSetMapper<StatementRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(StatementRecord.class);

        DefaultLineMapper<StatementRecord> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    @StepScope
    public AbstractItemStreamItemWriter<StatementRecord> writer(@Value("#{jobParameters[inputFile]}") String inputFile) {

        File file = new File(inputFile);
        FileSystemResource outputFile = new FileSystemResource(file.getParent() + "/validated-report.txt");

        BeanWrapperFieldExtractor<StatementRecord> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{REFERENCE, DESCRIPTION});

        DelimitedLineAggregator<StatementRecord> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<StatementRecord> writer = new FlatFileItemWriter<>();
        writer.setShouldDeleteIfExists(true);
        writer.setResource(outputFile);
        writer.setAppendAllowed(false);
        writer.setLineAggregator(lineAggregator);

        return writer;
    }
}
