/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 *  necesse.entity.pickup.ItemPickupEntity
 *  necesse.inventory.InventoryItem
 *  necesse.level.gameObject.CrystalClusterObject
 *  necesse.level.gameObject.GameObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.multiTile.MultiTile
 *  necesse.level.maps.multiTile.StaticMultiTile
 */
package aphorea.objects;

import aphorea.objects.SpinelClusterRObject;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CrystalClusterObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class SpinelClusterObject
extends CrystalClusterObject {
    public SpinelClusterObject(String textureName, Color mapColor, float glowHue) {
        super(textureName, mapColor, glowHue, "spinel", 0, 0, 0, new String[0]);
    }

    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (level.isServer()) {
            level.entityManager.addMob(MobRegistry.getMob((String)"spinelgolem", (Level)level), (float)(x * 32 + 16), (float)(y * 32 + 16));
        }
        if (level.isClient()) {
            level.entityManager.addParticle((ParticleOption)new SmokePuffParticle(level, (float)(x * 32 + 16), (float)(y * 32 + 32), AphColors.spinel), Particle.GType.CRITICAL);
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }

    public static void registerCrystalCluster(String itemStringID, String textureName, Color mapColor, float glowHue, float brokerValue, boolean isObtainable) {
        SpinelClusterObject o1 = new SpinelClusterObject(textureName, mapColor, glowHue);
        int id1 = ObjectRegistry.registerObject((String)itemStringID, (GameObject)o1, (float)brokerValue, (boolean)isObtainable);
        SpinelClusterRObject o2 = new SpinelClusterRObject(textureName, mapColor, glowHue);
        o1.counterID = ObjectRegistry.registerObject((String)(itemStringID + "r"), (GameObject)o2, (float)0.0f, (boolean)false);
        o2.counterID = id1;
    }

    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 1, true, new int[]{this.getID(), this.counterID});
    }

    public ArrayList<InventoryItem> getObjectDroppedItems(Level level, int layerID, int tileX, int tileY, String purpose) {
        return new ArrayList<InventoryItem>();
    }
}

