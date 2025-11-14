/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.io.Serializable;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.GameTextureData;

public abstract class AbstractGameTextureCache {
    public abstract void set(String var1, int var2, GameTexture var3);

    public abstract Element get(String var1);

    public static class Element
    implements Serializable {
        public int hash;
        public GameTextureData textureData;

        public Element(int hash, GameTextureData textureData) {
            this.hash = hash;
            this.textureData = textureData;
        }
    }
}

