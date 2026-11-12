# AGENTS.md

This file provides guidance to coding agents (Claude Code, Codex, ...) when working with code in this repository.

## What this repository is

`erupt-pulse` is a small erupt module (a library jar, Java 17) that routes messages arriving over a Vert.x TCP
socket to annotated methods. Its Maven parent is `xyz.erupt:erupt` (the erupt framework's parent pom) and it
depends on `erupt-core`, so the erupt version is set in one place: `<parent><version>` in `pom.xml`. The
README is only a title.

## Commands

```bash
mvn clean install     # build the jar; the erupt parent pom and erupt-core must be resolvable at the parent's version
mvn compile           # compile only
```

There are no tests and no runnable application in this repository; the module runs inside a host erupt
application.

## Architecture

- `EruptPulseConfiguration` is the module entry: an `EruptModule` that registers itself with
  `EruptModuleInvoke` and is loaded through
  `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- `PulseCore` scans the host application's erupt scan packages at startup for classes annotated `@Pulse` and
  maps every method annotated `@Path("...")` by its path. `handler(NetSocket)` splits the stream into
  messages, parses each as a `PulseRequest` and invokes the mapped method on its Spring bean with the request
  as the only argument; `send(...)` writes a request back. Nothing here opens a server or a client socket:
  the host creates the Vert.x connection and hands the `NetSocket` to `handler`.
- Wire format: one GSON-serialized `PulseRequest` (erupt's `GsonFactory`) per message, messages separated by
  `PulseRequest.PULSE_PACK_SPLIT` (`\0`). A request carries `path`, `header`, `data` (a JSON string),
  `requestId` (snowflake), the sender's HTTP `port` and `machineId`.
- `HardwareUtil.getMachineId()` is the SHA-256 of the first local disk's serial number (OSHI, with a
  `udevadm` fallback); it is evaluated once when `PulseRequest` is loaded and throws when no local disk is
  found.
