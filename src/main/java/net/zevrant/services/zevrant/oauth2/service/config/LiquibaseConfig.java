package net.zevrant.services.zevrant.oauth2.service.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;

import static java.lang.String.format;

@Configuration
public class LiquibaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(LiquibaseConfig.class);

    private final ConfigurableApplicationContext applicationContext;

    @Autowired
    public LiquibaseConfig(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        removeDBLock(dataSource);
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
        if(Arrays.asList(profiles).contains("develop")) {
            liquibase.setChangeLog("classpath:db/changelog/db.changelog-master-develop.yaml");
        } else if(Arrays.asList(profiles).contains("local")) {
            liquibase.setChangeLog("classpath:db/changelog/db.changelog-master-local.yaml");
        } else {
            liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        }
        return liquibase;
    }



    private void removeDBLock(DataSource dataSource) {

        //Timestamp, currently set to 3 mins or older.

        final Timestamp lastDBLockTime = new Timestamp(System.currentTimeMillis() - (3 * 60 * 1000));

        logger.error(lastDBLockTime.toString());

        final String query = format("DELETE FROM DATABASECHANGELOGLOCK WHERE LOCKED=true AND LOCKGRANTED<'%s'", lastDBLockTime.toString());


        try (Statement stmt = dataSource.getConnection().createStatement()) {

            int updateCount = stmt.executeUpdate(query);
            if(updateCount>0){
                logger.error("Locks Removed Count: {} .",updateCount);
            }
        } catch (SQLException e) {
            logger.error("Error! Remove Change Lock threw and Exception. ",e);
        }
    }
}
