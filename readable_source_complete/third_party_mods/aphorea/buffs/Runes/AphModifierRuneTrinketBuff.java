/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.level.maps.Level
 */
package aphorea.buffs.Runes;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.level.maps.Level;

public class AphModifierRuneTrinketBuff
extends TrinketBuff {
    private float effectNumberVariation = 0.0f;
    private float cooldownVariation = 0.0f;
    private float healthCost = 0.0f;

    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    public float getEffectNumberVariation() {
        return this.effectNumberVariation;
    }

    public float getCooldownVariation() {
        return this.cooldownVariation;
    }

    public float getHealthCost() {
        return this.healthCost;
    }

    public AphModifierRuneTrinketBuff setEffectNumberVariation(float effectNumberVariation) {
        this.effectNumberVariation = effectNumberVariation;
        return this;
    }

    public AphModifierRuneTrinketBuff setCooldownVariation(float cooldownVariation) {
        this.cooldownVariation = cooldownVariation;
        return this;
    }

    public AphModifierRuneTrinketBuff setHealthCost(float healthCost) {
        this.healthCost = healthCost;
        return this;
    }

    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
        this.run(player.getLevel(), player, targetX, targetY);
    }

    public void runClient(Client client, PlayerMob player, int targetX, int targetY) {
        this.run(client.getLevel(), player, targetX, targetY);
    }

    public void run(Level level, PlayerMob player, int targetX, int targetY) {
    }
}

