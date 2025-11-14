/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.Objects;
import necesse.engine.window.WindowManager;

public abstract class ClipboardTracker<T> {
    public long pollingRate;
    public boolean useDefault;
    private long lastPoll;
    private String lastClipboard;
    private T lastValue;

    public ClipboardTracker(long pollingRate, boolean useDefault) {
        this.pollingRate = pollingRate;
        this.useDefault = useDefault;
        this.forceUpdate();
    }

    public ClipboardTracker(long pollingRate) {
        this(pollingRate, false);
    }

    public ClipboardTracker(boolean useDefault) {
        this(500L, useDefault);
    }

    public ClipboardTracker() {
        this(500L);
    }

    public abstract T parse(String var1);

    public abstract void onUpdate(T var1);

    public void update() {
        if (this.lastPoll + this.pollingRate < System.currentTimeMillis()) {
            this.forceUpdate();
        }
    }

    public void forceUpdate() {
        this.lastPoll = System.currentTimeMillis();
        String newClipboard = this.getNewClipboard();
        if (!Objects.equals(newClipboard, this.lastClipboard)) {
            this.lastClipboard = newClipboard;
            this.lastValue = this.parse(this.lastClipboard);
            this.onUpdate(this.lastValue);
        }
    }

    public T getValue() {
        return this.lastValue;
    }

    protected String getNewClipboard() {
        if (this.useDefault) {
            return WindowManager.getWindow().getClipboardDefault();
        }
        return WindowManager.getWindow().getClipboard();
    }
}

