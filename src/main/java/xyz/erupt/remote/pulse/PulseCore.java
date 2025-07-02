package xyz.erupt.remote.pulse;

import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import xyz.erupt.core.config.GsonFactory;
import xyz.erupt.core.service.EruptApplication;
import xyz.erupt.core.util.EruptSpringUtil;
import xyz.erupt.remote.annotation.Path;
import xyz.erupt.remote.annotation.Pulse;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PulseCore {

    // <path,method>
    private final Map<String, Method> pulsePathMethodMap = new HashMap<>();

    @Resource
    private ServerProperties serverProperties;

    @PostConstruct
    public void init() {
        EruptSpringUtil.scannerPackage(EruptApplication.getScanPackage(), new TypeFilter[]{
                new AnnotationTypeFilter(Pulse.class)
        }, clazz -> {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Path.class)) {
                    Path path = method.getAnnotation(Path.class);
                    pulsePathMethodMap.put(path.value(), method);
                }
            }
        });
    }


    public void handler(NetSocket socket) {
        StringBuilder buffer = new StringBuilder();
        socket.handler(data -> {
            buffer.append(data.toString());
            String delimiter = PulseRequest.PULSE_PACK_SPLIT;
            int index;
            while ((index = buffer.indexOf(delimiter)) != -1) {
                String jsonStr = buffer.substring(0, index);
                buffer.delete(0, index + delimiter.length());
                try {
                    PulseRequest pulseRequest = GsonFactory.getGson().fromJson(jsonStr, PulseRequest.class);
                    pulseRequest.setNetSocket(socket);
                    this.call(pulseRequest);
                } catch (Exception e) {
                    log.error("Error parsing PulseRequest: {}", e.getMessage());
                }
            }
        });
    }

    public void call(PulseRequest pulseRequest) {
        Method method = pulsePathMethodMap.get(pulseRequest.getPath());
        if (null == method) {
            throw new RuntimeException("Pulse path not found: " + pulseRequest.getPath());
        }
        try {
            method.invoke(EruptSpringUtil.getBean(method.getDeclaringClass()), pulseRequest);
        } catch (Exception e) {
            log.error("Pulse call error: ", e);
            throw new RuntimeException("Pulse call error: " + e.getMessage());
        }
    }

    public void send(NetSocket netSocket, String path, Object data) {
        PulseRequest pulseRequest = PulseRequest.of(path, GsonFactory.getGson().toJson(data));
        pulseRequest.setPort(serverProperties.getPort());
        netSocket.write(GsonFactory.getGson().toJson(pulseRequest) + PulseRequest.PULSE_PACK_SPLIT);
    }

}
