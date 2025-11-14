/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import necesse.engine.AreaFinder;
import necesse.engine.GlobalData;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.PriorityMap;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ToolDamageItemAttackHandler;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolDamageEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.StringItemStatTip;
import necesse.inventory.item.toolItem.TileDamageOption;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.incursion.IncursionData;

public abstract class ToolDamageItem
extends ToolItem {
    protected IntUpgradeValue toolDps = new IntUpgradeValue(0, 0.2f).setUpgradedValue(1.0f, 210);
    protected ToolType toolType;
    protected FloatUpgradeValue toolTier = new FloatUpgradeValue(0.0f, 0.0f).setUpgradedValue(1.0f, 10.0f);
    protected int addedRange = 0;

    public ToolDamageItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.hungerUsage = 6.6666666E-4f;
        this.changeDir = false;
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "tools");
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    @Override
    public float getItemAttackerWeaponValueFlat(InventoryItem item) {
        return 0.0f;
    }

    protected abstract void addToolTooltips(ListGameTooltips var1);

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        this.addToolTooltips(tooltips);
        tooltips.add(super.getPreEnchantmentTooltips(item, perspective, blackboard));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addToolDPSTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addToolTierTip(list, currentItem, lastItem, perspective, forceAdd || GlobalData.debugActive());
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addAddedRangeTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    public void addToolDPSTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        int lastDps;
        int dps = this.getToolDps(currentItem, perspective);
        int n = lastDps = lastItem == null ? -1 : this.getToolDps(lastItem, perspective);
        if (dps > 0 || lastDps > 0 || forceAdd) {
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "tooldmg", "value", dps, 0);
            if (lastItem != null) {
                tip.setCompareValue(lastDps);
            }
            list.add(50, tip);
        }
        if (GlobalData.debugActive()) {
            String debugToolString = this.getToolHitDamageString(currentItem, perspective);
            StringItemStatTip debugToolTip = new StringItemStatTip(debugToolString){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new StaticMessage("Tool hit damage: " + this.getReplaceValue(betterColor, worseColor, neutralColor, showDifference));
                }
            };
            if (lastItem != null) {
                String lastDebugToolString = this.getToolHitDamageString(lastItem, perspective);
                debugToolTip.setCompareValue(lastDebugToolString, dps == lastDps ? null : Boolean.valueOf(dps > lastDps));
            }
            list.add(40, debugToolTip);
        }
    }

    public void addToolTierTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        float lastTier;
        float tier = this.getToolTier(currentItem, perspective);
        float f = lastTier = lastItem == null ? tier : this.getToolTier(lastItem, perspective);
        if (tier != lastTier || forceAdd) {
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "tooltier", "value", tier, 1);
            if (lastItem != null) {
                tip.setCompareValue(lastTier);
            }
            list.add(60, tip);
        }
    }

    public void addAddedRangeTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        int lastAddedRange;
        int addedRange = this.getAddedRange(currentItem);
        int n = lastAddedRange = lastItem == null ? 0 : this.getAddedRange(lastItem);
        if (addedRange != 0 || lastAddedRange != 0 || forceAdd) {
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "tooladdrange", "value", addedRange, 0);
            tip.setValueToString(value -> {
                if (value >= 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastItem != null) {
                tip.setCompareValue(lastAddedRange);
            }
            list.add(250, tip);
        }
    }

    public boolean isTileInRange(Level level, int tileX, int tileY, ItemAttackerMob attackerMob, Line2D attackerPositionLine, InventoryItem item) {
        Point hitPoint = new Point(tileX * 32 + 16, tileY * 32 + 16);
        Point2D playerPositionPoint = attackerPositionLine != null ? GameMath.getClosestPointOnLine(attackerPositionLine, hitPoint, false) : attackerMob.getPositionPoint();
        int placeRange = this.getMiningRange(item, attackerMob) + (attackerMob != null && attackerMob.isPlayer && attackerMob.isServer() ? 32 : 0);
        return playerPositionPoint.distance(hitPoint) <= (double)placeRange;
    }

    public boolean canSmartMineTile(Level level, int tileX, int tileY, PlayerMob player, InventoryItem item) {
        if (this.canDamageTile(level, 0, tileX, tileY, player, item)) {
            if (this.toolType == ToolType.ALL || this.toolType == ToolType.SHOVEL) {
                return true;
            }
            return level.getObject(tileX, tileY).shouldSnapSmartMining(level, tileX, tileY);
        }
        return false;
    }

    public boolean canDamageTile(Level level, int layerID, int tileX, int tileY, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    public ToolType getToolType(InventoryItem item) {
        return this.toolType;
    }

    public float getToolTier(InventoryItem item, Mob perspective) {
        return GameMath.max(this.toolTier.getValue(this.getUpgradeTier(item)).floatValue() + (float)(perspective == null ? 0 : perspective.buffManager.getModifier(BuffModifiers.TOOL_TIER)), 1.0f);
    }

    public InventoryItem runLevelDamage(Level level, int levelX, int levelY, int priorityObjectLayerID, int tileX, int tileY, PlayerMob player, Line2D playerPositionLine, InventoryItem item, int animAttack, GNDItemMap mapContent) {
        if (this.toolType != ToolType.NONE && this.isTileInRange(level, tileX, tileY, player, playerPositionLine, item) && this.canDamageTile(level, priorityObjectLayerID, tileX, tileY, player, item)) {
            int damage = this.getToolHitDamage(item, animAttack, player);
            this.runTileDamage(level, levelX, levelY, priorityObjectLayerID, tileX, tileY, player, item, damage);
        }
        return item;
    }

    protected void runTileDamage(Level level, int levelX, int levelY, int priorityObjectLayerID, int tileX, int tileY, PlayerMob player, InventoryItem item, int damage) {
        ServerClient client = player != null && player.isServerClient() ? player.getServerClient() : null;
        level.entityManager.doToolDamage(priorityObjectLayerID, tileX, tileY, damage, this.toolType, this.getToolTier(item, player), new ToolDamageItemAttacker(player, item), client, true, levelX, levelY);
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        if (attackerMob.isPlayer) {
            Mob mobHit = this.getMobHitOption(level, x, y, (PlayerMob)attackerMob, null, item);
            if (mobHit != null) {
                this.setupAttackMapContentHitMob(map, level, mobHit);
            } else {
                TileDamageOption tileDamageOption = this.getTileDamageOption(level, x, y, (PlayerMob)attackerMob, null, item, true);
                if (tileDamageOption == null) {
                    int tileX = GameMath.getTileCoordinate(x);
                    int tileY = GameMath.getTileCoordinate(y);
                    tileDamageOption = new TileDamageOption(-1, tileX, tileY);
                }
                this.setupAttackMapContentHitTile(map, level, tileDamageOption);
            }
        }
    }

    public void setupAttackMapContentHitTile(GNDItemMap map, Level level, TileDamageOption tileDamageOption) {
        map.setBoolean("hasTileDamageOption", true);
        tileDamageOption.writeGNDMap(null, map);
        level.setupTileAndObjectsHashGNDMap(map, tileDamageOption.tileX, tileDamageOption.tileY, false);
    }

    public TileDamageOption getTileDamageOptionFromMap(GNDItemMap map, Level level) {
        if (map.getBoolean("hasTileDamageOption")) {
            return new TileDamageOption(null, map);
        }
        return null;
    }

    public void setupAttackMapContentHitMob(GNDItemMap map, Level level, Mob mob) {
        map.setInt("hitMobUniqueID", mob.getUniqueID());
    }

    public Mob getMobHitFromMap(GNDItemMap map, Level level) {
        int hitMobUniqueID = map.getInt("hitMobUniqueID", -1);
        if (hitMobUniqueID != -1) {
            return level.entityManager.mobs.get(hitMobUniqueID, false);
        }
        return null;
    }

    public TileDamageOption getTileDamageOption(Level level, int levelX, int levelY, PlayerMob player, Line2D playerPositionLine, InventoryItem item, boolean ignoreTileDamage) {
        int tileY;
        LevelObject hitObject;
        if ((this.toolType == ToolType.ALL || this.toolType == ToolType.PICKAXE || this.toolType == ToolType.AXE) && (hitObject = GameUtils.getInteractObjectHit(level, levelX, levelY, -1, lo -> {
            if (!this.isTileInRange(level, lo.tileX, lo.tileY, player, playerPositionLine, item)) {
                return false;
            }
            return this.canDamageTile(level, lo.layerID, lo.tileX, lo.tileY, player, item);
        }, null)) != null) {
            if (ignoreTileDamage) {
                return new TileDamageOption(hitObject.layerID, hitObject.tileX, hitObject.tileY);
            }
            DamagedObjectEntity damagedObjectEntity = level.entityManager.getDamagedObjectEntity(hitObject.tileX, hitObject.tileY);
            if (damagedObjectEntity == null || damagedObjectEntity.objectDamage[hitObject.layerID] < hitObject.object.objectHealth) {
                return new TileDamageOption(hitObject.layerID, hitObject.tileX, hitObject.tileY);
            }
            return null;
        }
        int tileX = GameMath.getTileCoordinate(levelX);
        if (this.canDamageTile(level, -1, tileX, tileY = GameMath.getTileCoordinate(levelY), player, item)) {
            if (this.toolType == ToolType.ALL || this.toolType == ToolType.SHOVEL) {
                GameTile tile = level.getTile(tileX, tileY);
                if (tile.canBeMined) {
                    if (ignoreTileDamage) {
                        return new TileDamageOption(0, tileX, tileY);
                    }
                    DamagedObjectEntity damagedObjectEntity = level.entityManager.getDamagedObjectEntity(tileX, tileY);
                    if (damagedObjectEntity == null || damagedObjectEntity.tileDamage < tile.tileHealth) {
                        return new TileDamageOption(0, tileX, tileY);
                    }
                }
            } else {
                for (int layerID = 0; layerID < ObjectLayerRegistry.getTotalLayers(); ++layerID) {
                    GameObject object = level.getObject(layerID, tileX, tileY);
                    if (ignoreTileDamage) {
                        if (object.getID() == 0 || !this.toolType.canDealDamageTo(object.toolType)) continue;
                        return new TileDamageOption(layerID, tileX, tileY);
                    }
                    DamagedObjectEntity damagedObjectEntity = level.entityManager.getDamagedObjectEntity(tileX, tileY);
                    if (object.getID() == 0 || !this.toolType.canDealDamageTo(object.toolType) || damagedObjectEntity != null && damagedObjectEntity.objectDamage[layerID] >= object.objectHealth) continue;
                    return new TileDamageOption(layerID, tileX, tileY);
                }
            }
        }
        return null;
    }

    public Mob getMobHitOption(Level level, int levelX, int levelY, PlayerMob player, Line2D playerPositionLine, InventoryItem item) {
        return null;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            if (animAttack == 0) {
                attackerMob.startAttackHandler(new ToolDamageItemAttackHandler((PlayerMob)attackerMob, slot, x, y, seed, this, mapContent));
            }
        } else {
            item = this.runTileAttack(level, x, y, attackerMob, null, item, animAttack, mapContent);
        }
        item = this.startToolItemEventAbilityEvent(level, x, y, attackerMob, attackHeight, item, seed);
        return item;
    }

    public InventoryItem runTileAttack(Level level, int x, int y, ItemAttackerMob attackerMob, Line2D attackerPositionLine, InventoryItem item, int animAttack, GNDItemMap mapContent) {
        if (attackerMob != null && attackerMob.isPlayer) {
            Mob mobHit = this.getMobHitFromMap(mapContent, level);
            if (mobHit != null) {
                return this.runMobToolDamageHit(level, mobHit, attackerMob, attackerPositionLine, item, animAttack, mapContent);
            }
            TileDamageOption tileDamageOption = this.getTileDamageOptionFromMap(mapContent, level);
            if (tileDamageOption == null) {
                return item;
            }
            int tileX = tileDamageOption.tileX;
            int tileY = tileDamageOption.tileY;
            if (tileDamageOption.layerID > 0) {
                List<ObjectHoverHitbox> hoverBoxes = level.getObject(tileDamageOption.layerID, tileX, tileY).getHoverHitboxes(level, tileDamageOption.layerID, tileX, tileY);
                Rectangle hoverBoxUnion = null;
                for (ObjectHoverHitbox hoverBox : hoverBoxes) {
                    if (hoverBoxUnion == null) {
                        hoverBoxUnion = hoverBox;
                        continue;
                    }
                    hoverBoxUnion = hoverBoxUnion.union(hoverBox);
                }
                if (hoverBoxUnion != null) {
                    x = GameMath.limit(x, hoverBoxUnion.x, hoverBoxUnion.x + hoverBoxUnion.width - 1);
                    y = GameMath.limit(y, hoverBoxUnion.y, hoverBoxUnion.y + hoverBoxUnion.height - 1);
                }
            } else {
                x = GameMath.limit(x, tileX * 32, tileX * 32 + 31);
                y = GameMath.limit(y, tileY * 32, tileY * 32 + 31);
            }
            PlayerMob player = (PlayerMob)attackerMob;
            if (player.isServerClient()) {
                ServerClient client = player.getServerClient();
                level.checkTileAndObjectsHashGNDMap(client, mapContent, tileX, tileY, false);
            }
            item = this.runLevelDamage(level, x, y, tileDamageOption.layerID, tileX, tileY, player, attackerPositionLine, item, animAttack, mapContent);
        }
        return item;
    }

    public InventoryItem runMobToolDamageHit(Level level, Mob targetMob, ItemAttackerMob attackerMob, Line2D attackerPositionLine, InventoryItem item, int animAttack, GNDItemMap mapContent) {
        return item;
    }

    public InventoryItem startToolItemEventAbilityEvent(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int seed) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime);
        attackerMob.addAndSendAttackerLevelEvent(event);
        return item;
    }

    public int getToolHitDamage(InventoryItem item, int hitNum, ItemAttackerMob perspective) {
        hitNum = Math.max(0, hitNum);
        float perHit = this.getToolDamagePerHit(item, perspective);
        return (int)((double)(perHit + perHit * (float)hitNum) - Math.floor(perHit * (float)hitNum));
    }

    protected int[] getToolHitDamage(InventoryItem item, ItemAttackerMob perspective) {
        int[] out = new int[this.getAnimAttacks(item)];
        for (int i = 0; i < out.length; ++i) {
            out[i] = this.getToolHitDamage(item, i, perspective);
        }
        return out;
    }

    protected String getToolHitDamageString(InventoryItem item, ItemAttackerMob perspective) {
        return this.getToolDamagePerHit(item, perspective) + ", " + Arrays.toString(this.getToolHitDamage(item, perspective));
    }

    public int getFlatToolDps(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("toolDps") ? gndData.getInt("toolDps") : this.toolDps.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getToolDps(InventoryItem item, Mob persepctive) {
        int toolDps = this.getFlatToolDps(item);
        int buffModifierFlat = persepctive == null ? 0 : persepctive.buffManager.getModifier(BuffModifiers.TOOL_DAMAGE_FLAT);
        float buffModifier = persepctive == null ? 1.0f : persepctive.buffManager.getModifier(BuffModifiers.TOOL_DAMAGE).floatValue();
        return Math.round((float)(toolDps + buffModifierFlat) * buffModifier * this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.TOOL_DAMAGE, (Float)ToolItemModifiers.TOOL_DAMAGE.defaultBuffManagerValue).floatValue());
    }

    public float getToolDamagePerHit(InventoryItem item, ItemAttackerMob perspective) {
        return (float)this.getToolDps(item, perspective) * ((float)super.getAttackAnimTime(item, perspective) * this.getMiningSpeedModifier(item, perspective) / 1000.0f) / (float)this.getAnimAttacks(item);
    }

    public int getAttackHandlerDamageCooldown(InventoryItem item, ItemAttackerMob attackerMob) {
        return super.getAttackAnimTime(item, attackerMob) / this.getAnimAttacks(item);
    }

    public float getMiningSpeedModifier(InventoryItem item, Mob mob) {
        return (mob == null ? 1.0f : mob.buffManager.getModifier(BuffModifiers.MINING_SPEED).floatValue()) * this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.MINING_SPEED, (Float)ToolItemModifiers.MINING_SPEED.defaultBuffManagerValue).floatValue();
    }

    @Override
    public float getAttackSpeedModifier(InventoryItem item, ItemAttackerMob attackerMob) {
        if (attackerMob != null && attackerMob.isPlayer && ((PlayerMob)attackerMob).hasGodMode()) {
            return 1.0f;
        }
        return super.getAttackSpeedModifier(item, attackerMob) * this.getMiningSpeedModifier(item, attackerMob);
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.max(super.getAttackAnimTime(item, attackerMob), 50);
    }

    public int getAddedRange(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("addedRange") ? gndData.getInt("addedRange") : this.addedRange;
    }

    public int getMiningRange(InventoryItem item, ItemAttackerMob attackerMob) {
        return (int)((3.5f + (float)this.getAddedRange(item) + (attackerMob == null ? 0.0f : attackerMob.buffManager.getModifier(BuffModifiers.MINING_RANGE).floatValue())) * 32.0f);
    }

    @Override
    public ToolDamageEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, EnchantmentRegistry.toolDamageEnchantments, this.getEnchantmentID(item), ToolDamageEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.toolDamageEnchantments.contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.toolDamageEnchantments;
    }

    @Override
    public ToolDamageEnchantment getEnchantment(InventoryItem item) {
        return EnchantmentRegistry.getEnchantment(this.getEnchantmentID(item), ToolDamageEnchantment.class, ToolDamageEnchantment.noEnchant);
    }

    public ToolType getToolType() {
        return this.toolType;
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        if (!this.toolDps.hasMoreThanOneValue()) {
            return Localization.translate("ui", "itemnotupgradable");
        }
        if (this.getUpgradeTier(item) >= (float)IncursionData.ITEM_TIER_UPGRADE_CAP) {
            return Localization.translate("ui", "itemupgradelimit");
        }
        return null;
    }

    @Override
    protected int getNextUpgradeTier(InventoryItem item) {
        int currentTier = (int)item.item.getUpgradeTier(item);
        int nextTier = currentTier + 1;
        int baseValue = this.toolDps.getValue(0.0f);
        float nextTierValue = this.toolDps.getValue(nextTier).intValue();
        if (nextTier == 1 && (float)baseValue < nextTierValue) {
            return nextTier;
        }
        while ((float)baseValue / nextTierValue > 1.0f - this.toolDps.defaultLevelIncreaseMultiplier / 4.0f && nextTier < currentTier + 100) {
            nextTierValue = this.toolDps.getValue(++nextTier).intValue();
        }
        return nextTier;
    }

    @Override
    protected float getTier1CostPercent(InventoryItem item) {
        return (float)this.toolDps.getValue(0.0f).intValue() / (float)this.toolDps.getValue(1.0f).intValue();
    }

    public SmartMineTarget getFirstSmartHitTile(final Level level, final PlayerMob player, final InventoryItem attackItem, int mouseX, int mouseY) {
        if (attackItem.item == this) {
            boolean isController;
            boolean bl = isController = Input.lastInputIsController && !ControllerInput.isCursorVisible();
            if (this.toolType == ToolType.ALL || this.toolType == ToolType.AXE || this.toolType == ToolType.PICKAXE) {
                LevelObject hitObject;
                if (!isController && (hitObject = GameUtils.getInteractObjectHit(level, mouseX, mouseY, 0, lo -> {
                    if (lo.object.getID() == 0) {
                        return false;
                    }
                    if (!this.isTileInRange(level, lo.tileX, lo.tileY, player, null, attackItem)) {
                        return false;
                    }
                    return this.canDamageTile(level, lo.layerID, lo.tileX, lo.tileY, player, attackItem);
                }, null)) != null && this.toolType.canDealDamageTo(hitObject.object.toolType)) {
                    return new SmartMineTarget(level, hitObject.tileX, hitObject.tileY, true, hitObject.layerID);
                }
                Point2D.Float dir = GameMath.normalize((float)mouseX - player.x, (float)mouseY - player.y);
                int miningRange = this.getMiningRange(attackItem, player) + 32;
                Line2D.Float line = new Line2D.Float(player.x + dir.x * 16.0f, player.y + dir.y * 16.0f, player.x + dir.x * (float)miningRange, player.y + dir.y * (float)miningRange);
                LineHitbox lineHitbox = new LineHitbox(line, 14.0f);
                LineHitbox extendedHitbox = new LineHitbox(line, 100.0f);
                Rectangle bounds = extendedHitbox.getBounds();
                int startTileX = level.limitTileXToBounds(GameMath.getTileCoordinate(bounds.x) - 1);
                int startTileY = level.limitTileYToBounds(GameMath.getTileCoordinate(bounds.y) - 1);
                int endTileX = level.limitTileXToBounds(GameMath.getTileCoordinate(bounds.x + bounds.width) + 1);
                int endTileY = level.limitTileYToBounds(GameMath.getTileCoordinate(bounds.y + bounds.height) + 1);
                PriorityMap<Point> hits = new PriorityMap<Point>();
                for (int x = startTileX; x <= endTileX; ++x) {
                    for (int y = startTileY; y <= endTileY; ++y) {
                        if (level.getObjectID(x, y) == 0) continue;
                        GameObject object = level.getObject(x, y);
                        if (!this.toolType.canDealDamageTo(object.toolType) || !this.isTileInRange(level, x, y, player, null, attackItem) || !this.canSmartMineTile(level, x, y, player, attackItem) && (!isController || !object.shouldSnapControllerMining(level, x, y))) continue;
                        Rectangle tileRec = new Rectangle(x * 32, y * 32, 32, 32);
                        if (lineHitbox.intersects(tileRec)) {
                            hits.addIfHasNoBetter(100, new Point(x, y));
                            continue;
                        }
                        if (hits.hasBetter(0) || !extendedHitbox.intersects(tileRec)) continue;
                        hits.addIfHasNoBetter(0, new Point(x, y));
                    }
                }
                Point tile = hits.getBestObjectsList().stream().min(Comparator.comparing(p -> Float.valueOf(player.getDistance(p.x * 32 + 16, p.y * 32 + 16)))).orElse(null);
                if (tile != null) {
                    return new SmartMineTarget(level, tile, true, -1);
                }
            }
            if (this.toolType == ToolType.ALL || this.toolType == ToolType.SHOVEL) {
                int mouseTileX = GameMath.getTileCoordinate(mouseX);
                int mouseTileY = GameMath.getTileCoordinate(mouseY);
                if (!isController && level.getTile((int)mouseTileX, (int)mouseTileY).canBeMined && this.canDamageTile(level, 0, mouseTileX, mouseTileY, player, attackItem) && this.isTileInRange(level, mouseTileX, mouseTileY, player, null, attackItem)) {
                    return new SmartMineTarget(level, mouseTileX, mouseTileY, false, -1);
                }
                int miningRange = this.getMiningRange(attackItem, player);
                final int regionID = level.getRegionID(player.getTileX(), player.getTileY());
                final PriorityMap hits = new PriorityMap();
                AreaFinder areaFinder = new AreaFinder(player.getTileX(), player.getTileY(), (miningRange + 32) / 32, true){

                    @Override
                    public boolean checkPoint(int x, int y) {
                        GameTile tile = level.getTile(x, y);
                        if (tile.canBeMined && ToolDamageItem.this.isTileInRange(level, x, y, player, null, attackItem) && (ToolDamageItem.this.canSmartMineTile(level, x, y, player, attackItem) || isController) && ToolDamageItem.this.canDamageTile(level, 0, x, y, player, attackItem)) {
                            if (level.getRegionID(x, y) == regionID) {
                                if (tile.smartMinePriority) {
                                    hits.addIfHasNoBetter(1000, new Point(x, y));
                                } else {
                                    hits.addIfHasNoBetter(900, new Point(x, y));
                                }
                            } else if (tile.smartMinePriority) {
                                hits.addIfHasNoBetter(100, new Point(x, y));
                            } else {
                                hits.addIfHasNoBetter(0, new Point(x, y));
                            }
                        }
                        return false;
                    }
                };
                areaFinder.tickFinder(areaFinder.getMaxTicks());
                Point tile = hits.getBestObjectsList().stream().min(Comparator.comparing(p -> new Point(mouseX, mouseY).distance(p.x * 32 + 16, p.y * 32 + 16))).orElse(null);
                if (tile != null) {
                    return new SmartMineTarget(level, tile, false, -1);
                }
            }
        }
        return null;
    }

    public static class ToolDamageItemAttacker
    implements Attacker {
        private final Mob owner;
        private final InventoryItem toolItem;

        public ToolDamageItemAttacker(Mob owner, InventoryItem toolItem) {
            this.owner = owner;
            this.toolItem = toolItem;
        }

        @Override
        public GameMessage getAttackerName() {
            return this.owner.getAttackerName();
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.owner.getDeathMessages();
        }

        @Override
        public Mob getFirstAttackOwner() {
            return this.owner;
        }
    }

    public static class SmartMineTarget
    extends Point {
        public Level level;
        public boolean isObject;
        public int priorityObjectLayerID;
        public TileDamageOption tileDamageOption;

        public SmartMineTarget(Level level, int x, int y, boolean isObject, int priorityObjectLayerID) {
            super(x, y);
            this.level = level;
            this.isObject = isObject;
            this.priorityObjectLayerID = priorityObjectLayerID;
            this.tileDamageOption = new TileDamageOption(priorityObjectLayerID, this.x, this.y);
        }

        public SmartMineTarget(Level level, Point p, boolean isObject, int priorityObjectLayerID) {
            this(level, p.x, p.y, isObject, priorityObjectLayerID);
        }
    }
}

