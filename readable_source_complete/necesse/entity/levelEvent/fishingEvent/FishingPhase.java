/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.fishingEvent;

import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.inventory.InventoryItem;

public abstract class FishingPhase {
    public final FishingEvent event;

    public FishingPhase(FishingEvent event) {
        this.event = event;
    }

    public abstract void tickMovement(float var1);

    public abstract void clientTick();

    public abstract void serverTick();

    public abstract void end();

    public abstract void over();

    public void addNewCatch(int lineIndex, int inTicks, InventoryItem item) {
    }

    public void reel() {
    }

    public int getTicksToNextCatch() {
        return 500;
    }
}

