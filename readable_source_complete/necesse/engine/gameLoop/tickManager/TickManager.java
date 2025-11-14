/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.gfx.ui.debug.Debug;

public class TickManager
extends PerformanceTimerManager {
    public static float globalTimeMod = 1.0f;
    public static boolean skipDrawIfBehind = false;
    public static final int ticksPerSec = 20;
    public static final int msPerTick = 50;
    public static final int nsPerTick = 50000000;
    private double gameTickValue;
    private boolean gameTick;
    private long lastTickTime;
    private long totalTicks;
    private long lastResetTime;
    private long loopTime;
    private int fps;
    private int tps;
    private int frame;
    private int tick;
    private long totalFrames;
    private float delta;
    private float fullDelta;
    private long totalTicksSecond;
    private long totalExpectedTicks;
    private int maxFPS;
    private double sleepCount;
    private double msPerFrame;
    private final String name;

    public static float getTickDelta(float seconds) {
        return 1.0f / seconds / 20.0f;
    }

    public static double getTickDelta(long milliseconds) {
        return 1000.0 / (double)milliseconds / 20.0;
    }

    public TickManager(String name, int maxFPS) {
        this.name = name;
        this.setMaxFPS(maxFPS);
    }

    public void init() {
        long curNs = System.nanoTime();
        long curMs = System.currentTimeMillis();
        this.gameTickValue = 0.0;
        this.gameTick = false;
        this.lastTickTime = curNs;
        this.tick = 0;
        this.totalTicks = 0L;
        this.totalTicksSecond = 0L;
        this.totalExpectedTicks = 0L;
        this.lastResetTime = curMs;
        this.loopTime = curNs;
        this.totalFrames = 0L;
        this.fps = 0;
        this.delta = 0.0f;
        this.fullDelta = 0.0f;
        this.sleepCount = curMs;
    }

    public void tickLogic() {
        long curNs = System.nanoTime();
        long curMs = System.currentTimeMillis();
        if (this.maxFPS > 0) {
            this.sleepCount += this.msPerFrame / (double)globalTimeMod;
            int sleepTime = (int)(this.sleepCount - (double)curMs);
            if (sleepTime > 1) {
                try {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        this.gameTickValue += (double)(curNs - this.lastTickTime) / 5.0E7 * (double)globalTimeMod;
        this.lastTickTime = curNs;
        this.gameTick = false;
        if (this.gameTickValue >= 1.0) {
            this.gameTick = true;
            ++this.totalTicks;
            this.gameTickValue -= 1.0;
            ++this.tick;
        }
        ++this.frame;
        ++this.totalFrames;
        this.fullDelta = (float)((double)(curNs - this.loopTime) / 1000000.0 * (double)globalTimeMod);
        this.delta = Math.min(this.fullDelta, 100.0f);
        this.loopTime = curNs;
        this.update();
        if (curMs - this.lastResetTime > 1000L) {
            this.tps = this.tick;
            this.tick = 0;
            this.fps = this.frame;
            this.frame = 0;
            this.lastResetTime += 1000L;
            this.totalTicksSecond = this.totalTicks;
            this.totalExpectedTicks += 20L;
            this.sleepCount = System.currentTimeMillis();
            this.updateSecond();
            this.calcFrame(true, this.frame, this.totalFrames);
            this.nextRunOnlyConstantTimers = !Debug.isActive();
        } else {
            this.calcFrame(false, this.frame, this.totalFrames);
        }
    }

    public void update() {
    }

    public void updateSecond() {
    }

    public void setMaxFPS(int maxFPS) {
        if (maxFPS < 0) {
            maxFPS = Math.max(20, maxFPS);
        }
        this.maxFPS = maxFPS;
        this.msPerFrame = maxFPS != 0 ? 1000.0 / (double)maxFPS : 0.0;
        this.sleepCount = System.currentTimeMillis();
    }

    public boolean isBehind() {
        return this.gameTickValue >= 1.0;
    }

    public int getMaxFPS() {
        return this.maxFPS;
    }

    public String getName() {
        return this.name;
    }

    public float getDelta() {
        return this.delta;
    }

    public float getFullDelta() {
        return this.fullDelta;
    }

    public boolean isGameTick() {
        return this.gameTick;
    }

    public boolean isFirstGameTickInSecond() {
        return this.gameTick && this.tick == 1;
    }

    public boolean isGameTickInSecond(int tick) {
        if (tick >= 20) {
            tick %= 20;
        }
        return this.gameTick && this.tick == tick;
    }

    public long getTotalTicks() {
        return this.totalTicks;
    }

    public long getTotalExpectedTicks() {
        return this.totalExpectedTicks;
    }

    public long getSkippedTicks() {
        return this.totalExpectedTicks - this.totalTicksSecond;
    }

    public long getTotalFrames() {
        return this.totalFrames;
    }

    public int getFPS() {
        return this.fps;
    }

    public int getTPS() {
        return this.tps;
    }

    public int getFrame() {
        return this.frame;
    }

    public int getTick() {
        return this.tick;
    }

    @Override
    public TickManager getChild() {
        TickManager out = new TickManager(this.name, this.maxFPS);
        this.applyChildProperties(out);
        out.delta = this.delta;
        out.fullDelta = this.fullDelta;
        out.gameTick = this.gameTick;
        out.totalTicks = this.totalTicks;
        out.fps = this.fps;
        out.tps = this.tps;
        out.tick = this.tick;
        return out;
    }
}

