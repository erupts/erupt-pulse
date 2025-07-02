package xyz.erupt.remote;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import xyz.erupt.core.annotation.EruptScan;
import xyz.erupt.core.module.EruptModule;
import xyz.erupt.core.module.EruptModuleInvoke;
import xyz.erupt.core.module.ModuleInfo;

@Configuration
@ComponentScan
@EruptScan
@EntityScan
@EnableConfigurationProperties
@Component
public class PulseConfiguration implements EruptModule {

    static {
        EruptModuleInvoke.addEruptModule(PulseConfiguration.class);
    }

    @Override
    public ModuleInfo info() {
        return ModuleInfo.builder().name("erupt-pulse").build();
    }

}
