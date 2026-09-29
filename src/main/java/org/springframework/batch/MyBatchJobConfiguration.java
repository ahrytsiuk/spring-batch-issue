package org.springframework.batch;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Objects;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class MyBatchJobConfiguration {


    @Bean
    public Step step(JobRepository jobRepository, JdbcTransactionManager transactionManager) {
        return new StepBuilder("step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("hello world");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .observationRegistry(new JobObservationRegistry())
                .start(step)
                .build();
    }

    /*
     * Infrastructure beans configuration
     */

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("/org/springframework/batch/core/schema-h2.sql")
                .build();
    }

    @Bean
    public JdbcTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    public ObservationRegistry observationRegistry() {
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        observationRegistry.observationConfig()
                .observationHandler(new DefaultMeterObservationHandler(Metrics.globalRegistry));
        return observationRegistry;
    }

    public static class JobObservationRegistry implements ObservationRegistry {
        @Override
        public @Nullable Observation getCurrentObservation() {
            return null;
        }

        @Override
        public Observation.@Nullable Scope getCurrentObservationScope() {
            return null;
        }

        @Override
        public void setCurrentObservationScope(Observation.@Nullable Scope current) {

        }

        @Override
        public ObservationConfig observationConfig() {
            return null;
        }

        @Override
        public boolean isNoop() {
            return true;
        }
    }

    public static Object getObservationRegistry(Job job) {
        Field observationRegistryField = ReflectionUtils.findField(job.getClass(), "observationRegistry");
        Objects.requireNonNull(observationRegistryField, "observationRegistryField must not be null");
        ReflectionUtils.makeAccessible(observationRegistryField);
        return ReflectionUtils.getField(observationRegistryField, job);
    }

    /*
     * Main method to run the application and exhibit the issue
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(MyBatchJobConfiguration.class);
        Job job = context.getBean(Job.class);
        System.out.println("Job '" + job.getName() + "' has configured ObservationRegistry of type: '" + getObservationRegistry(job) + "'");
    }

}
