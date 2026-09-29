package org.springframework.batch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.batch.core.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.springframework.batch.MyBatchJobConfiguration.getObservationRegistry;

public class MyBatchJobObservationTest {

    @Nested
    @SpringJUnitConfig({MyBatchJobConfiguration.class, ExcludeBatchObservabilityBeanPostProcessor.class})
    class WithObservabilityAndBatchObservabilityBeanPostProcessorExcluded {

        @Autowired
        private Job job;

        @Test
        void testJobObservationRegistry() {
            assertJobUsesObservationRegistry(job, MyBatchJobConfiguration.JobObservationRegistry.class);
        }

    }

    @Nested
    @SpringJUnitConfig({MyBatchJobConfiguration.class})
    class WithObservabilityAndBatchObservabilityBeanPostProcessorIncluded {

        @Autowired
        private Job job;

        @Test
        void testJobObservationRegistry() {
            assertJobUsesObservationRegistry(job, MyBatchJobConfiguration.JobObservationRegistry.class);
        }

    }

    private static void assertJobUsesObservationRegistry(Job job, Class<?> expected) {
        Object observationRegistry = getObservationRegistry(job);
        Assertions.assertInstanceOf(expected, observationRegistry);
    }

}
