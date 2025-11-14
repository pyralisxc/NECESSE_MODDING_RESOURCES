/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameUtils
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.ItemStatTip
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.LocalMessageDoubleItemStatTip
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.healing;

import aphorea.items.tools.healing.AphMagicHealingToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.level.maps.Level;

public class MagicalVial
extends AphMagicHealingToolItem {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    static AphAreaList area = new AphAreaList(new AphArea(400.0f, AphColors.blood)).setOnlyVision(false);
    int particlesAreaCount = 0;
    int particleCount = 0;

    public MagicalVial() {
        super(650);
        this.rarity = Item.Rarity.COMMON;
        this.magicHealing.setBaseValue(30).setUpgradedValue(1.0f, 80);
        this.setItemCategory(new String[]{"equipment", "tools", "healing"});
        this.setItemCategory(ItemCategory.equipmentManager, new String[]{"tools", "healingtools"});
        this.attackDamage.setBaseValue(1.0f).setUpgradedValue(1.0f, 2.0f);
    }

    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 500;
    }

    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 20000;
    }

    public boolean onMouseHoverMob(InventoryItem item, GameCamera camera, PlayerMob perspective, Mob mob, boolean isDebug) {
        boolean inInDistance;
        boolean canHealMob = AphMagicHealing.canHealMob((Mob)perspective, mob);
        boolean bl = inInDistance = perspective.getPositionPoint().distance(mob.x, mob.y) <= 400.0;
        if (canHealMob && inInDistance && perspective.isClient() && !perspective.isItemOnCooldown((Item)this) && AphMagicHealing.canHealMob((Mob)perspective, mob)) {
            ++this.particleCount;
            if (this.particleCount >= 80) {
                this.particleCount = 0;
            }
            this.circleParticle(perspective, mob);
        }
        if (canHealMob && !perspective.isItemOnCooldown((Item)this) && !inInDistance) {
            if (this.particlesAreaCount >= 3) {
                this.particlesAreaCount = 0;
                area.executeClient(perspective.getLevel(), perspective.x, perspective.y, 1.0f, 0.5f, 0.0f, (int)(Math.random() * 200.0) + 400);
            } else {
                ++this.particlesAreaCount;
            }
        }
        return false;
    }

    public void circleParticle(PlayerMob perspective, Mob target) {
        float d = (float)(target.getSelectBox().height + target.getSelectBox().width) * 0.55f;
        int particles = (int)(Math.PI * (double)d / 2.0);
        for (int i = 0; i < particles; ++i) {
            float angle = (float)i / (float)particles * 240.0f + (float)(9 * this.particleCount);
            float dx = (float)Math.sin(Math.toRadians(angle)) * d;
            float dy = (float)Math.cos(Math.toRadians(angle)) * d;
            perspective.getLevel().entityManager.addParticle(target.x + dx, target.y + dy, this.particleTypeSwitcher.next()).movesFriction(0.0f, 0.0f, 0.0f).color(AphColors.blood).heightMoves(10.0f, 10.0f).lifeTime(160);
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob mob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (mob.isPlayer) {
            PlayerMob player = (PlayerMob)mob;
            Mob target = GameUtils.streamNetworkClients((Level)level).filter(c -> c.playerMob != null).map(c -> c.playerMob).filter(m -> AphMagicHealing.canHealMob((Mob)player, (Mob)m) && m.getDistance((float)x, (float)y) / 32.0f <= 2.0f).findFirst().orElse(null);
            if (target == null) {
                target = level.entityManager.mobs.getInRegionByTileRange(x / 32, y / 32, 2).stream().filter(m -> AphMagicHealing.canHealMob((Mob)player, m)).findFirst().orElse(null);
            }
            if (level.isServer()) {
                this.healMob((ItemAttackerMob)player, (Mob)(target == null ? player : target), item);
            }
            this.animInverted = target == null;
            this.onHealingToolItemUsed((Mob)player, item);
        }
        return item;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob mob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.drink, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)mob));
        }
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        AphMagicHealing.addMagicHealingTip(this, list, currentItem, lastItem, (Mob)perspective);
    }

    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"magicalvial"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"magicalvial2"));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        int healing = AphMagicHealing.getMagicHealing(perspective, null, this.magicHealing.getValue(currentItem.item.getUpgradeTier(currentItem)), this, currentItem);
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", (double)healing, 0);
        if (lastItem != null) {
            int lastHealing = AphMagicHealing.getMagicHealing(perspective, null, this.magicHealing.getValue(lastItem.item.getUpgradeTier(lastItem)), this, lastItem);
            tip.setCompareValue((double)lastHealing);
        }
        list.add(100, (ItemStatTip)tip);
    }
}

