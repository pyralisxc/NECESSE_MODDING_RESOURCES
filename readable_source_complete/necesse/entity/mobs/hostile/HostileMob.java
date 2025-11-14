/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class HostileMob
extends AttackAnimMob {
    public static LootItemInterface randomMapDrop = new LootItemList(new ChanceLootItem(0.05f, "mapfragment"));
    public static LootItemInterface randomPrivatePortalDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItemList(0.02f, new ConditionLootItem("mysteriousportal", (r, o) -> {
        Mob self = LootTable.expectExtra(Mob.class, o, 0);
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return self != null && self.getLevel().isCave && client != null && client.playerMob.getInv().getAmount(ItemRegistry.getItem("mysteriousportal"), false, false, true, true, "have") == 0 && client.characterStats().mob_kills.getKills("evilsprotector") == 0;
    }))});

    public HostileMob(int health) {
        super(health);
        this.isHostile = true;
        this.setTeam(-2);
        this.canDespawn = true;
    }

    @Override
    public boolean shouldSave() {
        return this.shouldSave && !this.canDespawn();
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return new MobSpawnLocation(this, targetX, targetY).checkLightThreshold(client).checkMobSpawnLocation().checkMaxHostilesAround(4, 8, client).validAndApply();
    }

    @Override
    public float getOutgoingDamageModifier() {
        float modifier = super.getOutgoingDamageModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE).floatValue();
        }
        return modifier;
    }

    @Override
    public float getSpeedModifier() {
        float modifier = super.getSpeedModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_SPEED).floatValue();
        }
        return modifier;
    }

    @Override
    public float getMaxHealthModifier() {
        float modifier = super.getMaxHealthModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_MAX_HEALTH).floatValue();
        }
        return modifier;
    }
}

