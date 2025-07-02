package xyz.erupt.pulse.pulse;

import cn.hutool.core.util.IdUtil;
import xyz.erupt.pulse.util.HardwareUtil;
import xyz.erupt.pulse.util.ProjectX;
import io.vertx.core.net.NetSocket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PulseRequest {

    public static final String END_DELIMITER = "__ERUPT_PULSE_END__";

    public static final String PULSE_PACK_SPLIT = "\0";

    public static final String MACHINE_ID = HardwareUtil.getMachineId();

    // Machine unique identification
    private String machineId = MACHINE_ID;

    private long requestId = IdUtil.getSnowflakeNextId();

    private String version = ProjectX.getVersion();

    private Integer port;

    private String path;

    private NetSocket netSocket;

    private Map<String, Object> header;

    private String data;

    private String end = END_DELIMITER;

    public static PulseRequest of(String path, String data) {
        return of(path, null, data);
    }

    public static PulseRequest of(String path, Map<String, Object> header, String data) {
        PulseRequest pulseRequest = new PulseRequest();
        pulseRequest.setPath(path);
        pulseRequest.setHeader(header);
        pulseRequest.setData(data);
        return pulseRequest;
    }

}
