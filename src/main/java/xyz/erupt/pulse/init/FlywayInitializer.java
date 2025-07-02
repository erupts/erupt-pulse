package xyz.erupt.pulse.init;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class FlywayInitializer {

    @Resource
    private Flyway flyway;

    @PostConstruct
    public void migrate() {
        flyway.migrate();
    }

}
