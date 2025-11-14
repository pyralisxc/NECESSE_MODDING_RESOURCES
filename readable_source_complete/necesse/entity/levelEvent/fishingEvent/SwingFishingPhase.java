/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.fishingEvent;

import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.levelEvent.fishingEvent.FishingPhase;
import necesse.entity.levelEvent.fishingEvent.HookFishingPhase;

public class SwingFishingPhase
extends FishingPhase {
    public SwingFishingPhase(FishingEvent event) {
        super(event);
    }

    @Override
    public void tickMovement(float delta) {
        if (this.event.getFishingMob().isFishingSwingDone()) {
            this.event.setPhase(new HookFishingPhase(this.event));
        }
    }

    @Override
    public void clientTick() {
    }

    @Override
    public void serverTick() {
    }

    @Override
    public void end() {
    }

    @Override
    public void over() {
    }
}

