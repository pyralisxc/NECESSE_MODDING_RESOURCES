/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

public class SourcedLightModifier {
    public final int sourceX;
    public final int sourceY;
    private final byte value;

    public SourcedLightModifier(int sourceX, int sourceY, int value) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.value = (byte)value;
    }

    public int getValue() {
        return this.value & 0xFF;
    }
}

