/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.GreatswordAttackHandler
 *  necesse.entity.mobs.attackHandler.GreatswordChargeLevel
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem
 */
package aphorea.items.tools.weapons.melee.battleaxe.logic;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

public class BattleaxeAttackHandler
extends GreatswordAttackHandler {
    float speedModifier;

    public BattleaxeAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, float speedModifier, GreatswordChargeLevel ... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
        this.speedModifier = speedModifier;
    }

    public void onUpdatePacket(PacketReader reader) {
        super.onUpdatePacket(reader);
        this.speedModifier = reader.getNextFloat();
    }

    protected void setupClientUpdatePacket(PlayerMob player, PacketWriter writer) {
        super.setupClientUpdatePacket(player, writer);
        writer.putNextFloat(this.speedModifier);
    }

    public long getTimeSinceStart() {
        return (long)((float)super.getTimeSinceStart() * this.speedModifier);
    }

    public void drawWeaponParticles(InventoryItem showItem, Color color) {
        float chargePercent = showItem.getGndData().getFloat("chargePercent");
        showItem.getGndData().setBoolean("charging", true);
        float angle = this.toolItem.getSwingRotation(showItem, this.attackerMob.getDir(), chargePercent);
        int attackDir = this.attackerMob.getDir();
        int offsetX = 0;
        int offsetY = 0;
        if (attackDir == 0) {
            angle = -angle - 90.0f;
            offsetY = -8;
        } else if (attackDir == 1) {
            angle = -angle + 180.0f + 45.0f;
            offsetX = 8;
        } else if (attackDir == 2) {
            angle = -angle + 90.0f;
            offsetY = 12;
        } else {
            angle = angle + 90.0f + 45.0f;
            offsetX = -8;
        }
        float dx = GameMath.sin((float)angle);
        float dy = GameMath.cos((float)angle);
        int range = GameRandom.globalRandom.getIntBetween(0, (int)((float)this.toolItem.getAttackRange(this.item) * 0.5f));
        this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + (float)offsetX + dx * (float)range + GameRandom.globalRandom.floatGaussian() * 3.0f, this.attackerMob.y + 4.0f + GameRandom.globalRandom.floatGaussian() * 4.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx, this.attackerMob.dy).color(color).height(20.0f - dy * (float)range - (float)offsetY);
    }
}

