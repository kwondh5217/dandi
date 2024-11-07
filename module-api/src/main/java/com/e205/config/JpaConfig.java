package com.e205.config;

import com.e205.log.LogInterceptor;
import com.e205.log.TransactionSynchronizationRegistryImpl;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class JpaConfig {

  private static final String ENTITY_PACKAGE_TO_SCAN = "com.e205";
  private static final String HIBERNATE_SESSION_FACTORY_INTERCEPTOR = "hibernate.session_factory.interceptor";
  private static final String HIBERNATE_FORMAT_SQL_KEY = "hibernate.format_sql";
  private static final String HIBERNATE_HBM2DDL_AUTO_KEY = "hibernate.hbm2ddl.auto";

  @Value("${spring.jpa.hibernate.ddl-auto:#{'none'}}")
  private String ddlValue;
  @Value("${spring.jpa.generate-ddl:#{false}}")
  private boolean generateDdl;
  @Value("${spring.jpa.show-sql:#{false}}")
  private boolean showSql;
  @Value("${spring.jpa.properties.hibernate.format_sql:#{false}}")
  private boolean formatSql;

  private final JpaProperties jpaProperty;

  @Bean
  public LogInterceptor logInterceptor() {
    return new LogInterceptor(new TransactionSynchronizationRegistryImpl());
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource) {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(generateDdl);
    vendorAdapter.setShowSql(showSql);

    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setJpaVendorAdapter(vendorAdapter);
    emf.setDataSource(dataSource);
    emf.setPackagesToScan(ENTITY_PACKAGE_TO_SCAN);

    Map<String, Object> jpaProperties = new HashMap<>(jpaProperty.getProperties());
    jpaProperties.put(HIBERNATE_SESSION_FACTORY_INTERCEPTOR, logInterceptor());
    jpaProperties.put(HIBERNATE_FORMAT_SQL_KEY, formatSql);
    jpaProperties.put(HIBERNATE_HBM2DDL_AUTO_KEY, ddlValue);
    emf.setJpaPropertyMap(jpaProperties);

    return emf;
  }

  @Bean
  public PlatformTransactionManager transactionManager(
      LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return transactionManager;
  }
}
