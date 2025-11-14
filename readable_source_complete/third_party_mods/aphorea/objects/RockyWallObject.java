/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 *  necesse.entity.pickup.ItemPickupEntity
 *  necesse.level.gameObject.RockObject
 *  necesse.level.maps.Level
 */
package aphorea.objects;

import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.RockObject;
import necesse.level.maps.Level;

public class RockyWallObject
extends RockObject {
    public RockyWallObject(String rockTexture, Color rockColor, String droppedStone, int minStoneAmount, int maxStoneAmount, int placedStoneAmount) {
        super(rockTexture, rockColor, droppedStone, minStoneAmount, maxStoneAmount, placedStoneAmount, new String[0]);
    }

    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (new GameRandom().seeded(RockyWallObject.getTileSeed((int)x, (int)y)).getChance(0.05f)) {
            if (level.isServer()) {
                level.entityManager.addMob(MobRegistry.getMob((String)"rockygelslime", (Level)level), (float)(x * 32 + 16), (float)(y * 32 + 16));
            }
            if (level.isClient()) {
                level.entityManager.addParticle((ParticleOption)new SmokePuffParticle(level, (float)(x * 32 + 16), (float)(y * 32 + 32), AphColors.rock), Particle.GType.CRITICAL);
            }
            level.objectLayer.setObject(layerID, x, y, 0);
        } else {
            super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        }
    }
}

