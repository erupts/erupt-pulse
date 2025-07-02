package xyz.erupt.pulse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3DData {
    private final int width;
    private final int height;

    private byte[] mean;      // [H * W * 3]
    private byte[] normal;    // [H * W * 3]
    private float[] heightData; // [H * W]

    public V3DData(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public byte getMean(int x, int y, int c) {
        return mean[(y * width + x) * 3 + c];
    }


    public byte getNormal(int x, int y, int c) {
        return normal[(y * width + x) * 3 + c];
    }

    public float getHeight(int x, int y) {
        return heightData[y * width + x];
    }

}
