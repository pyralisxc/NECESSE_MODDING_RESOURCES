/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.GLDrawOptionsList;

public class TileLightDrawList {
    private final GLDrawOptionsList drawOptions = new GLDrawOptionsList();

    public synchronized TileLightDrawList add(int drawX, int drawY, float[] advColor) {
        this.drawOptions.add(new float[]{drawX, drawY, drawX + 32, drawY, drawX + 32, drawY + 32, drawX, drawY + 32}, advColor);
        return this;
    }

    public synchronized void draw() {
        this.draw(SharedTextureDrawOptions.MAX_VERTEX_CALLS_PER_DRAW_CALL);
    }

    public synchronized void draw(int maxElementsPerCall) {
        this.drawOptions.draw(GameResources.empty, 7, 4, maxElementsPerCall);
    }
}

