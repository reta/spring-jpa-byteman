package com.example.spring.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.spring.services.CustomerService;

@EnableTransactionManagement
@Configuration
@ComponentScan( basePackageClasses = CustomerService.class )
public class AppConfig {
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor() {
        final PersistenceExceptionTranslationPostProcessor processor = new PersistenceExceptionTranslationPostProcessor();
        processor.setRepositoryAnnotationType( Service.class );
        return processor;
    }

	@Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        
        adapter.setDatabase( Database.MYSQL );
        adapter.setShowSql( false );
        
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManager() throws Throwable {
    	final LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
    	
        entityManager.setPersistenceUnitName( "customers" );
        entityManager.setDataSource( dataSource() );
        entityManager.setJpaVendorAdapter( hibernateJpaVendorAdapter() );

        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", MySQL5InnoDBDialect.class.getName());
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop" );
        entityManager.setJpaProperties( properties );

        return entityManager;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName( com.mysql.jdbc.Driver.class.getName() );
        dataSource.setUrl( "jdbc:mysql://localhost/customers?enableQueryTimeouts=true" );
        dataSource.setUsername( "root" );
        dataSource.setPassword( "" );

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Throwable {
    	return new JpaTransactionManager( this.entityManager().getObject() );
    }
}
