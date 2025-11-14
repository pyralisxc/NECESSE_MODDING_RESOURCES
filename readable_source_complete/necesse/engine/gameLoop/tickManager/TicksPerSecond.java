/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

public class TicksPerSecond {
    public int msPerTick;
    private float counter;

    private TicksPerSecond(int msPerTick) {
        this.msPerTick = msPerTick;
    }

    public static TicksPerSecond msPerTick(int msPerTick) {
        return new TicksPerSecond(msPerTick);
    }

    public static TicksPerSecond ticksPerSecond(int ticksPerSecond) {
        return TicksPerSecond.msPerTick(1000 / ticksPerSecond);
    }

    public void tick(float delta) {
        this.counter += delta;
    }

    public void gameTick() {
        this.tick(50.0f);
    }

    public boolean peekTick() {
        return this.counter > (float)this.msPerTick;
    }

    public boolean shouldTick() {
        if (this.peekTick()) {
            this.counter -= (float)this.msPerTick;
            return true;
        }
        return false;
    }

    public float getTicksPerSecond() {
        return 1000.0f / (float)this.msPerTick;
    }
}

