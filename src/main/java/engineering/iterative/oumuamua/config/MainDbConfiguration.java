package engineering.iterative.oumuamua.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("main")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "engineering.iterative.oumuamua",
    entityManagerFactoryRef = "masterEntityManager",
    transactionManagerRef = "masterTransactionManager")
public class MainDbConfiguration {

  @Bean
  public LocalContainerEntityManagerFactoryBean masterEntityManager() {

    Map<String, String> hibernateProperties = masterHibernateProperties();

    HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    EntityManagerFactoryBuilder builder =
        new EntityManagerFactoryBuilder(hibernateJpaVendorAdapter, hibernateProperties, null);

    return builder
        .dataSource(masterDataSource())
        .packages("engineering.iterative.oumuamua")
        .properties(hibernateProperties)
        .persistenceUnit("master")
        .build();
  }

  @Bean
  @ConfigurationProperties(prefix = "oumuamua.master.datasource")
  public Properties masterHikariProperties() {
    return new Properties();
  }

  @Bean
  @ConfigurationProperties(prefix = "oumuamua.master")
  public Properties masterProperties() {
    return new Properties();
  }

  @Bean
  public Map<String, String> masterHibernateProperties() {
    Map<String, String> hibernateProperties =
        masterProperties().entrySet().stream()
            .filter(e -> e.getKey().toString().startsWith("hibernate"))
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));

    return hibernateProperties;
  }

  @Bean
  @ConfigurationProperties(prefix = "oumuamua.master.liquibase")
  public LiquibaseProperties masterLiquibaseProperties() {
    return new LiquibaseProperties();
  }

  @Bean
  public HikariConfig masterHikariConfig() {
    HikariConfig hikariConfig = new HikariConfig(masterHikariProperties());
    hikariConfig.setPoolName(hikariConfig.getPoolName() + UUID.randomUUID().hashCode());
    return hikariConfig;
  }

  @Bean
  public DataSource masterDataSource() {
    return new HikariDataSource(masterHikariConfig());
  }

  @Bean
  public SpringLiquibase masterLiquibase() {
    return springLiquibase(masterDataSource(), masterLiquibaseProperties());
  }

  @Bean
  public PlatformTransactionManager masterTransactionManager(
          @Qualifier("masterEntityManager") EntityManagerFactory masterEntityManager) {
    return new JpaTransactionManager(masterEntityManager);
  }

  private static SpringLiquibase springLiquibase(
      DataSource dataSource, LiquibaseProperties properties) {
    SpringLiquibase liquibase = new SpringLiquibase();

    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog(properties.getChangeLog());
    liquibase.setContexts(properties.getContexts());
    liquibase.setDefaultSchema(properties.getDefaultSchema());
    liquibase.setDropFirst(properties.isDropFirst());
    liquibase.setShouldRun(properties.isEnabled());
    liquibase.setChangeLogParameters(properties.getParameters());
    liquibase.setRollbackFile(properties.getRollbackFile());

    return liquibase;
  }
}
