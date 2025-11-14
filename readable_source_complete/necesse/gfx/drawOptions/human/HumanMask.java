/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.human;

import necesse.engine.util.GameMath;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class HumanMask {
    public final GameSprite sprite64;
    public final GameSprite sprite128;

    public HumanMask(GameSprite sprite64, GameSprite sprite128) {
        this.sprite64 = sprite64;
        this.sprite128 = sprite128;
    }

    public HumanMask(GameTexture texture64, GameTexture texture128) {
        this(new GameSprite(texture64), new GameSprite(texture128));
    }

    public HumanMask(GameSprite sprite64) {
        this.sprite64 = sprite64;
        GameTexture texture = new GameTexture("humanmask", 128, 128);
        int spriteXMin = sprite64.spriteX * sprite64.spriteWidth;
        int spriteYMin = sprite64.spriteY * sprite64.spriteHeight;
        for (int x = 0; x < 128; ++x) {
            for (int y = 0; y < 128; ++y) {
                int spriteX = spriteXMin + x;
                int spriteY = spriteYMin + y;
                int sampleX = GameMath.limit(spriteX - 32, spriteXMin, spriteXMin + 63);
                int sampleY = GameMath.limit(spriteY - 32, spriteYMin, spriteYMin + 63);
                texture.setPixel(x, y, sprite64.texture.getPixel(sampleX, sampleY));
            }
        }
        this.sprite128 = new GameSprite(texture);
    }

    public HumanMask(GameTexture texture64) {
        this(new GameSprite(texture64));
    }
}

