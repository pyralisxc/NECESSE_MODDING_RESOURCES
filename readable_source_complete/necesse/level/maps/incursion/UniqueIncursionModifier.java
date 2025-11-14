/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.IncursionData;

public class UniqueIncursionModifier
implements IDDataContainer {
    public final IDData idData = new IDData();

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public int getID() {
        return this.idData.getID();
    }

    public LocalMessage getModifierName() {
        return new LocalMessage("ui", "incursionmodifier" + this.getStringID());
    }

    public LocalMessage getModifierDescription() {
        return new LocalMessage("ui", "incursionmodifier" + this.getStringID() + "info");
    }

    public int getModifierTickets(IncursionData data) {
        return 100;
    }

    public void onIncursionLevelGenerated(IncursionLevel level, int modifierIndex) {
    }

    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
    }
}

