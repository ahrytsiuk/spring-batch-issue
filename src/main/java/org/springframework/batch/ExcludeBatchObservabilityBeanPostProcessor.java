package org.springframework.batch;

import org.springframework.batch.core.configuration.annotation.BatchObservabilityBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ExcludeBatchObservabilityBeanPostProcessor {

    @Bean
    static BeanDefinitionRegistryPostProcessor removeBatchObservabilityBeanPostProcessor() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                if (registry.containsBeanDefinition(BatchObservabilityBeanPostProcessor.class.getName())) {
                    registry.removeBeanDefinition(BatchObservabilityBeanPostProcessor.class.getName());
                }
            }
        };
    }

}
