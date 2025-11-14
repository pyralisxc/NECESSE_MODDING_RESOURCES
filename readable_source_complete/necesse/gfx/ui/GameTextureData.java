/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.io.Serializable;
import java.util.Arrays;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTexture.GameTexture;

public class GameTextureData
implements Serializable {
    public int width;
    public int height;
    private byte[] bufferCompressed;
    public boolean isFinal;
    public GameTexture.BlendQuality blendQuality;

    public GameTextureData(int width, int height, byte[] buffer, boolean isFinal, GameTexture.BlendQuality blendQuality) {
        this.width = width;
        this.height = height;
        try {
            this.bufferCompressed = GameUtils.compressData(buffer);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.bufferCompressed = new byte[0];
        }
        this.isFinal = isFinal;
        this.blendQuality = blendQuality;
    }

    public byte[] getBuffer() {
        try {
            return GameUtils.decompressData(this.bufferCompressed);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public void printData() {
        System.out.println("Width:  " + this.width);
        System.out.println("Height: " + this.height);
        System.out.println("Buffer: " + Arrays.toString(this.bufferCompressed));
    }

    public static GameTextureData fromCompressed(int width, int height, byte[] bufferCompressed, boolean isFinal, GameTexture.BlendQuality blendQuality) {
        GameTextureData out = new GameTextureData(0, 0, new byte[0], isFinal, blendQuality);
        out.width = width;
        out.height = height;
        out.bufferCompressed = bufferCompressed;
        return out;
    }
}

