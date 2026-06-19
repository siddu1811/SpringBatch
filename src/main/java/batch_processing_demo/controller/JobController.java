package batch_processing_demo.controller;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job job;

    @PostMapping("/importCustomer")
    public JobExecution importCsvToDBJob()
            throws JobInstanceAlreadyCompleteException,
            InvalidJobParametersException,
            JobExecutionAlreadyRunningException,
            JobRestartException {

        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("startAt", System.currentTimeMillis())
                        .toJobParameters();

        return jobOperator.start(job, jobParameters);
    }
}