/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobManaChangeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WeaponChargeSmiteLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.IncursionCloseRangeWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PerfectStormSwordToolItem
extends SwordToolItem
implements ItemInteractAction {
    protected float abilityCooldown = 30.0f;

    public PerfectStormSwordToolItem() {
        super(300, IncursionCloseRangeWeaponsLootTable.incursionCloseRangeWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(350);
        this.attackDamage.setBaseValue(75.0f).setUpgradedValue(1.0f, 87.50002f);
        this.attackRange.setBaseValue(50);
        this.knockback.setBaseValue(75);
        this.canBeUsedForRaids = true;
        this.attackXOffset = 16;
        this.attackYOffset = 16;
        this.changeDir = true;
    }

    @Override
    public int getFlatAttackRange(InventoryItem item) {
        return 85;
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (perspective != null && perspective.buffManager.hasBuff(BuffRegistry.PERFECT_STORM)) {
            return new GameSprite(this.itemTexture, 1, 0, this.itemTexture.getHeight());
        }
        return new GameSprite(this.itemTexture, 0, 0, this.itemTexture.getHeight());
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return new GameSprite(this.attackTexture, 0, 0, this.attackTexture.getHeight());
    }

    public ItemAttackDrawOptions getLightningDrawOptions(InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress) {
        int spriteRes = this.attackTexture.getHeight();
        int timePerFrame = 300;
        int spriteCount = 3;
        int sprite = GameUtils.getAnim(player == null ? System.currentTimeMillis() : player.getLocalTime(), spriteCount, spriteCount * timePerFrame) + 1;
        GameSprite lightningSprite = new GameSprite(this.attackTexture, sprite, 0, spriteRes);
        ItemAttackDrawOptions options = ItemAttackDrawOptions.start(mobDir);
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(lightningSprite);
        itemSprite.itemRotatePoint(this.attackXOffset, this.attackYOffset);
        if (!this.animDrawBehindHand(item)) {
            options.itemAfterHand();
        }
        this.setDrawAttackRotation(item, options, attackDirX, attackDirY, attackProgress);
        return options;
    }

    @Override
    public HumanAttackDrawOptions getAttackDrawOptions(InventoryItem item, Level level, final PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, float attackDirX, float attackDirY, float attackProgress, GameSprite armSprite) {
        final HumanAttackDrawOptions attackDrawOptions = super.getAttackDrawOptions(item, level, player, headItem, chestItem, feetItem, mobDir, attackDirX, attackDirY, attackProgress, armSprite);
        final ItemAttackDrawOptions lightningDrawOptions = this.getLightningDrawOptions(item, player, mobDir, attackDirX, attackDirY, attackProgress);
        lightningDrawOptions.light(new GameLight(150.0f));
        return new HumanAttackDrawOptions(){

            @Override
            public HumanAttackDrawOptions setOffsets(int centerX, int centerY, int armPosX, int armPosY, float armRotationOffset, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
                attackDrawOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
                lightningDrawOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
                return this;
            }

            @Override
            public HumanAttackDrawOptions light(GameLight light) {
                attackDrawOptions.light(light);
                return this;
            }

            @Override
            public DrawOptions pos(int drawX, int drawY) {
                DrawOptions options1 = attackDrawOptions.pos(drawX, drawY);
                DrawOptions options2 = lightningDrawOptions.pos(drawX, drawY);
                return () -> {
                    options1.draw();
                    if (player != null && player2.buffManager.hasBuff(BuffRegistry.PERFECT_STORM)) {
                        options2.draw();
                    }
                };
            }
        };
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.buffManager.hasBuff(BuffRegistry.Debuffs.PERFECT_STORM_ABILITY_COOLDOWN);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        float fortyPercentMissingMana = attackerMob.getMissingMana() * 0.4f;
        if (!level.isClient() && fortyPercentMissingMana >= 1.0f) {
            MobManaChangeEvent event = new MobManaChangeEvent((Mob)attackerMob, fortyPercentMissingMana);
            level.entityManager.addLevelEvent(event);
        }
        float maxmana = attackerMob.getMaxMana();
        float percManaRestored = fortyPercentMissingMana / maxmana;
        float restorationFactor = percManaRestored / 0.4f;
        float cooldownScale = GameMath.limit(restorationFactor, 0.0f, 1.0f);
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.PERFECT_STORM, (Mob)attackerMob, 5.0f + 15.0f * cooldownScale, null), true);
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.PERFECT_STORM_ABILITY_COOLDOWN, (Mob)attackerMob, this.abilityCooldown, null), true);
        item.getGndData().setBoolean("attackIsRightClick", true);
        return ItemInteractAction.super.onLevelInteract(level, x, y, attackerMob, attackHeight, item, slot, seed, mapContent);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (item.getGndData().getBoolean("attackIsRightClick")) {
            drawOptions.rotation(-35.0f);
        } else {
            super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("attackIsRightClick")) {
            item.getGndData().setBoolean("attackIsRightClick", false);
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public boolean showAttackAllDirections(ItemAttackerMob attackerMob, InventoryItem item) {
        return !item.getGndData().getBoolean("attackIsRightClick");
    }

    @Override
    public void showLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int seed, GNDItemMap mapContent) {
        if (attackerMob.isClient()) {
            level.getClient().startCameraShake(x, (float)y, 400, 40, 3.0f, 3.0f, true);
            SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(attackerMob).volume(1.0f));
        }
        if (attackerMob.isClient()) {
            return;
        }
        int smites = 7;
        float radius = 300.0f;
        for (int i = 0; i < smites; ++i) {
            double angle = Math.PI * 2 * (double)i / (double)smites;
            float cX = attackerMob.x + (float)((double)radius * Math.cos(angle));
            float cY = attackerMob.y + (float)((double)radius * Math.sin(angle));
            WeaponChargeSmiteLevelEvent smiteEvent = new WeaponChargeSmiteLevelEvent(attackerMob, new GameRandom(attackerMob.getUniqueID()), cX, cY);
            attackerMob.getLevel().entityManager.addLevelEvent(smiteEvent);
        }
    }

    @Override
    public Attacker getToolItemEventAttacker(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        return new PerfectStormSwordAttacker(attacker);
    }

    @Override
    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.lightningHammer).pitchVariance(0.2f).volume(0.6f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "perfectstormtip"), 400);
        return tooltips;
    }

    public static class PerfectStormSwordAttacker
    implements Attacker {
        protected Mob attackerMob;

        public PerfectStormSwordAttacker(Mob attackerMob) {
            this.attackerMob = attackerMob;
        }

        @Override
        public GameMessage getAttackerName() {
            return this.attackerMob.getAttackerName();
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.attackerMob.getDeathMessages();
        }

        @Override
        public Mob getFirstAttackOwner() {
            return this.attackerMob.getFirstAttackOwner();
        }
    }
}

