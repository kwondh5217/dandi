package com.e205.cdc;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.domain.Route;
import com.e205.domain.member.entity.Member;
import com.e205.entity.Comment;
import com.e205.entity.FoundItem;
import com.e205.entity.LostItem;
import com.e205.entity.Notification;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.junit.jupiter.api.Assertions;
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
class ClassInfoCacheTest {

  @MockBean
  private LettuceBasedProxyManager lettuceBasedProxyManager;
  @MockBean
  private BinlogReader binlogReader;
  @Container
  private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(
      "mysql:8.0")
      .withDatabaseName("dandi")
      .withUsername("root")
      .withPassword("1234")
      .withEnv("MYSQL_ROOT_PASSWORD", "1234")
      .withCopyFileToContainer(
          MountableFile.forClasspathResource("init.sql"),
          "/docker-entrypoint-initdb.d/init.sql"
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

  @Autowired
  private ClassInfoCache classInfoCache;

  @Test
  void whenStartApplicationClassInfoCacheShouldBeInitialized() {
    Assertions.assertAll(
        () -> assertThat(this.classInfoCache.getMappedClass("LostItem")).isNotNull(),
        () -> assertThat(this.classInfoCache.getMappedClass("LostItem")).isEqualTo(LostItem.class),
        () -> assertThat(this.classInfoCache.getMappedClass("FoundItem")).isNotNull(),
        () -> assertThat(this.classInfoCache.getMappedClass("FoundItem")).isEqualTo(FoundItem.class),
        () -> assertThat(this.classInfoCache.getMappedClass("Comment")).isNotNull(),
        () -> assertThat(this.classInfoCache.getMappedClass("Comment")).isEqualTo(Comment.class),
        () -> assertThat(this.classInfoCache.getMappedClass("Notification")).isNotNull(),
        () -> assertThat(this.classInfoCache.getMappedClass("Notification")).isEqualTo(Notification.class),
        () -> assertThat(this.classInfoCache.getMappedClass("Member")).isNotNull(),
        () -> assertThat(this.classInfoCache.getMappedClass("Member")).isEqualTo(Member.class),
        () -> assertThat(this.classInfoCache.getMappedClass("Route")).isNotNull(),
        () -> assertThat(this.classInfoCache.getMappedClass("Route")).isEqualTo(Route.class)
    );
  }

}