package xyz.erupt.remote.util;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.SneakyThrows;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class HardwareUtil {

    public static void main(String[] args) {
        getMachineId();
    }

    public static String getMachineId() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        for (HWDiskStore diskStore : hal.getDiskStores()) {
            if (isLocalDisk(diskStore)) {
                String serial = diskStore.getSerial();
                if ("unknown".equalsIgnoreCase(serial)) {
                    serial = getSerialFromUdevadm(diskStore.getName());  // e.g., /dev/sda
                }
                return DigestUtil.sha256Hex(serial);
            }
        }
        throw new RuntimeException("No local disk found to generate machine ID");
    }

    @SneakyThrows
    public static String getSerialFromUdevadm(String device) {
        Process process = new ProcessBuilder("/usr/bin/udevadm", "info", "--query=all", "--name=" + device)
                .redirectErrorStream(true)
                .start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("E: ID_SERIAL_SHORT=")) {
                    return line.substring("E: ID_SERIAL_SHORT=".length()).trim();
                }
            }
        }
        throw new RuntimeException("No serial number found for device " + device);
    }

    public static boolean isLocalDisk(HWDiskStore diskStore) {
        String osName = System.getProperty("os.name").toLowerCase();
        String devicePath = diskStore.getName();
        String model = diskStore.getModel() != null ? diskStore.getModel().toLowerCase() : "";

        List<HWPartition> partitions = diskStore.getPartitions();
        for (HWPartition partition : partitions) {
            String mountPoint = partition.getMountPoint();
            String fsType = partition.getType() != null ? partition.getType().toLowerCase() : "";
            // Universal check: root mount point is usually local
            if ("/".equals(mountPoint) || "C:\\".equalsIgnoreCase(mountPoint)) {
                // Exclude known external device patterns
                if (!model.contains("usb") && !model.contains("flash") && !model.contains("external")) {
                    return true;
                }
            }
            if (osName.contains("win")) {
                // Windows: Check for typical drive letters like C:\, D:\ etc.
                if (mountPoint.matches("^[A-Z]:\\\\$") && !model.contains("usb") && !model.contains("flash")) {
                    return true;
                }
            } else if (osName.contains("linux")) {
                // Exclude typical removable media mount paths
                if (mountPoint != null && (mountPoint.startsWith("/media") || mountPoint.startsWith("/run/media"))) {
                    return false;
                }
                // Linux: Internal devices usually start with /dev/sdX, /dev/nvmeX or /dev/mapper
                if (devicePath.startsWith("/dev/sda")) {
                    return true;
                }
            } else if (osName.contains("mac")) {
                // macOS: Local disks usually use APFS or HFS, are under /dev/diskX, and mounted to /
                if ((fsType.contains("apfs") || fsType.contains("hfs")) &&
                        devicePath.startsWith("/dev/disk") &&
                        !model.contains("usb") &&
                        !mountPoint.startsWith("/Volumes")) {
                    return true;
                }
            }
        }
        // Default: not a local disk
        return false;
    }

}
