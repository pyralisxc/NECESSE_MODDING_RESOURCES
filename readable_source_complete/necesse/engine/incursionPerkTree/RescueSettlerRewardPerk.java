/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class RescueSettlerRewardPerk
extends IncursionPerk {
    public static HashMap<String, IncursionMobReward> settlers = new HashMap();

    public RescueSettlerRewardPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public TicketSystemList<LootItemInterface> onGenerateTabletRewards(TicketSystemList<LootItemInterface> ticketedRewards, GameRandom seededRandom, int tier, IncursionData incursionData) {
        int tabletTierWeight = tier * 10;
        OneOfLootItems lootItemList = new OneOfLootItems(new LootItemInterface[0]);
        for (Map.Entry<String, IncursionMobReward> entry : settlers.entrySet()) {
            GNDItemMap gndData = new GNDItemMap();
            gndData.setString("settlerIncursionReward", entry.getKey());
            LootItem lootItem = new LootItem(entry.getValue().mobStringID + "spawnitem", gndData);
            lootItemList.add(lootItem);
        }
        ticketedRewards.addObject(40 + tabletTierWeight, (Object)lootItemList);
        return ticketedRewards;
    }

    static {
        settlers.put("settler", new SettlerIncursionReward("human"));
        settlers.put("farmersettler", new SettlerIncursionReward("farmerhuman"));
        settlers.put("blacksmithsettler", new SettlerIncursionReward("blacksmithhuman"));
        settlers.put("guardsettler", new SettlerIncursionReward("guardhuman"));
        settlers.put("magesettler", new SettlerIncursionReward("magehuman"));
        settlers.put("gunsmithsettler", new SettlerIncursionReward("gunsmithhuman"));
        settlers.put("alchemistsettler", new SettlerIncursionReward("alchemisthuman"));
        settlers.put("huntersettler", new SettlerIncursionReward("hunterhuman"));
        settlers.put("anglersettler", new SettlerIncursionReward("anglerhuman"));
        settlers.put("pawnbrokersettler", new SettlerIncursionReward("pawnbrokerhuman"));
        settlers.put("animalkeepersettler", new SettlerIncursionReward("animalkeeperhuman"));
        settlers.put("stylistsettler", new SettlerIncursionReward("stylisthuman"));
        settlers.put("piratesettler", new SettlerIncursionReward("piratehuman"));
        settlers.put("explorersettler", new SettlerIncursionReward("explorerhuman"));
        settlers.put("minersettler", new SettlerIncursionReward("minerhuman"));
        settlers.put("tradersettler", new SettlerIncursionReward("traderhuman"));
    }

    public static abstract class IncursionMobReward {
        public final String mobStringID;

        public IncursionMobReward(String mobStringID) {
            this.mobStringID = mobStringID;
        }

        public abstract void spawnMob(Level var1, int var2, int var3);
    }

    public static class SettlerIncursionReward
    extends IncursionMobReward {
        public SettlerIncursionReward(String mobStringID) {
            super(mobStringID);
        }

        @Override
        public void spawnMob(Level level, int levelX, int levelY) {
            Mob mob = MobRegistry.getMob(this.mobStringID, level);
            mob.onSpawned(levelX, levelY);
            if (mob instanceof HumanMob) {
                HumanMob humanMob = (HumanMob)mob;
                humanMob.setTrapped();
                ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
                ArmorSetsLootTable.armorSets.addItems(items, GameRandom.globalRandom, 1.0f, new Object[0]);
                for (InventoryItem invItem : items) {
                    if (!invItem.item.isArmorItem()) continue;
                    invItem.item.setUpgradeTier(invItem, 1.0f);
                    ArmorItem armorItem = (ArmorItem)invItem.item;
                    switch (armorItem.armorType) {
                        case HEAD: {
                            humanMob.equipmentInventory.setItem(0, invItem);
                            break;
                        }
                        case CHEST: {
                            humanMob.equipmentInventory.setItem(1, invItem);
                            break;
                        }
                        case FEET: {
                            humanMob.equipmentInventory.setItem(2, invItem);
                        }
                    }
                }
            }
            Point2D.Float dir = GameMath.getAngleDir(GameRandom.globalRandom.nextInt(360));
            float speed = GameRandom.globalRandom.getFloatBetween(100.0f, 150.0f);
            mob.dx = dir.x * speed;
            mob.dy = dir.y * speed;
            mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.SPAWN_INVINCIBILITY, mob, 4.0f, null), false);
            level.entityManager.mobs.add(mob);
        }
    }
}

