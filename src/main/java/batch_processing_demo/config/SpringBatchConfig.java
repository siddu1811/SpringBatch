package batch_processing_demo.config;

import batch_processing_demo.entity.Customer;
import batch_processing_demo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class SpringBatchConfig {

    private CustomerRepository customerRepository;


    @Bean
    public FlatFileItemReader<Customer> reader() {

        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>(new ClassPathResource("customers.csv"), lineMapper());
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);

        return itemReader;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");

        BeanWrapperFieldSetMapper<Customer> fieldsetMapper = new BeanWrapperFieldSetMapper<>();
        fieldsetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldsetMapper);

        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor()
    {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer()
    {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step(JobRepository jobRepository) {

        return new StepBuilder("csv-step", jobRepository)
                .<Customer, Customer>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor((AsyncTaskExecutor) taskExecutor())
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository)
    {
        return new JobBuilder("importCustomers",jobRepository)
                .flow(step(jobRepository))
                .end().build();
    }

    @Bean
    public TaskExecutor taskExecutor()
    {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(100);
        return simpleAsyncTaskExecutor;
    }

}