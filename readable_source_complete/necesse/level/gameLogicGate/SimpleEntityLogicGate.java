/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate;

import java.util.function.BiFunction;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class SimpleEntityLogicGate
extends GameLogicGate {
    public BiFunction<GameLogicGate, TilePosition, LogicGateEntity> provider;

    public SimpleEntityLogicGate(BiFunction<GameLogicGate, TilePosition, LogicGateEntity> provider) {
        this.provider = provider;
    }

    @Override
    public LogicGateEntity getNewEntity(Level level, int tileX, int tileY) {
        return this.provider.apply(this, new TilePosition(level, tileX, tileY));
    }
}

