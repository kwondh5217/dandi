package com.e205.config;

import com.e205.log.LogInterceptor;
import com.e205.log.TransactionSynchronizationRegistryImpl;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JpaConfig {

  private static final String ENTITY_PACKAGE_TO_SCAN = "com.e205";
  private static final String HIBERNATE_SESSION_FACTORY_INTERCEPTOR = "hibernate.session_factory.interceptor";
  private static final String HIBERNATE_FORMAT_SQL_KEY = "hibernate.format_sql";
  private static final String HIBERNATE_HBM2DDL_AUTO_KEY = "hibernate.hbm2ddl.auto";

  @Value("${hibernate.hbm2ddl.auto:#{'none'}}")
  private String ddlValue;
  @Value("${hibernate.generateddl:#{false}}")
  private boolean generateDdl;
  @Value("${hibernate.showsql:#{false}}")
  private boolean showSql;
  @Value("${hibernate.formatsql:#{false}}")
  private boolean formatSql;

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

    Map<String, Object> jpaProperties = emf.getJpaPropertyMap();
    jpaProperties.put(HIBERNATE_SESSION_FACTORY_INTERCEPTOR, logInterceptor());
    jpaProperties.put(HIBERNATE_FORMAT_SQL_KEY, formatSql);
    jpaProperties.put(HIBERNATE_HBM2DDL_AUTO_KEY, ddlValue);

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
