package xyz.erupt.pulse;

import xyz.erupt.pulse.model.ScriptRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import xyz.erupt.core.annotation.EruptScan;
import xyz.erupt.core.module.EruptModule;
import xyz.erupt.core.module.EruptModuleInvoke;
import xyz.erupt.core.module.MetaMenu;
import xyz.erupt.core.module.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
@EruptScan
@EntityScan
@EnableConfigurationProperties
@Component
public class CommonConfiguration implements EruptModule {

    @Value("${spring.application.name}")
    private String applicationName;

    static {
        EruptModuleInvoke.addEruptModule(CommonConfiguration.class);
    }

    @Override
    public ModuleInfo info() {
        return ModuleInfo.builder().name("x-common-" + applicationName).build();
    }

    @Override
    public List<MetaMenu> initMenus() {
        List<MetaMenu> menus = new ArrayList<>();
        MetaMenu metaMenu = MetaMenu.createEruptClassMenu(ScriptRun.class, null, 300);
        metaMenu.setIcon("fa fa-play");
        menus.add(metaMenu);
        return menus;
    }
}
