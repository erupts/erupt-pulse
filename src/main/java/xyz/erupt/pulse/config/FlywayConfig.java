package xyz.erupt.pulse.config;

import xyz.erupt.pulse.prop.XProp;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Component
@Configuration
public class FlywayConfig {

    @Resource
    private XProp xProp;

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .table("e_flyway_schema_history")
                .baselineVersion(xProp.getBaselineOnMigrate())
                .baselineOnMigrate(true)
                .load();
    }

}
