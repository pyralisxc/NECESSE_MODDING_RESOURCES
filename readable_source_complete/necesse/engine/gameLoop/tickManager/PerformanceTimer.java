/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

import necesse.engine.gameLoop.tickManager.AbstractPerformanceTimer;

public class PerformanceTimer
extends AbstractPerformanceTimer<PerformanceTimer> {
    public final boolean isFirstFrame;
    public final int secondFrame;
    public final long totalFrame;
    private long time;
    private int calls;

    public PerformanceTimer(String name, boolean isFirstFrame, int secondFrame, long totalFrame) {
        this(name, null, isFirstFrame, secondFrame, totalFrame);
    }

    private PerformanceTimer(String name, PerformanceTimer parent, boolean isFirstFrame, int secondFrame, long totalFrame) {
        super(name, parent);
        this.isFirstFrame = isFirstFrame;
        this.secondFrame = secondFrame;
        this.totalFrame = totalFrame;
        this.time = 0L;
        this.calls = 0;
    }

    public synchronized PerformanceTimer startChild(String name) {
        PerformanceTimer child = (PerformanceTimer)this.getChildren().get(name);
        if (child == null) {
            child = new PerformanceTimer(name, this, this.isFirstFrame, this.secondFrame, this.totalFrame);
            this.getChildren().put(name, child);
        }
        return child;
    }

    public synchronized void appendTime(long nanoTime) {
        this.time += nanoTime;
        ++this.calls;
    }

    public long getTime() {
        return this.time;
    }

    public int getCalls() {
        return this.calls;
    }

    public PerformanceTimer copy() {
        PerformanceTimer out = new PerformanceTimer(this.name, (PerformanceTimer)this.getParent(), this.isFirstFrame, this.secondFrame, this.totalFrame);
        for (PerformanceTimer child : this.getChildren().values()) {
            out.getChildren().put(child.name, child.copy());
        }
        return out;
    }
}

