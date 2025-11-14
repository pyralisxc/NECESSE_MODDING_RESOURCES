/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReapersCallSummonToolItem
extends SummonToolItem {
    public ReapersCallSummonToolItem() {
        super("playerreaperspirit", null, 1.0f, 1350, SummonWeaponsLootTable.summonWeapons);
        this.summonType = "summonedmobtemp";
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(75.0f).setUpgradedValue(1.0f, 88.66669f);
        this.knockback.setBaseValue(0);
        this.attackXOffset = 4;
        this.attackYOffset = 20;
        this.drawMaxSummons = false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "reaperscalltip1"));
        tooltips.add(Localization.translate("itemtooltip", "reaperscalltip2"));
        tooltips.add(Localization.translate("itemtooltip", "secondarysummon"));
        return tooltips;
    }

    @Override
    public int getMaxSummons(InventoryItem item, ItemAttackerMob attackerMob) {
        return 5;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.reapersCall).volume(0.15f);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
    }

    @Override
    public HumanAttackDrawOptions getAttackDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, final float attackDirX, final float attackDirY, float attackProgress, GameSprite armSprite) {
        final HumanAttackDrawOptions attackDrawOptions = super.getAttackDrawOptions(item, level, player, headItem, chestItem, feetItem, mobDir, attackDirX, attackDirY, attackProgress, armSprite);
        long angle = level.getTime() / 2L;
        final TextureDrawOptionsEnd portalDrawOptions = MobRegistry.Textures.reaperSpiritPortal.initDraw().sprite(0, 0, 32).rotate(-angle, 16, 16);
        return new HumanAttackDrawOptions(){

            @Override
            public HumanAttackDrawOptions setOffsets(int centerX, int centerY, int armPosX, int armPosY, float armRotationOffset, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
                attackDrawOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
                return this;
            }

            @Override
            public HumanAttackDrawOptions light(GameLight light) {
                portalDrawOptions.light(light);
                attackDrawOptions.light(light);
                return this;
            }

            @Override
            public DrawOptions pos(int drawX, int drawY) {
                DrawOptions options1 = attackDrawOptions.pos(drawX, drawY);
                TextureDrawOptionsEnd options2 = portalDrawOptions.pos(drawX + 16 + (int)(attackDirX * 40.0f), drawY + 24 + (int)(attackDirY * 40.0f));
                return () -> {
                    options1.draw();
                    options2.draw();
                };
            }
        };
    }

    @Override
    public Point2D.Float findSpawnLocation(ToolItemSummonedMob mob, Level level, int x, int y, int attackHeight, ItemAttackerMob attackerMob, InventoryItem item) {
        Point2D.Float attackingDir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y + (float)attackHeight);
        return new Point2D.Float(attackerMob.x + 4.0f + attackingDir.x * 40.0f + (float)attackerMob.getCurrentAttackDrawXOffset(), attackerMob.y + 4.0f + attackingDir.y * 40.0f + (float)attackerMob.getCurrentAttackDrawYOffset());
    }

    @Override
    protected void beforeSpawn(ToolItemSummonedMob mob, InventoryItem item, ItemAttackerMob attackerMob) {
        super.beforeSpawn(mob, item, attackerMob);
        Mob castedMob = (Mob)((Object)mob);
        int angle = GameRandom.globalRandom.nextInt(360);
        float speed = GameRandom.globalRandom.getIntBetween(50, 60);
        castedMob.dx = (float)Math.cos(Math.toRadians(angle)) * speed;
        castedMob.dy = (float)Math.sin(Math.toRadians(angle)) * speed;
    }
}

