/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LevelIdentifier
 *  necesse.engine.world.WorldEntity
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.level.maps.Level
 *  necesse.level.maps.biomes.trial.TrialRoomLevel
 */
package aphorea.levels;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.trial.TrialRoomLevel;

public class InfectedTrialRoomLevel
extends TrialRoomLevel {
    int presentPlayersAnt = 0;

    public InfectedTrialRoomLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public InfectedTrialRoomLevel(LevelIdentifier levelIdentifier, WorldEntity worldEntity) {
        super(levelIdentifier, worldEntity);
    }

    public void serverTick() {
        super.serverTick();
        if (this.presentPlayersAnt != this.presentPlayers) {
            if (this.presentPlayersAnt == 0) {
                int summoned = 0;
                while (summoned < 3) {
                    int tileX = GameRandom.globalRandom.getIntBetween(0, 49);
                    int tileY = GameRandom.globalRandom.getIntBetween(0, 49);
                    if (tileX >= 21 && tileY <= 18 || this.getObject(tileX + 1, tileY).getID() != 0 || this.getObject(tileX - 1, tileY).getID() != 0 || this.getObject(tileX, tileY + 1).getID() != 0 || this.getObject(tileX, tileY - 1).getID() != 0 || this.getObject(tileX, tileY).getID() != 0 || !this.entityManager.mobs.getInRegionByTileRange(tileX, tileY, 5).isEmpty()) continue;
                    this.entityManager.addMob(MobRegistry.getMob((String)"infectedtreant", (Level)this), (float)(tileX * 32 + 16), (float)(tileY * 32 + 16));
                    ++summoned;
                }
            } else if (this.presentPlayers == 0) {
                for (Mob mob : this.entityManager.mobs) {
                    if (!mob.isHostile) continue;
                    mob.remove();
                }
            }
            this.presentPlayersAnt = this.presentPlayers;
        }
    }

    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        return Stream.concat(super.getMobModifiers(mob), Stream.of(new ModifierValue(BuffModifiers.BLINDNESS, (Object)Float.valueOf(0.6f))));
    }
}

