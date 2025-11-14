/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class ToolItem
extends Item
implements Enchantable<ItemEnchantment>,
UpgradableItem,
SalvageableItem,
ItemAttackerWeaponItem {
    public static final float initialReleaseToolItemUpgradeMultiplier = 1.166667f;
    protected DamageType damageType = DamageTypeRegistry.NORMAL;
    protected FloatUpgradeValue attackDamage = new FloatUpgradeValue(0.0f, 0.2f);
    protected FloatUpgradeValue resilienceGain = new FloatUpgradeValue(1.0f, 0.0f);
    protected IntUpgradeValue attackRange = new IntUpgradeValue(50, 0.0f);
    protected IntUpgradeValue knockback = new IntUpgradeValue(0, 0.0f);
    protected boolean animInverted;
    protected IntUpgradeValue enchantCost = new IntUpgradeValue(0, 0.07f);
    protected float width;
    protected FloatUpgradeValue manaCost = new FloatUpgradeValue(2.5f, 0.0f);
    protected IntUpgradeValue lifeSteal = new IntUpgradeValue(0, 0.0f);
    protected IntUpgradeValue lifeCost = new IntUpgradeValue(0, 0.0f);
    @Deprecated
    protected GameDamage attackDmg;
    public boolean canBeUsedForRaids = false;
    public int minRaidTier = 0;
    public int maxRaidTier = 0;
    public float raidTicketsModifier = 1.0f;
    public boolean useForRaidsOnlyIfObtained = false;
    public ArrayList<OneOfLootItems> onRegisterLootTables = new ArrayList();
    public String tierOneEssencesUpgradeRequirement = "anytier1essence";
    public String tierTwoEssencesUpgradeRequirement = "anytier2essence";
    public String tier9PlusUpgradeRequirement = "ascendedshard";

    public ToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(1);
        this.addToLootTable(lootTableCategory);
        this.enchantCost.setBaseValue(enchantCost).setUpgradedValue(1.0f, 2000);
        this.setItemCategory("equipment");
        this.setItemCategory(ItemCategory.craftingManager, "equipment");
        this.keyWords.add("toolitem");
        this.changeDir = true;
        this.width = 0.0f;
        this.attackXOffset = 4;
        this.attackYOffset = 4;
        this.worldDrawSize = 40;
        this.incinerationTimeMillis = 30000;
    }

    public ToolItem addToLootTable(OneOfLootItems ... lootTables) {
        if (this.idData.isSet()) {
            for (OneOfLootItems lootTable : lootTables) {
                if (lootTable == null) continue;
                lootTable.add(this.getDefaultToolLootItem());
            }
        } else {
            for (OneOfLootItems lootTable : lootTables) {
                if (lootTable == null) continue;
                this.onRegisterLootTables.add(lootTable);
            }
        }
        return this;
    }

    @Override
    public void onItemRegistryClosed() {
        super.onItemRegistryClosed();
        for (OneOfLootItems lootTable : this.onRegisterLootTables) {
            lootTable.add(this.getDefaultToolLootItem());
        }
        if (this.attackDmg != null && this.attackDamage.isEmpty()) {
            this.attackDamage.setBaseValue(this.attackDmg.damage);
            this.damageType = this.attackDmg.type;
        }
        this.registerRaidLoadouts();
    }

    public LootItemInterface getDefaultToolLootItem() {
        return new LootItem(this.getStringID());
    }

    @Override
    public float getRaiderTicketModifier(InventoryItem item, HashSet<String> obtainedItems) {
        if (this.useForRaidsOnlyIfObtained && (obtainedItems == null || !obtainedItems.contains(this.getStringID()))) {
            return 0.0f;
        }
        return this.raidTicketsModifier;
    }

    public void registerRaidLoadouts() {
        if (this.canBeUsedForRaids && ItemRegistry.isObtainable(this.getID())) {
            for (int tier = this.minRaidTier; tier <= this.maxRaidTier; ++tier) {
                SettlementRaidLoadout.Weapon weapon = new SettlementRaidLoadout.Weapon(this.getStringID());
                if (tier > 0) {
                    weapon.setTier(tier);
                }
                SettlementRaidLoadoutGenerator.ALL_WEAPONS.add(weapon);
            }
        }
    }

    @Override
    public final ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(this.getPreEnchantmentTooltips(item, perspective, blackboard));
        tooltips.add(this.getEnchantmentTooltips(item));
        tooltips.add(this.getPostEnchantmentTooltips(item, perspective, blackboard));
        return tooltips;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        ItemAttackerMob equippedMob = blackboard.get(ItemAttackerMob.class, "equippedMob", perspective);
        if (equippedMob == null) {
            equippedMob = blackboard.get(ItemAttackerMob.class, "perspective", perspective);
        }
        if (equippedMob == null) {
            equippedMob = perspective;
        }
        this.addStatTooltips(tooltips, item, blackboard.get(InventoryItem.class, "compareItem"), blackboard.getBoolean("showDifference"), blackboard.getBoolean("forceAdd"), equippedMob);
        return tooltips;
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        return new ListGameTooltips();
    }

    public final void addStatTooltips(ListGameTooltips tooltips, InventoryItem currentItem, InventoryItem lastItem, boolean showDifference, boolean forceAdd, ItemAttackerMob perspective) {
        ItemStatTipList list = new ItemStatTipList();
        this.addStatTooltips(list, currentItem, lastItem, perspective, forceAdd);
        for (ItemStatTip itemStatTip : list) {
            tooltips.add(itemStatTip.toTooltip(GameColor.GREEN.color.get(), GameColor.RED.color.get(), GameColor.YELLOW.color.get(), showDifference));
        }
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
    }

    @Override
    public int getWorldDrawSize(InventoryItem item, PlayerMob perspective) {
        GameSprite sprite = this.getWorldItemSprite(item, perspective);
        return Math.min(this.worldDrawSize, Math.max(sprite.width, sprite.height));
    }

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        GameSprite attackSprite = this.getAttackSprite(item, perspective);
        if (attackSprite == null || Math.max(attackSprite.width, attackSprite.height) < 32) {
            return this.getItemSprite(item, perspective);
        }
        return attackSprite;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (animAttack == 0) {
            int animTime = this.getAttackAnimTime(item, attackerMob);
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            this.playAttackSound(attackerMob);
        }
    }

    protected void playAttackSound(Mob source) {
        SoundManager.playSound(this.getSwingSound(), source);
        SoundManager.playSound(this.getAttackSound(), source);
    }

    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.swing2);
    }

    protected SoundSettings getAttackSound() {
        return null;
    }

    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<Shape>();
        int attackRange = this.getAttackRange(item);
        Point2D.Float dir = GameMath.normalize(aimX, aimY);
        Line2D.Float attackLine = new Line2D.Float(mob.x, mob.y, dir.x * (float)attackRange + mob.x, dir.y * (float)attackRange + mob.y);
        if (this.width > 0.0f) {
            out.add(new LineHitbox(attackLine, this.width));
        } else {
            out.add(attackLine);
        }
        return out;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return true;
    }

    public boolean getAnimInverted(InventoryItem item) {
        return this.animInverted;
    }

    @Override
    public float getAttackMovementMod(InventoryItem item) {
        return 0.5f;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (this.getAnimInverted(item)) {
            drawOptions.swingRotationInv(attackProgress);
        } else {
            drawOptions.swingRotation(attackProgress);
        }
    }

    public DamageType getDamageType(InventoryItem item) {
        return this.damageType;
    }

    public GameDamage getFlatAttackDamage(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("damage")) {
            GNDItem gndItem = gndData.getItem("damage");
            if (gndItem instanceof GNDItemGameDamage) {
                return ((GNDItemGameDamage)gndItem).damage;
            }
            if (gndItem instanceof GNDItem.GNDPrimitive) {
                float damage = ((GNDItem.GNDPrimitive)gndItem).getFloat();
                return new GameDamage(this.getDamageType(item), damage);
            }
        }
        return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)).floatValue());
    }

    public GameDamage getAttackDamage(InventoryItem item) {
        float finalDamageModifier;
        GameDamage damage = this.getFlatAttackDamage(item).enchantedDamage(this.getEnchantment(item), ToolItemModifiers.DAMAGE, ToolItemModifiers.ARMOR_PEN, ToolItemModifiers.CRIT_CHANCE);
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("finalDamageMod")) {
            finalDamageModifier = Math.max(0.0f, gndData.getFloat("finalDamageMod"));
            damage = damage.modFinalMultiplier(finalDamageModifier);
        }
        if (gndData.hasKey("damageMod")) {
            finalDamageModifier = Math.max(0.0f, gndData.getFloat("damageMod"));
            damage = damage.modDamage(finalDamageModifier);
        }
        return damage;
    }

    @Deprecated
    public GameDamage getDamage(InventoryItem item) {
        return this.getAttackDamage(item);
    }

    public void addAttackDamageTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Attacker attacker, boolean forceAdd) {
        int lastDamage;
        int damage = this.getAttackDamageValue(currentItem, attacker);
        int n = lastDamage = lastItem == null ? -1 : this.getAttackDamageValue(lastItem, attacker);
        if (damage > 0 || lastDamage > 0 || forceAdd) {
            DoubleItemStatTip tip = this.getDamageType(currentItem).getDamageTip(damage);
            if (lastItem != null) {
                tip.setCompareValue(lastDamage);
            }
            list.add(100, tip);
        }
    }

    public int getAttackDamageValue(InventoryItem item, Attacker attacker) {
        return Math.round(this.getAttackDamage(item).getBuffedDamage(attacker));
    }

    public void addAttackSpeedTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob attackerMob) {
        int currentMaxSpeed = Math.max(this.getAttackAnimTime(currentItem, attackerMob), this.getAttackCooldownTime(currentItem, attackerMob));
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "attackspeedtip", "value", this.toAttacksPerSecond(currentMaxSpeed), 1);
        if (lastItem != null) {
            int lastMaxSpeed = Math.max(this.getAttackAnimTime(lastItem, attackerMob), this.getAttackCooldownTime(lastItem, attackerMob));
            tip.setCompareValue(this.toAttacksPerSecond(lastMaxSpeed));
        }
        list.add(200, tip);
    }

    public void addResilienceGainTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob mob, boolean forceAdd) {
        if (mob != null && mob.getMaxResilience() > 0 || forceAdd) {
            float modifier = mob == null ? ((Float)BuffModifiers.RESILIENCE_GAIN.defaultBuffManagerValue).floatValue() : mob.buffManager.getModifier(BuffModifiers.RESILIENCE_GAIN).floatValue();
            float currentResilienceGain = this.getResilienceGain(currentItem) * modifier;
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "resiliencegaintip", "value", currentResilienceGain, 1);
            if (lastItem != null) {
                tip.setCompareValue(this.getResilienceGain(lastItem) * modifier);
            }
            list.add(300, tip);
        }
    }

    public float getResilienceGain(InventoryItem item) {
        return this.resilienceGain.getValue(this.getUpgradeLevel(item)).floatValue() * this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.RESILIENCE_GAIN, (Float)ToolItemModifiers.RESILIENCE_GAIN.defaultBuffManagerValue).floatValue();
    }

    public void addCritChanceTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Attacker attacker, boolean forceAdd) {
        float lastCritChance;
        float currentCritChance = this.getCritChance(currentItem, attacker);
        float f = lastCritChance = lastItem == null ? 0.0f : this.getCritChance(currentItem, attacker);
        if (currentCritChance > 0.0f || lastCritChance > 0.0f || forceAdd) {
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "crittip", "value", currentCritChance * 100.0f, 0);
            if (lastItem != null) {
                tip.setCompareValue(lastCritChance * 100.0f);
            }
            list.add(400, tip);
        }
    }

    public float getCritChance(InventoryItem item, Attacker attacker) {
        return GameMath.limit(this.getAttackDamage(item).getBuffedCritChance(attacker), 0.0f, 1.0f);
    }

    public void addAttackRangeTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem) {
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "knockbacktip", "value", this.getAttackRange(currentItem), 0);
        if (lastItem != null) {
            tip.setCompareValue(this.getAttackRange(lastItem));
        }
        list.add(500, tip);
    }

    public void addKnockbackTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Attacker attacker) {
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "knockbacktip", "value", this.getKnockback(currentItem, attacker), 0);
        if (lastItem != null) {
            tip.setCompareValue(this.getKnockback(lastItem, attacker));
        }
        list.add(600, tip);
    }

    public int getFlatKnockback(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("knockback") ? gndData.getInt("knockback") : this.knockback.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getKnockback(InventoryItem item, Attacker attacker) {
        Mob attackOwner;
        int knockback = this.getFlatKnockback(item);
        float buffMod = 1.0f;
        Mob mob = attackOwner = attacker != null ? attacker.getAttackOwner() : null;
        if (attackOwner != null) {
            buffMod = attackOwner.buffManager.getModifier(BuffModifiers.KNOCKBACK_OUT).floatValue();
        }
        return Math.round((float)knockback * this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.KNOCKBACK, (Float)ToolItemModifiers.KNOCKBACK.defaultBuffManagerValue).floatValue() * buffMod);
    }

    public void addManaCostTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob mob) {
        float modifier = mob == null ? ((Float)BuffModifiers.MANA_USAGE.defaultBuffManagerValue).floatValue() : mob.buffManager.getModifier(BuffModifiers.MANA_USAGE).floatValue();
        float currentManaCost = this.getManaCost(currentItem) * modifier;
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "manacosttip", "value", currentManaCost, 1);
        if (lastItem != null) {
            tip.setCompareValue(this.getManaCost(lastItem) * modifier, false);
        }
        list.add(1000, tip);
    }

    public float getFlatManaCost(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("manaCost") ? gndData.getFloat("manaCost") : this.manaCost.getValue(this.getUpgradeTier(item)).floatValue();
    }

    public float getManaCost(InventoryItem item) {
        return this.getFlatManaCost(item) * this.getManaUsageModifier(item);
    }

    public float getManaUsageModifier(InventoryItem item) {
        return this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.MANA_USAGE, (Float)ToolItemModifiers.MANA_USAGE.defaultBuffManagerValue).floatValue();
    }

    public void consumeMana(ItemAttackerMob attackerMob, InventoryItem item) {
        this.consumeMana(this.getManaCost(item), attackerMob);
    }

    public void consumeMana(float usedMana, ItemAttackerMob attackerMob) {
        if (usedMana > 0.0f) {
            attackerMob.useMana(usedMana, attackerMob.isPlayer && ((PlayerMob)attackerMob).isServerClient() ? ((PlayerMob)attackerMob).getServerClient() : null);
        }
    }

    public int getFlatLifeCost(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("lifeCost") ? gndData.getInt("lifeCost") : this.lifeCost.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getLifeSteal(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("lifeSteal") ? gndData.getInt("lifeSteal") : this.lifeSteal.getValue(this.getUpgradeTier(item)).intValue();
    }

    public void addLifeCostTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob mob) {
        float currentLifeCost = this.getFlatLifeCost(currentItem);
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "lifecosttip", "value", currentLifeCost, 1);
        if (lastItem != null) {
            tip.setCompareValue(this.getFlatLifeCost(lastItem), false);
        }
        list.add(1100, tip);
    }

    public void consumeLife(ItemAttackerMob attackerMob, InventoryItem item) {
        this.consumeLife(this.getFlatLifeCost(item), attackerMob, item);
    }

    public void consumeLife(int usedLife, ItemAttackerMob attackerMob, InventoryItem item) {
        this.consumeLife(usedLife, attackerMob, this.getLocalization(item));
    }

    public void consumeLife(int usedLife, ItemAttackerMob attackerMob, GameMessage attackerName) {
        if (usedLife > 0) {
            attackerMob.useLife(usedLife, attackerMob.isPlayer && ((PlayerMob)attackerMob).isServerClient() ? ((PlayerMob)attackerMob).getServerClient() : null, attackerName);
        }
    }

    public int getFlatAttackRange(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("attackRange") ? gndData.getInt("attackRange") : this.attackRange.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getAttackRange(InventoryItem item) {
        int attackRange = this.getFlatAttackRange(item);
        return Math.round((float)attackRange * this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.RANGE, (Float)ToolItemModifiers.RANGE.defaultBuffManagerValue).floatValue());
    }

    @Override
    public float getAttackSpeedModifier(InventoryItem item, ItemAttackerMob attackerMob) {
        DamageType damageType = this.getDamageType(item);
        float damageTypeModifier = damageType.calculateTotalAttackSpeedModifier(attackerMob, this.getEnchantment(item).applyModifierUnlimited(ToolItemModifiers.ATTACK_SPEED, (Float)ToolItemModifiers.ATTACK_SPEED.defaultBuffValue).floatValue());
        return super.getAttackSpeedModifier(item, attackerMob) * damageTypeModifier;
    }

    @Override
    public boolean matchesSearch(InventoryItem item, PlayerMob perspective, String search, GameBlackboard tooltipBlackboard) {
        if (super.matchesSearch(item, perspective, search, tooltipBlackboard)) {
            return true;
        }
        return this.getDamageType(item).getStringID().toLowerCase().contains(search);
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (!super.canCombineItem(level, player, me, them, purpose)) {
            return false;
        }
        return this.isSameGNDData(level, me, them, purpose);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return me.getGndData().sameKeys(them.getGndData(), "enchantment", "upgradeLevel");
    }

    public boolean canHitMob(Mob mob, ToolItemMobAbilityEvent event) {
        return mob.canBeHit(event);
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        if (event.totalHits == 0 && target.canGiveResilience(attacker)) {
            attacker.addResilience(this.getResilienceGain(item));
        }
        target.isServerHit(this.getAttackDamage(item), target.x - attacker.x, target.y - attacker.y, this.getKnockback(item, attacker), this.getToolItemEventAttacker(item, event, level, target, attacker));
    }

    public Attacker getToolItemEventAttacker(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        return attacker;
    }

    public boolean canHitObject(LevelObject levelObject) {
        return levelObject.object.attackThrough;
    }

    public void hitObject(InventoryItem item, LevelObject levelObject, Mob mob) {
        levelObject.attackThrough(this.getAttackDamage(item), mob);
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        return super.getSinkingRate(entity, currentSinking) / 5.0f;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return true;
    }

    @Override
    public void setEnchantment(InventoryItem item, int enchantment) {
        item.getGndData().setItem("enchantment", (GNDItem)new GNDItemEnchantment(enchantment));
    }

    @Override
    public int getEnchantmentID(InventoryItem item) {
        GNDItem enchantment = item.getGndData().getItem("enchantment");
        GNDItemEnchantment enchantmentItem = GNDItemEnchantment.convertEnchantmentID(enchantment);
        item.getGndData().setItem("enchantment", (GNDItem)enchantmentItem);
        return enchantmentItem.getRegistryID();
    }

    @Override
    public void clearEnchantment(InventoryItem item) {
        item.getGndData().setItem("enchantment", null);
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return null;
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return false;
    }

    @Override
    public int getEnchantCost(InventoryItem item) {
        return this.enchantCost.getValue(item.item.getUpgradeTier(item));
    }

    @Override
    public ToolItemEnchantment getEnchantment(InventoryItem item) {
        return EnchantmentRegistry.getEnchantment(this.getEnchantmentID(item), ToolItemEnchantment.class, ToolItemEnchantment.noEnchant);
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.toolDamageEnchantments;
    }

    @Override
    public GameTooltips getEnchantmentTooltips(InventoryItem item) {
        if (this.getEnchantmentID(item) > 0) {
            ListGameTooltips tooltips = new ListGameTooltips(this.getEnchantment(item).getTooltips());
            if (GlobalData.debugActive()) {
                tooltips.addFirst("Enchantment id " + this.getEnchantmentID(item));
            }
            return tooltips;
        }
        return new StringTooltips();
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        int totalBrokerValueInceaseBasedOnTier = 0;
        float tier = item.item.getUpgradeTier(item);
        if (tier > 0.0f) {
            int upgradeShardBrokerValue = 8;
            int tierIncreaseBrokerValue = 20 * ((int)tier - 1) * upgradeShardBrokerValue;
            int tierZeroToOneBrokerValue = Math.max((int)((1.0f - this.getTier1CostPercent(item)) * 40.0f), 1) * upgradeShardBrokerValue;
            totalBrokerValueInceaseBasedOnTier = tierZeroToOneBrokerValue + tierIncreaseBrokerValue;
        }
        return super.getBrokerValue(item) * this.getEnchantment(item).getEnchantCostMod() + (float)totalBrokerValueInceaseBasedOnTier;
    }

    @Override
    protected ListGameTooltips getDisplayNameTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getDisplayNameTooltips(item, perspective, blackboard);
        int upgradeLevel = this.getUpgradeLevel(item);
        if (upgradeLevel > 0) {
            String tierString;
            int tier = upgradeLevel / 100;
            if ((float)tier == (float)upgradeLevel / 100.0f) {
                tierString = String.valueOf(tier);
            } else {
                int extra = upgradeLevel - tier * 100;
                tierString = tier + " +" + extra + "%";
            }
            tooltips.add(new StringTooltips(Localization.translate("item", "tier", "tiernumber", tierString), new Color(133, 49, 168)));
        }
        return tooltips;
    }

    @Override
    public Item.Rarity getRarity(InventoryItem item) {
        Item.Rarity rarity = super.getRarity(item);
        if (this.getUpgradeTier(item) >= 1.0f) {
            return rarity.getNext(Item.Rarity.EPIC);
        }
        return rarity;
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        GameMessage out = super.getLocalization(item);
        ToolItemEnchantment enchant = this.getEnchantment(item);
        if (enchant != null && enchant.getID() > 0) {
            out = new LocalMessage("enchantment", "format", "enchantment", enchant.getLocalization(), "item", out);
        }
        return out;
    }

    @Override
    public InventoryItem getDefaultLootItem(GameRandom random, int amount) {
        InventoryItem item = super.getDefaultLootItem(random, amount);
        if (this.isEnchantable(item) && random.getChance(0.65f)) {
            ((Enchantable)((Object)item.item)).addRandomEnchantment(item, random);
        }
        return item;
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        if (!this.attackDamage.hasMoreThanOneValue()) {
            return Localization.translate("ui", "itemnotupgradable");
        }
        if (this.getUpgradeTier(item) >= (float)IncursionData.ITEM_TIER_UPGRADE_CAP) {
            return Localization.translate("ui", "itemupgradelimit");
        }
        return null;
    }

    @Override
    public void addUpgradeStatTips(ItemStatTipList list, InventoryItem lastItem, InventoryItem upgradedItem, ItemAttackerMob perspective, ItemAttackerMob statPerspective) {
        DoubleItemStatTip tierTip = new LocalMessageDoubleItemStatTip("item", "tier", "tiernumber", this.getUpgradeTier(upgradedItem), 2).setCompareValue(this.getUpgradeTier(lastItem)).setToString(tier -> {
            int floorTier = (int)tier;
            double percentAdd = tier - (double)floorTier;
            if (percentAdd != 0.0) {
                return floorTier + " (+" + (int)(percentAdd * 100.0) + "%)";
            }
            return String.valueOf(floorTier);
        });
        list.add(Integer.MIN_VALUE, tierTip);
        this.addStatTooltips(list, upgradedItem, lastItem, perspective, true);
    }

    protected int getNextUpgradeTier(InventoryItem item) {
        int currentTier = (int)item.item.getUpgradeTier(item);
        int nextTier = currentTier + 1;
        float baseValue = this.attackDamage.getValue(0.0f).floatValue();
        float nextTierValue = this.attackDamage.getValue(nextTier).floatValue();
        if (nextTier == 1 && baseValue < nextTierValue) {
            return nextTier;
        }
        while (baseValue / nextTierValue > 1.0f - this.attackDamage.defaultLevelIncreaseMultiplier / 4.0f && nextTier < currentTier + 100) {
            nextTierValue = this.attackDamage.getValue(++nextTier).floatValue();
        }
        return nextTier;
    }

    protected float getTier1CostPercent(InventoryItem item) {
        return this.attackDamage.getValue(0.0f).floatValue() / this.attackDamage.getValue(1.0f).floatValue();
    }

    protected float getUpgradeCostPerTier(InventoryItem item) {
        return 30.0f;
    }

    @Override
    public UpgradedItem getUpgradedItem(InventoryItem item) {
        int nextTier = this.getNextUpgradeTier(item);
        InventoryItem upgradedItem = item.copy();
        upgradedItem.item.setUpgradeTier(upgradedItem, nextTier);
        float cost = nextTier <= 1 ? Math.max((1.0f - this.getTier1CostPercent(item)) * 40.0f, 1.0f) : (float)nextTier * this.getUpgradeCostPerTier(item);
        HashMap<Integer, Ingredient> additionalUpgradeCost = this.getEssenceUpgradeCost();
        if (additionalUpgradeCost.get(nextTier) != null) {
            return new UpgradedItem(item, upgradedItem, new Ingredient[]{new Ingredient("upgradeshard", Math.round(cost)), additionalUpgradeCost.get(nextTier)});
        }
        return new UpgradedItem(item, upgradedItem, new Ingredient[]{new Ingredient("upgradeshard", Math.round(cost))});
    }

    protected float getSavageRewardPerTier(InventoryItem item) {
        return item.getAmount() * 20;
    }

    @Override
    public Collection<InventoryItem> getSalvageRewards(InventoryItem item) {
        float rewardPerTier = this.getSavageRewardPerTier(item);
        float reward = 0.0f;
        for (float tier = this.getUpgradeTier(item); tier > 0.0f; tier -= 1.0f) {
            reward += tier * rewardPerTier;
        }
        return Collections.singleton(new InventoryItem("upgradeshard", Math.round(reward)));
    }

    private HashMap<Integer, Ingredient> getEssenceUpgradeCost() {
        HashMap<Integer, Ingredient> additionalUpgradeCost = new HashMap<Integer, Ingredient>();
        additionalUpgradeCost.put(4, new Ingredient(this.tierOneEssencesUpgradeRequirement, 5));
        additionalUpgradeCost.put(5, new Ingredient(this.tierOneEssencesUpgradeRequirement, 10));
        additionalUpgradeCost.put(6, new Ingredient(this.tierOneEssencesUpgradeRequirement, 15));
        additionalUpgradeCost.put(7, new Ingredient(this.tierTwoEssencesUpgradeRequirement, 10));
        additionalUpgradeCost.put(8, new Ingredient(this.tierTwoEssencesUpgradeRequirement, 20));
        additionalUpgradeCost.put(9, new Ingredient(this.tier9PlusUpgradeRequirement, 1));
        additionalUpgradeCost.put(10, new Ingredient(this.tier9PlusUpgradeRequirement, 1));
        return additionalUpgradeCost;
    }
}

