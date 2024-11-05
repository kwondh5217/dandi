package com.e205.config;

import com.e205.log.LogInterceptor;
import com.e205.log.TransactionSynchronizationRegistryImpl;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
  private static final String CREATE_DROP = "create";
  private static final String NONE = "none";

  @Autowired(required = false)
  private RedissonClient redissonClient;

  @Bean
  public LogInterceptor logInterceptor() {
    return new LogInterceptor(new TransactionSynchronizationRegistryImpl());
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource) {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true); // 개발 환경에서 필요하면 true로 설정
    vendorAdapter.setShowSql(false); // 개발 환경에서 필요하면 true로 설정

    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setJpaVendorAdapter(vendorAdapter);
    emf.setDataSource(dataSource);
    emf.setPackagesToScan(ENTITY_PACKAGE_TO_SCAN);

    Map<String, Object> jpaProperties = emf.getJpaPropertyMap();
    jpaProperties.put(HIBERNATE_SESSION_FACTORY_INTERCEPTOR, logInterceptor());
    jpaProperties.put(HIBERNATE_FORMAT_SQL, true);
    jpaProperties.put(HIBERNATE_HBM2DDL_AUTO, CREATE_DROP);

    if (redissonClient != null) {
      jpaProperties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, true);
      jpaProperties.put(AvailableSettings.CACHE_REGION_FACTORY,
          "org.redisson.hibernate.RedissonRegionFactory");
      log.info("RedissonClient detected, enabling second-level cache with Redisson");
    } else {
      jpaProperties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, false);
      log.info("RedissonClient not detected, second-level cache is disabled");
    }

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
