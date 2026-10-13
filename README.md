# erupt-pulse

基于 [Vert.x](https://vertx.io) TCP 长连接的轻量消息模块，以注解方式声明消息处理器，作为 [erupt](https://github.com/erupts/erupt) 模块自动装配。

## 环境要求

- JDK 17+
- Spring Boot 3.x / erupt 2.3.0+

## 引入

```xml
<dependency>
    <groupId>xyz.erupt</groupId>
    <artifactId>erupt-pulse</artifactId>
    <version>2.3.0</version>
</dependency>
```

## 使用

### 声明处理器

类上标注 `@Pulse`（同时须是 Spring Bean），方法上用 `@Path` 绑定消息路径：

```java
@Pulse
@Component
public class DemoPulse {

    @Path("/demo/hello")
    public void hello(PulseRequest request) {
        String data = request.getData();          // JSON 字符串
        NetSocket socket = request.getNetSocket(); // 来源连接，可用于回写
    }

}
```

启动时 `PulseCore` 会扫描 erupt 扫描包下所有 `@Pulse` 类并注册其 `@Path` 方法。

### 接收与发送

```java
@Resource
private PulseCore pulseCore;

// 将连接交给 PulseCore 解析，按 path 分发到对应处理器
pulseCore.handler(netSocket);

// 发送消息，data 会被序列化为 JSON
pulseCore.send(netSocket, "/demo/hello", data);
```

## 协议

每条消息是一个 JSON 序列化的 `PulseRequest`，以 `\0` 作为分包符：

| 字段 | 说明 |
|---|---|
| `machineId` | 机器唯一标识，取本地系统盘序列号的 SHA-256 |
| `requestId` | 雪花算法生成的请求 ID |
| `port` | 发送方 `server.port` |
| `path` | 消息路径，对应 `@Path` |
| `header` | 扩展头信息 |
| `data` | 消息体，JSON 字符串 |
| `end` | 结束标记 `__ERUPT_PULSE_END__` |
