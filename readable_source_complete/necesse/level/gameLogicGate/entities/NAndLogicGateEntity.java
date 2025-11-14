/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.AndLogicGateEntity;
import necesse.level.maps.TilePosition;

public class NAndLogicGateEntity
extends AndLogicGateEntity {
    public NAndLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        super(logicGate, pos);
    }

    @Override
    public boolean condition() {
        return !super.condition();
    }
}

