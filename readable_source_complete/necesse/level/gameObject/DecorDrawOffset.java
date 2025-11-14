/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

public class DecorDrawOffset {
    public int xOffset;
    public int yOffset;
    public int sortY;
    public boolean useShadowTexture;

    public DecorDrawOffset(int xOffset, int yOffset, int sortY, boolean useShadowTexture) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.sortY = sortY;
        this.useShadowTexture = useShadowTexture;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        return this.equals((DecorDrawOffset)object);
    }

    public boolean equals(DecorDrawOffset other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return this.xOffset == other.xOffset && this.yOffset == other.yOffset && this.useShadowTexture == other.useShadowTexture;
    }
}

