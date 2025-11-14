/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.SlimeGreatswordAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.IncursionGreatswordWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class SlimeGreatswordToolItem
extends GreatswordToolItem {
    public SlimeGreatswordToolItem() {
        super(1900, IncursionGreatswordWeaponsLootTable.incursionGreatswordWeapons, SlimeGreatswordToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(140.0f).setUpgradedValue(1.0f, 175.00005f);
        this.attackRange.setBaseValue(130);
        this.knockback.setBaseValue(150);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "slimegreatswordchargetip1"));
        tooltips.add(Localization.translate("itemtooltip", "slimegreatswordchargetip2"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new SlimeGreatswordAttackHandler(attackerMob, slot, item, this, seed, x, y, this.chargeLevels));
        return item;
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        int timePerFrame = 100;
        int spriteRes = this.attackTexture.getHeight();
        int sprites = this.attackTexture.getWidth() / spriteRes;
        int sprite = GameUtils.getAnim(player == null ? System.currentTimeMillis() : player.getLocalTime(), sprites, sprites * timePerFrame);
        return new GameSprite(this.attackTexture, sprite, 0, spriteRes);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound1() {
        return new SoundSettings(GameResources.slimeGreatsword1).volume(0.7f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound2() {
        return new SoundSettings(GameResources.slimeGreatsword2).volume(0.5f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound3() {
        return new SoundSettings(GameResources.slimeGreatsword3).volume(0.6f);
    }
}

