/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSummonFocus;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.ObjectValue;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class SummonToolItem
extends ToolItem
implements ItemInteractAction {
    public boolean singleUse = false;
    public String mobStringID;
    public String summonType;
    public FollowPosition followPosition;
    public float summonSpaceTaken;
    public boolean drawMaxSummons = true;

    public SummonToolItem(String mobStringID, FollowPosition followPosition, float summonSpaceTaken, int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.mobStringID = mobStringID;
        this.summonType = "summonedmob";
        this.followPosition = followPosition;
        this.summonSpaceTaken = summonSpaceTaken;
        this.damageType = DamageTypeRegistry.SUMMON;
        this.attackAnimTime.setBaseValue(400);
        this.setItemCategory("equipment", "weapons", "summonweapons");
        if (this instanceof ItemAttackerWeaponItem) {
            this.setItemCategory(ItemCategory.equipmentManager, "weapons", "summonweapons");
        }
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "summonweapons");
        this.keyWords.add("summon");
        this.attackXOffset = 4;
        this.attackYOffset = 4;
        this.tierOneEssencesUpgradeRequirement = "cryoessence";
        this.tierTwoEssencesUpgradeRequirement = "bloodessence";
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        int maxSummons;
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "summonfocustip"));
        GameTooltips spaceTaken = this.getSpaceTakenTooltip(item, perspective);
        if (spaceTaken != null) {
            tooltips.add(spaceTaken);
        }
        if (this.drawMaxSummons && (maxSummons = this.getMaxSummons(item, perspective)) != 1) {
            tooltips.add(Localization.translate("itemtooltip", "summonslots", "count", (Object)maxSummons));
        }
        if (this.singleUse) {
            tooltips.add(Localization.translate("itemtooltip", "singleuse"));
        }
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    public GameTooltips getSpaceTakenTooltip(InventoryItem item, PlayerMob perspective) {
        float spaceTaken = this.getSummonSpaceTaken(item, perspective);
        if (spaceTaken != 1.0f) {
            return new StringTooltips(Localization.translate("itemtooltip", "summonuseslots", "count", (Object)Float.valueOf(spaceTaken)));
        }
        return null;
    }

    public int getMaxSummons(InventoryItem item, ItemAttackerMob attackerMob) {
        if (attackerMob == null) {
            return (Integer)BuffModifiers.MAX_SUMMONS.defaultBuffManagerValue;
        }
        return attackerMob.buffManager.getModifier(BuffModifiers.MAX_SUMMONS);
    }

    public float getSummonSpaceTaken(InventoryItem item, ItemAttackerMob attackerMob) {
        return this.summonSpaceTaken;
    }

    @Override
    public InventoryItem onMobInteract(Level level, Mob targetMob, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        return ItemInteractAction.super.onMobInteract(level, targetMob, attackerMob, attackHeight, item, slot, seed, mapContent);
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.summonItemEnchantments;
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, EnchantmentRegistry.summonItemEnchantments, this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.summonItemEnchantments.contains(enchantment.getID());
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        GameWindow window = WindowManager.getWindow();
        Line2D.Float line = new Line2D.Float(player.x, player.y, player.x + aimDirX * ((float)window.getSceneWidth() / 2.0f - 20.0f), player.y + aimDirY * ((float)window.getSceneHeight() / 2.0f - 20.0f));
        double distance = ((Line2D)line).getP1().distance(((Line2D)line).getP2());
        LineHitbox lineHitbox = new LineHitbox(line, 200.0f);
        Mob best = Stream.concat(level.entityManager.mobs.streamInRegionsInRange(player.getX(), player.getY(), (int)distance), level.entityManager.players.streamInRegionsInRange(player.getX(), player.getY(), (int)distance)).filter(m -> m.canBeTargeted(player, player.getNetworkClient()) && lineHitbox.intersects(m.getSelectBox())).min(Comparator.comparingDouble(player::getDistance)).orElse(null);
        if (best != null) {
            Rectangle selectBox = best.getSelectBox();
            return new Point(selectBox.x + selectBox.width / 2, selectBox.y + selectBox.height / 2);
        }
        return super.getControllerAttackLevelPos(level, aimDirX, aimDirY, player, item);
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y){

            @Override
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            @Override
            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer) {
            return item;
        }
        SummonToolItem.runSummonFocus(level, x, y, (PlayerMob)attackerMob);
        return item;
    }

    @Override
    public void showLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.9f).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 1.7f)));
        }
    }

    protected void beforeSpawn(ToolItemSummonedMob mob, InventoryItem item, ItemAttackerMob attackerMob) {
    }

    public Point2D.Float findSpawnLocation(ToolItemSummonedMob mob, Level level, int x, int y, int attackHeight, ItemAttackerMob attackerMob, InventoryItem item) {
        return SummonToolItem.findSpawnLocation((Mob)((Object)mob), level, attackerMob.x, attackerMob.y);
    }

    public static Point2D.Float findSpawnLocation(Mob mob, Level level, float centerX, float centerY) {
        ArrayList<Point2D.Float> possibleSpawns = new ArrayList<Point2D.Float>();
        for (int cX = -1; cX <= 1; ++cX) {
            for (int cY = -1; cY <= 1; ++cY) {
                float posY;
                float posX;
                if (cX == 0 && cY == 0 || mob.collidesWith(level, (int)(posX = centerX + (float)(cX * 32)), (int)(posY = centerY + (float)(cY * 32)))) continue;
                possibleSpawns.add(new Point2D.Float(posX, posY));
            }
        }
        if (possibleSpawns.size() > 0) {
            return (Point2D.Float)possibleSpawns.get(GameRandom.globalRandom.nextInt(possibleSpawns.size()));
        }
        return new Point2D.Float(centerX, centerY);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.magicbolt4).volume(0.3f);
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return 320;
    }

    @Override
    public int getItemAttackerRunAwayDistance(ItemAttackerMob attackerMob, InventoryItem item) {
        return 160;
    }

    @Override
    public float getItemAttackerWeaponValue(ItemAttackerMob mob, InventoryItem item) {
        if (mob != null && !mob.isPlayer && this.getSummonSpaceTaken(item, mob) > (float)this.getMaxSummons(item, mob)) {
            return 0.0f;
        }
        return super.getItemAttackerWeaponValue(mob, item);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        float spaceLeft;
        float spaceTaken;
        if (!attackerMob.isPlayer && attackerMob.isServer() && ((spaceTaken = this.getSummonSpaceTaken(item, attackerMob)) > (spaceLeft = (float)this.getMaxSummons(item, attackerMob) - attackerMob.serverFollowersManager.getFollowerCount(this.summonType)) || spaceTaken > (float)this.getMaxSummons(item, attackerMob))) {
            return "";
        }
        return super.canAttack(level, x, y, attackerMob, item);
    }

    public String superCanAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return super.canAttack(level, x, y, attackerMob, item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isServer()) {
            this.runServerSummon(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        }
        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    public void runServerSummon(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        ToolItemSummonedMob mob = (ToolItemSummonedMob)((Object)MobRegistry.getMob(this.mobStringID, level));
        this.summonServerMob(attackerMob, mob, x, y, attackHeight, item);
    }

    public void summonServerMob(ItemAttackerMob attackerMob, ToolItemSummonedMob mob, int x, int y, int attackHeight, InventoryItem item) {
        Mob castedMob = (Mob)((Object)mob);
        attackerMob.serverFollowersManager.addFollower(this.summonType, castedMob, this.followPosition, "summonedmob", this.getSummonSpaceTaken(item, attackerMob), p -> this.getMaxSummons(item, (ItemAttackerMob)p), null, false);
        Point2D.Float spawnPoint = this.findSpawnLocation(mob, castedMob.getLevel(), x, y, attackHeight, attackerMob, item);
        mob.updateDamage(this.getAttackDamage(item));
        mob.setEnchantment(this.getEnchantment(item));
        if (!attackerMob.isPlayer) {
            mob.setRemoveWhenNotInInventory(item.item, CheckSlotType.WEAPON);
        }
        this.beforeSpawn(mob, item, attackerMob);
        castedMob.getLevel().entityManager.addMob(castedMob, spawnPoint.x, spawnPoint.y);
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (this.drawMaxSummons && inInventory) {
            int maxSummons = this.getMaxSummons(item, perspective);
            if (maxSummons > 999) {
                maxSummons = 999;
            }
            if (maxSummons != 1) {
                String amountString = String.valueOf(maxSummons);
                int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
                FontManager.bit.drawString(x + 28 - width, y + 16, amountString, tipFontOptions);
            }
        }
    }

    public static Mob getNextSummonFocus(Level level, int x, int y, PlayerMob player) {
        if (level.isClient()) {
            Mob nextFocus = level.entityManager.streamAreaMobsAndPlayersTileRange(x, y, 10).filter(m -> m.canBeTargeted(player, player.getNetworkClient()) && m.getSelectBox().contains(x, y)).findFirst().orElse(null);
            if (nextFocus == null) {
                nextFocus = level.entityManager.streamAreaMobsAndPlayersTileRange(x, y, 10).filter(m -> m.canBeTargeted(player, player.getNetworkClient())).map(m -> {
                    Rectangle selectBox = m.getSelectBox();
                    return new ObjectValue<Mob, Double>((Mob)m, new Point2D.Double(selectBox.getCenterX(), selectBox.getCenterY()).distance(x, y));
                }).filter(m -> (Double)m.value < 75.0).findBestDistance(1, Comparator.comparingDouble(m -> (Double)m.value)).map(m -> (Mob)m.object).orElse(null);
            }
            return nextFocus;
        }
        return null;
    }

    public static void runSummonFocus(Level level, int x, int y, PlayerMob player) {
        if (level.isClient()) {
            Mob newFocus = SummonToolItem.getNextSummonFocus(level, x, y, player);
            ClientClient me = level.getClient().getClient();
            if (me != null) {
                int n = me.playerMob.summonFocusUniqueID = newFocus == null ? -1 : newFocus.getUniqueID();
                if (newFocus != null) {
                    newFocus.onFocussedBySummons(player);
                }
                level.getClient().network.sendPacket(new PacketSummonFocus(newFocus));
            }
        }
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "summonweapon");
    }
}

