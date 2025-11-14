/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.res;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import necesse.engine.util.GameMath;

public class GameStreamWriter
implements Closeable,
Flushable {
    private OutputStream stream;
    private long writtenBytes;

    public GameStreamWriter(OutputStream stream) {
        this.stream = stream;
    }

    public void writeBytes(byte[] data) throws IOException {
        this.stream.write(data);
        this.writtenBytes += (long)data.length;
    }

    public void writeByte(byte data) throws IOException {
        this.stream.write(data);
        ++this.writtenBytes;
    }

    public void writeShort(short data) throws IOException {
        for (int i = 0; i < 2; ++i) {
            this.writeByte(GameMath.getByte(data, i));
        }
    }

    public void writeInt(int data) throws IOException {
        for (int i = 0; i < 4; ++i) {
            this.writeByte(GameMath.getByte(data, i));
        }
    }

    public void writeLong(long data) throws IOException {
        for (int i = 0; i < 8; ++i) {
            this.writeByte(GameMath.getByte(data, i));
        }
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }

    public long getTotalWritten() {
        return this.writtenBytes;
    }
}

