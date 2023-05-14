package ru.mirea.secureapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.mirea.secureapp.repositories")
public class JPAConfig {
}
