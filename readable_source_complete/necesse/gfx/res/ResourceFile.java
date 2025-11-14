/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.res;

import java.io.IOException;
import java.io.InputStream;
import necesse.engine.modLoader.InputStreamSupplier;
import necesse.engine.util.GameUtils;

public class ResourceFile {
    public final String path;
    private final int length;
    private final InputStreamSupplier inputStreamSupplier;

    public ResourceFile(String path, int length, InputStreamSupplier inputStreamSupplier) {
        this.path = path;
        this.length = length;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    public byte[] loadBytes(boolean throwOnUnexpectedLength) throws IOException {
        try (InputStream inputStream = this.inputStreamSupplier.get();){
            if (this.length == -1) {
                byte[] byArray = GameUtils.loadInputStream(inputStream);
                return byArray;
            }
            byte[] data = new byte[this.length];
            int read = inputStream.read(data, 0, this.length);
            if (throwOnUnexpectedLength && read != this.length) {
                throw new IOException("Read unexpected size of resource file " + this.path + ": " + read + " bytes read out of " + this.length);
            }
            byte[] byArray = data;
            return byArray;
        }
    }
}

