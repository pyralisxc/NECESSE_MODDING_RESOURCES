/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.postProcessing;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.shader.ShockwaveShader;

public class PostProcessingEffects {
    private static float sceneRed = 1.0f;
    private static float sceneGreen = 1.0f;
    private static float sceneBlue = 1.0f;
    private static boolean nextSceneUpdate = false;
    private static float nextSceneRed;
    private static float nextSceneGreen;
    private static float nextSceneBlue;
    private static boolean nextSceneDarknessUpdate;
    private static float nextSceneDarkness;
    private static int nextSceneDarknessMinViewDist;
    private static int nextSceneDarknessFadeDist;
    private static int sceneDarknessMidX;
    private static int sceneDarknessMidY;
    private static DarknessFade sceneDarknessFade;
    public static LinkedList<AbstractShockwaveEffect> shockwaveEffects;

    public static void initialize() {
    }

    public static void preGameTick(TickManager tickManager) {
        if (tickManager.isGameTick()) {
            nextSceneUpdate = false;
            nextSceneDarknessUpdate = false;
            nextSceneDarkness = 0.0f;
            nextSceneDarknessMinViewDist = 0;
            nextSceneDarknessFadeDist = 1000;
        }
    }

    public static void postGameTick(TickManager tickManager) {
        if (tickManager.isGameTick()) {
            GameWindow window = WindowManager.getWindow();
            if (nextSceneUpdate) {
                sceneRed = nextSceneRed;
                sceneGreen = nextSceneGreen;
                sceneBlue = nextSceneBlue;
            } else {
                sceneRed = 1.0f;
                sceneGreen = 1.0f;
                sceneBlue = 1.0f;
            }
            window.setSceneShade(sceneRed, sceneGreen, sceneBlue);
            if (sceneDarknessFade != null && !sceneDarknessFade.apply()) {
                sceneDarknessFade = null;
            }
            GameResources.darknessShader.midScreenX = sceneDarknessMidX;
            GameResources.darknessShader.midScreenY = sceneDarknessMidY;
            window.setSceneDarkness(nextSceneDarkness, nextSceneDarknessMinViewDist, nextSceneDarknessFadeDist);
            shockwaveEffects.removeIf(AbstractShockwaveEffect::shouldRemove);
        }
    }

    public static void setSceneShade(float red, float green, float blue) {
        if (!nextSceneUpdate) {
            nextSceneRed = red;
            nextSceneGreen = green;
            nextSceneBlue = blue;
            nextSceneUpdate = true;
        } else if (red == sceneRed && green == sceneGreen && blue == sceneBlue) {
            nextSceneRed = red;
            nextSceneGreen = green;
            nextSceneBlue = blue;
        }
    }

    public static void setSceneShade(Color color) {
        PostProcessingEffects.setSceneShade((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f);
    }

    public static void setSceneDarkness(float percent, int minViewDist, int fadeDist, int midX, int midY) {
        if (!nextSceneDarknessUpdate || percent > nextSceneDarkness) {
            nextSceneDarkness = percent;
        }
        if (!nextSceneDarknessUpdate || minViewDist < nextSceneDarknessMinViewDist) {
            nextSceneDarknessMinViewDist = minViewDist;
        }
        if (!nextSceneDarknessUpdate || fadeDist < nextSceneDarknessFadeDist) {
            nextSceneDarknessFadeDist = fadeDist;
        }
        sceneDarknessMidX = midX;
        sceneDarknessMidY = midY;
        nextSceneDarknessUpdate = true;
    }

    public static void setSceneDarknessFade(float startPercent, float endPercent, int minViewDist, int fadeDist, int timeSpan, Supplier<Point> midGetter, Supplier<Boolean> isValid) {
        sceneDarknessFade = new DarknessFade(startPercent, endPercent, minViewDist, fadeDist, System.currentTimeMillis(), timeSpan, midGetter, isValid);
    }

    public static void addShockwaveEffect(AbstractShockwaveEffect effect) {
        shockwaveEffects.add(effect);
    }

    public static void addShockwaveEffect(final Client client, int centerX, int centerY, float maxDistance, float fadeInDistance, float fadeOutDistance, float size, int speed) {
        final WorldEntity worldEntity = client.worldEntity;
        if (worldEntity == null) {
            return;
        }
        shockwaveEffects.add(new ShockwaveEffect(centerX, centerY, maxDistance, fadeInDistance, fadeOutDistance, size, speed){

            @Override
            public long getCurrentTime() {
                return worldEntity.getLocalTime();
            }

            @Override
            public boolean shouldRemove() {
                return super.shouldRemove() || client.hasDisconnected();
            }
        });
    }

    static {
        nextSceneDarknessUpdate = false;
        shockwaveEffects = new LinkedList();
    }

    private static class DarknessFade {
        public float startDarkness;
        public float endDarkness;
        public int minViewDist;
        public int fadeDist;
        public long startTime;
        public long timeSpan;
        public Supplier<Point> midGetter;
        public Supplier<Boolean> isValid;

        public DarknessFade(float startDarkness, float endDarkness, int minViewDist, int fadeDist, long startTime, long timeSpan, Supplier<Point> midGetter, Supplier<Boolean> isValid) {
            this.startDarkness = startDarkness;
            this.endDarkness = endDarkness;
            this.minViewDist = minViewDist;
            this.fadeDist = fadeDist;
            this.startTime = startTime;
            this.timeSpan = timeSpan;
            this.midGetter = midGetter;
            this.isValid = isValid;
        }

        public boolean apply() {
            if (this.isValid != null && !this.isValid.get().booleanValue()) {
                return false;
            }
            long timeSinceStart = System.currentTimeMillis() - this.startTime;
            if (timeSinceStart > this.timeSpan) {
                return false;
            }
            float percent = (float)timeSinceStart / (float)this.timeSpan;
            float actualDarkness = this.startDarkness + (this.endDarkness - this.startDarkness) * percent;
            Point mid = this.midGetter.get();
            PostProcessingEffects.setSceneDarkness(GameMath.limit(actualDarkness, 0.0f, 1.0f), this.minViewDist, this.fadeDist, mid.x, mid.y);
            return true;
        }
    }

    public static abstract class ShockwaveEffect
    implements AbstractShockwaveEffect {
        public int centerX;
        public int centerY;
        public float maxDistance;
        public float fadeInDistance;
        public float fadeOutDistance;
        public float size;
        public int speed;
        public long spawnTime;

        public ShockwaveEffect(int centerX, int centerY, float maxDistance, float fadeInDistance, float fadeOutDistance, float size, int speed) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.maxDistance = maxDistance;
            this.fadeInDistance = fadeInDistance;
            this.fadeOutDistance = fadeOutDistance;
            this.size = size;
            this.speed = speed;
            this.spawnTime = this.getCurrentTime();
        }

        @Override
        public int getDrawX(GameCamera camera) {
            return camera.getDrawX(this.centerX);
        }

        @Override
        public int getDrawY(GameCamera camera) {
            return camera.getDrawY(this.centerY);
        }

        @Override
        public float getCurrentDistance() {
            long timeSinceSpawn = this.getCurrentTime() - this.spawnTime;
            return Entity.getPositionAfterMillis(this.speed, timeSinceSpawn);
        }

        @Override
        public float getSize() {
            float currentDistance = this.getCurrentDistance();
            float currentSize = this.size;
            if (currentDistance < this.fadeInDistance) {
                currentSize *= currentDistance / this.fadeInDistance;
            }
            if (this.maxDistance > 0.0f && currentDistance > this.maxDistance - this.fadeOutDistance) {
                currentSize *= (this.maxDistance - currentDistance) / this.fadeOutDistance;
            }
            return currentSize;
        }

        public abstract long getCurrentTime();

        @Override
        public float getEasingScale() {
            return 1.0f;
        }

        @Override
        public float getEasingPower() {
            return 2.0f;
        }

        @Override
        public boolean shouldRemove() {
            return this.getCurrentDistance() > this.maxDistance;
        }
    }

    public static interface AbstractShockwaveEffect {
        public int getDrawX(GameCamera var1);

        public int getDrawY(GameCamera var1);

        public float getCurrentDistance();

        public float getSize();

        public float getEasingScale();

        public float getEasingPower();

        public boolean shouldRemove();

        default public void setupShader(ShockwaveShader shader, GameCamera camera) {
            shader.pass1i("drawX", this.getDrawX(camera));
            shader.pass1i("drawY", this.getDrawY(camera));
            shader.pass1f("waveDistance", this.getCurrentDistance());
            shader.pass1f("waveSize", this.getSize());
            shader.pass1f("easingScale", this.getEasingScale());
            shader.pass1f("easingPower", this.getEasingPower());
        }
    }
}

