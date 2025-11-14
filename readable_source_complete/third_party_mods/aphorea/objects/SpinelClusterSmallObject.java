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
 *  necesse.inventory.InventoryItem
 *  necesse.level.gameObject.CrystalClusterSmallObject
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
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CrystalClusterSmallObject;
import necesse.level.maps.Level;

public class SpinelClusterSmallObject
extends CrystalClusterSmallObject {
    public SpinelClusterSmallObject(String textureName, Color mapColor, float glowHue) {
        super(textureName, mapColor, glowHue, "spinel", 0, 0, 0, new String[0]);
    }

    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (new GameRandom().seeded(SpinelClusterSmallObject.getTileSeed((int)x, (int)y)).getChance(0.25f)) {
            if (level.isServer()) {
                level.entityManager.addMob(MobRegistry.getMob((String)"spinelcaveling", (Level)level), (float)(x * 32 + 16), (float)(y * 32 + 16));
            }
            if (level.isClient()) {
                level.entityManager.addParticle((ParticleOption)new SmokePuffParticle(level, (float)(x * 32 + 16), (float)(y * 32 + 32), AphColors.spinel), Particle.GType.CRITICAL);
            }
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }

    public ArrayList<InventoryItem> getObjectDroppedItems(Level level, int layerID, int tileX, int tileY, String purpose) {
        return new ArrayList<InventoryItem>();
    }
}

