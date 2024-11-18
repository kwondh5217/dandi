package com.e205.cdc;

import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
class BinlogPositionTrackerTest {

  @MockBean
  private LettuceBasedProxyManager lettuceBasedProxyManager;
  @Autowired
  private BinlogReader binlogReader;

  @Container
  private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("testdb")
      .withUsername("root")
      .withPassword("1234")
      .withEnv("MYSQL_ROOT_PASSWORD", "1234")
      .withCopyFileToContainer(
          MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql"
      )
      .withCopyFileToContainer(
          MountableFile.forClasspathResource("testcontainers-mysql-config.cnf"),
          "/etc/mysql/conf.d/custom.cnf"
      )
      .withCreateContainerCmdModifier(cmd -> {
        cmd.withCmd("--defaults-file=/etc/mysql/conf.d/custom.cnf");
      });


  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
  }

  @Test
  void test() throws InterruptedException {
    Thread.sleep(100000);
    this.binlogReader.close();
  }
}