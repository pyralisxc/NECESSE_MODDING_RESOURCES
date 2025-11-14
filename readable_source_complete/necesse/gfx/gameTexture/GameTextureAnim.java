/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTexture;

import necesse.gfx.gameTexture.GameTexture;

public class GameTextureAnim
extends GameTexture {
    private GameTexture[] textures;
    private long speed;

    public GameTextureAnim(String debugName, int width, int height, float cycleInSeconds, GameTexture[] textures) {
        super(debugName, width, height);
        this.textures = textures;
        this.speed = (long)(cycleInSeconds * 1000.0f);
        for (GameTexture t : textures) {
            t.getTextureID();
        }
    }

    @Override
    public int getTextureID() {
        float percent = (float)(System.currentTimeMillis() % this.speed) / (float)this.speed;
        int index = (int)(percent * (float)this.textures.length) % this.textures.length;
        return this.textures[index].getTextureID();
    }
}

