/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.OrLogicGateEntity;
import necesse.level.maps.TilePosition;

public class NOrLogicGateEntity
extends OrLogicGateEntity {
    public NOrLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        super(logicGate, pos);
    }

    @Override
    public boolean condition() {
        return !super.condition();
    }
}

