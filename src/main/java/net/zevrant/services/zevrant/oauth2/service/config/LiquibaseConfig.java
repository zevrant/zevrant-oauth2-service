package net.zevrant.services.zevrant.oauth2.service.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import static java.lang.String.format;

public class LiquibaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Bean
    public SpringLiquibase liquibase(SpringLiquibase liquibase, DataSource dataSource) {
        removeDBLock(dataSource);
        liquibase.setDataSource(dataSource);
        return liquibase;
    }



    private void removeDBLock(DataSource dataSource) {

        //Timestamp, currently set to 5 mins or older.

        final Timestamp lastDBLockTime = new Timestamp(System.currentTimeMillis() - (3 * 60 * 1000));

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
