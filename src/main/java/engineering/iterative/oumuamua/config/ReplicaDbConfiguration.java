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

@Profile("replica")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "engineering.iterative.oumuamua",
    entityManagerFactoryRef = "replicaEntityManager",
    transactionManagerRef = "replicaTransactionManager")
public class ReplicaDbConfiguration {

  @Bean
  public LocalContainerEntityManagerFactoryBean replicaEntityManager() {

    Map<String, String> hibernateProperties = replicaHibernateProperties();

    HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    EntityManagerFactoryBuilder builder =
        new EntityManagerFactoryBuilder(hibernateJpaVendorAdapter, hibernateProperties, null);

    return builder
        .dataSource(replicaDataSource())
        .packages("engineering.iterative.oumuamua")
        .properties(hibernateProperties)
        .persistenceUnit("replica")
        .build();
  }

  @Bean
  @ConfigurationProperties(prefix = "oumuamua.replica.datasource")
  public Properties replicaHikariProperties() {
    return new Properties();
  }

  @Bean
  @ConfigurationProperties(prefix = "oumuamua.replica")
  public Properties replicaProperties() {
    return new Properties();
  }

  @Bean
  public Map<String, String> replicaHibernateProperties() {
    Map<String, String> hibernateProperties =
        replicaProperties().entrySet().stream()
            .filter(e -> e.getKey().toString().startsWith("hibernate"))
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));

    return hibernateProperties;
  }

  @Bean
  @ConfigurationProperties(prefix = "oumuamua.replica.liquibase")
  public LiquibaseProperties replicaLiquibaseProperties() {
    return new LiquibaseProperties();
  }

  @Bean
  public SpringLiquibase masterLiquibase() {
    return springLiquibase(replicaDataSource(), replicaLiquibaseProperties());
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


  @Bean
  public HikariConfig replicaHikariConfig() {
    HikariConfig hikariConfig = new HikariConfig(replicaHikariProperties());
    hikariConfig.setPoolName(hikariConfig.getPoolName() + UUID.randomUUID().hashCode());
    return hikariConfig;
  }

  @Bean
  public DataSource replicaDataSource() {
    return new HikariDataSource(replicaHikariConfig());
  }

  @Bean
  public PlatformTransactionManager replicaTransactionManager(
          @Qualifier("replicaEntityManager") EntityManagerFactory replicaEntityManager) {
    return new JpaTransactionManager(replicaEntityManager);
  }
}
