/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class MobBeforeHitEvent {
    public final Mob target;
    public final Attacker attacker;
    public GameDamage damage;
    public float knockbackX;
    public float knockbackY;
    public float knockbackAmount;
    public boolean showDamageTip = true;
    public boolean playHitSound = true;
    public GNDItemMap gndData = new GNDItemMap();
    private boolean prevented;

    public MobBeforeHitEvent(Mob target, Attacker attacker, GameDamage damage, float knockbackX, float knockbackY, float knockbackAmount) {
        this.target = target;
        this.damage = damage;
        this.knockbackX = knockbackX;
        this.knockbackY = knockbackY;
        this.knockbackAmount = knockbackAmount;
        this.attacker = attacker;
    }

    public void prevent() {
        this.prevented = true;
    }

    public boolean isPrevented() {
        return this.prevented;
    }
}

