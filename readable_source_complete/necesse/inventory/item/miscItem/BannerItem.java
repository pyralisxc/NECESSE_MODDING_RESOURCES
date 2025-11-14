/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.KeepDistanceAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.ItemCategory;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BannerItem
extends Item
implements ItemAttackerWeaponItem {
    public Function<Mob, Buff> buff;
    public int range;

    public BannerItem(Item.Rarity rarity, int range, Function<Mob, Buff> buff) {
        super(1);
        this.setItemCategory("equipment", "banners");
        this.setItemCategory(ItemCategory.craftingManager, "equipment");
        this.rarity = rarity;
        this.range = range;
        this.buff = buff;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public void tickHolding(InventoryItem item, PlayerMob player) {
        super.tickHolding(item, player);
        GameUtils.streamNetworkClients(player.getLevel()).filter(c -> this.shouldBuffPlayer(item, player, c.playerMob)).filter(c -> GameMath.diagonalMoveDistance(player.getX(), player.getY(), c.playerMob.getX(), c.playerMob.getY()) <= (double)this.range).forEach(c -> this.applyBuffs(c.playerMob));
        player.getLevel().entityManager.mobs.streamInRegionsInRange(player.x, player.y, this.range).filter(m -> !m.removed()).filter(m -> this.shouldBuffMob(item, player, (Mob)m)).filter(m -> GameMath.diagonalMoveDistance(player.getX(), player.getY(), m.getX(), m.getY()) <= (double)this.range).forEach(this::applyBuffs);
    }

    public DrawOptions getStandDrawOptions(Level level, int tileX, int tileY, int drawX, int drawY, GameLight light) {
        int anim = GameUtils.getAnim(level.getWorldEntity().getTime() + (long)tileX * 97L + (long)tileY * 151L, 4, 800);
        int xOffset = 0;
        int yOffset = 0;
        int holdSpriteRes = 64;
        if (this.holdTexture.getWidth() / 128 == 6) {
            xOffset = -32;
            yOffset = -32;
            holdSpriteRes = 128;
        }
        return this.holdTexture.initDraw().sprite(1 + anim, 3, holdSpriteRes).light(light).pos(drawX - 16 + xOffset, drawY - 40 + yOffset + (anim == 0 || anim == 2 ? 2 : 0));
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return "";
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        return super.getSinkingRate(entity, currentSinking) / 5.0f;
    }

    public void applyBuffs(Mob mob) {
        Buff buff = this.buff.apply(mob);
        if (buff == null) {
            return;
        }
        ActiveBuff ab = new ActiveBuff(buff, mob, 100, null);
        mob.buffManager.addBuff(ab, false);
    }

    public boolean shouldBuffPlayer(InventoryItem item, PlayerMob from, PlayerMob target) {
        return from == target || from.isSameTeam(target);
    }

    public boolean shouldBuffMob(InventoryItem item, PlayerMob player, Mob target) {
        return target.isHuman && ((HumanMob)target).isFriendlyClient(player.getNetworkClient());
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "bannertip"));
        return tooltips;
    }

    @Override
    public AINode<ItemAttackerMob> getItemAttackerWeaponChaserAI(ItemAttackerChaserAINode<? extends ItemAttackerMob> node, ItemAttackerMob mob, InventoryItem item, ItemAttackSlot slot) {
        return new KeepDistanceAINode<ItemAttackerMob>(100);
    }

    @Override
    public float getItemAttackerWeaponValueFlat(InventoryItem item) {
        return 0.0f;
    }

    @Override
    public void itemAttackerTickHolding(InventoryItem item, ItemAttackerMob mob) {
        ItemAttackerWeaponItem.super.itemAttackerTickHolding(item, mob);
        if (mob instanceof HumanMob) {
            HumanMob humanMob = (HumanMob)mob;
            GameUtils.streamNetworkClients(mob.getLevel()).filter(c -> this.settlerShouldBuffPlayer(item, humanMob, c.playerMob)).filter(c -> GameMath.diagonalMoveDistance(mob.getX(), mob.getY(), c.playerMob.getX(), c.playerMob.getY()) <= (double)this.range).forEach(c -> this.applyBuffs(c.playerMob));
            mob.getLevel().entityManager.mobs.streamInRegionsInRange(mob.x, mob.y, this.range).filter(m -> !m.removed()).filter(m -> this.settlerShouldBuffMob(item, humanMob, (Mob)m)).filter(m -> GameMath.diagonalMoveDistance(mob.getX(), mob.getY(), m.getX(), m.getY()) <= (double)this.range).forEach(this::applyBuffs);
        }
    }

    public boolean settlerShouldBuffPlayer(InventoryItem item, HumanMob from, PlayerMob target) {
        return from.isFriendlyClient(target.getNetworkClient());
    }

    public boolean settlerShouldBuffMob(InventoryItem item, HumanMob from, Mob target) {
        return from == target || target.isHuman && from.isFriendlyHuman((HumanMob)target);
    }
}

