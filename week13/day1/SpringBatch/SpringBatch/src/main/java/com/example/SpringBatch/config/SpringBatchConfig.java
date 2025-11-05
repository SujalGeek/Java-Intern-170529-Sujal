package com.example.SpringBatch.config;

import com.example.SpringBatch.entity.Person;
import com.example.SpringBatch.repository.PersonRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SpringBatchConfig {

    @Autowired
    private PersonRepository personRepository;

    @Bean
    public FlatFileItemReader<Person> reader(){
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("people.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper())
//                .targetType(Person.class)
                .build();
    }

    private LineMapper<Person> lineMapper(){
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("customerId","firstName","lastName","company","city","country","phone1","phone2","email","subscriptionDate","website");

        BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Person.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    PersonProcessor processor(){
        return new PersonProcessor();
    }

    @Bean
    RepositoryItemWriter<Person> writer(){
        RepositoryItemWriter<Person> writer = new RepositoryItemWriter<>();
        writer.setRepository(personRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step)
    {
        return new JobBuilder("importPersons",jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step csvImportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager)
    {
        return new StepBuilder("csv-import-step",jobRepository)
                .<Person,Person>chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importPersonsJob(JobRepository jobRepository, Step csvImportStep) {
        return new JobBuilder("importPersonsJob", jobRepository)
                .start(csvImportStep)
                .build();
    }
}
