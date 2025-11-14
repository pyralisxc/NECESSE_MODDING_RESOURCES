/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events;

import necesse.engine.events.GameEvent;

public class PreventableGameEvent
extends GameEvent {
    private boolean isPrevented;

    public void preventDefault() {
        this.isPrevented = true;
    }

    public boolean isPrevented() {
        return this.isPrevented;
    }
}

