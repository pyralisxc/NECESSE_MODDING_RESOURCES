/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class NetToolItem
extends SwordToolItem {
    public NetToolItem() {
        super(0, null);
        this.setItemCategory("equipment", "tools", "misc");
        this.setItemCategory(ItemCategory.equipmentManager, (String[])null);
        this.keyWords.remove("sword");
        this.keyWords.add("tool");
        this.damageType = DamageTypeRegistry.TRUE;
        this.attackDamage.setBaseValue(100.0f);
        this.attackAnimTime.setBaseValue(300);
        this.attackRange.setBaseValue(50);
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    @Override
    public boolean canHitMob(Mob mob, ToolItemMobAbilityEvent event) {
        return mob instanceof HoneyBeeMob || mob instanceof QueenBeeMob;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        if (attacker.isPlayer && ((PlayerMob)attacker).isServerClient() && (target.getStringID().equals("honeybee") || target.getStringID().equals("queenbee")) && JournalChallengeUtils.isPlainsBiome(level.getBiome(target.getTileX(), target.getTileY()))) {
            ServerClient serverClient = ((PlayerMob)attacker).getServerClient();
            JournalChallenge challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.CAPTURE_BEE_ID);
            if (!challenge.isCompleted(serverClient) && challenge.isJournalEntryDiscovered(serverClient)) {
                challenge.markCompleted(serverClient);
                serverClient.forceCombineNewStats();
            }
        }
        target.remove(0.0f, 0.0f, attacker, true);
    }

    @Override
    public boolean canHitObject(LevelObject levelObject) {
        return false;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "nettip"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        return Localization.translate("ui", "itemnotupgradable");
    }
}

