package com.conleos.data.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class UserDatasourceConfig {


    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean
    public DataSource userDS(){
        return userDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate userJdbcTemplate(@Qualifier("userDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
