/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.res;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import necesse.engine.modLoader.InputStreamSupplier;
import necesse.engine.util.GameMath;

public class GameStreamReader
implements Closeable {
    private InputStreamSupplier streamSupplier;
    private InputStream stream;
    private long readBytes;

    public GameStreamReader(InputStreamSupplier streamSupplier) throws IOException {
        this.streamSupplier = streamSupplier;
        this.stream = streamSupplier.get();
    }

    public long skipBytes(long bytes) throws IOException {
        this.readBytes += bytes;
        return this.stream.skip(bytes);
    }

    public InputStreamSupplier getSupplierAtCurrent() {
        long readBytes = this.readBytes;
        return () -> {
            InputStream inputStream = this.streamSupplier.get();
            long skipped = inputStream.skip(readBytes);
            if (skipped != readBytes) {
                throw new IOException("Only skipped " + skipped + " bytes out of " + readBytes);
            }
            return inputStream;
        };
    }

    public int readBytes(byte[] bytes) throws IOException {
        int read = this.stream.read(bytes);
        if (read != -1) {
            this.readBytes += (long)read;
        }
        return read;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] out = new byte[length];
        this.readBytes(out);
        return out;
    }

    public byte readByte() throws IOException {
        ++this.readBytes;
        return (byte)this.stream.read();
    }

    public short readShort() throws IOException {
        short out = 0;
        for (int i = 0; i < 8; ++i) {
            out = GameMath.setByte(out, i, this.readByte());
        }
        return out;
    }

    public int readInt() throws IOException {
        int out = 0;
        for (int i = 0; i < 4; ++i) {
            out = GameMath.setByte(out, i, this.readByte());
        }
        return out;
    }

    public long readLong() throws IOException {
        int out = 0;
        for (int i = 0; i < 8; ++i) {
            out = GameMath.setByte(out, i, this.readByte());
        }
        return out;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}

