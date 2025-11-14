/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import java.awt.geom.Point2D;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.shader.GameShader;
import necesse.level.maps.Level;

public class WaveShader
extends GameShader {
    public WaveShader() {
        super("vert", "fragWave");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public WaveState setupGrassWaveShader(Level level, int tileX, int tileY, long weaveTime, final float amount, final float height, int waves, final float textureHeightOffset, GameRandom random, long seed, boolean mirrored, float windMod) {
        float wind;
        long weave = level.grassWeave(tileX, tileY);
        float f = Settings.wavyGrass ? (mirrored ? -level.weatherLayer.getWindAmount(tileX, tileY) : level.weatherLayer.getWindAmount(tileX, tileY)) * windMod : (wind = 0.0f);
        if (weave > 0L || wind != 0.0f) {
            if (weave <= 0L) {
                weave = 0L;
            } else {
                GameRandom gameRandom = random;
                synchronized (gameRandom) {
                    if (random.seeded(seed).nextBoolean()) {
                        weave = -weave;
                    }
                }
            }
            final float bendTime = (float)weave / ((float)weaveTime / (float)waves / (float)Math.PI);
            return new WaveState(){

                @Override
                public void start() {
                    WaveShader.this.use();
                    WaveShader.this.pass1f("bendAmount", amount);
                    WaveShader.this.pass1f("bendHeight", height);
                    WaveShader.this.pass1f("bendTime", bendTime);
                    WaveShader.this.pass1f("windAmount", wind);
                    WaveShader.this.pass1f("heightOffset", textureHeightOffset);
                }

                @Override
                public void end() {
                    WaveShader.this.stop();
                }
            };
        }
        return null;
    }

    public WaveState setupGrassWaveShader(Level level, int tileX, int tileY, long weaveTime, float amount, float height, GameRandom random, long seed, boolean mirrored, float windMod) {
        return this.setupGrassWaveShader(level, tileX, tileY, weaveTime, amount, height, 2, 0.0f, random, seed, mirrored, windMod);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Consumer<TextureDrawOptionsPositionMod> setupGrassWaveMod(Level level, int tileX, int tileY, long weaveTime, float amount, int waves, GameRandom random, long seed, boolean mirrored, float windMod) {
        float wind;
        long weave = level.grassWeave(tileX, tileY);
        float f = wind = Settings.windEffects && windMod != 0.0f ? level.weatherLayer.getWindAmount(tileX, tileY) * windMod : 0.0f;
        if (weave > 0L || wind != 0.0f) {
            if (weave <= 0L) {
                weave = 0L;
            } else {
                GameRandom gameRandom = random;
                synchronized (gameRandom) {
                    if (random.seeded(seed).nextBoolean()) {
                        weave = -weave;
                    }
                }
            }
            float weaveTimePerWave = (float)weaveTime / (float)waves;
            float weaveProgress = (float)weave / weaveTimePerWave;
            float weaveOffset = GameMath.sin(weaveProgress * 180.0f) * amount;
            Point2D.Double windDir = level.weatherLayer.getWindDirNormalized();
            float windOffset = wind * amount;
            return o -> {
                float offset = (float)o.getWidth() * weaveOffset;
                o.addX1(offset);
                o.addX2(offset);
                float windOffsetX = (float)o.getWidth() * (float)windDir.x * windOffset;
                float windOffsetY = (float)o.getWidth() * (float)windDir.y * windOffset * 0.5f;
                o.addX1(windOffsetX);
                o.addX2(windOffsetX);
                o.addY1(windOffsetY);
                o.addY2(windOffsetY);
            };
        }
        return o -> {};
    }

    public static abstract class WaveState {
        public abstract void start();

        public abstract void end();
    }
}

