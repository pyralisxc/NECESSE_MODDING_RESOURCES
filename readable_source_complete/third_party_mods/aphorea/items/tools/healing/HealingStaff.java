/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.upgradeUtils.IntUpgradeValue
 *  necesse.level.maps.Level
 *  org.jetbrains.annotations.Nullable
 */
package aphorea.items.tools.healing;

import aphorea.items.AphAreaToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import org.jetbrains.annotations.Nullable;

public class HealingStaff
extends AphAreaToolItem {
    protected IntUpgradeValue magicHealing2 = new IntUpgradeValue(0, 0.2f);

    public HealingStaff() {
        super(650, false, true);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(1000);
        this.manaCost.setBaseValue(6.0f);
        this.attackXOffset = 12;
        this.attackYOffset = 22;
        this.magicHealing.setBaseValue(10).setUpgradedValue(1.0f, 20);
        this.magicHealing2.setBaseValue(6).setUpgradedValue(1.0f, 12);
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.magicbolt3, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(1.0f).pitch(1.0f));
        }
    }

    public int getHealing2(@Nullable InventoryItem item) {
        return item == null ? this.magicHealing2.getValue(0.0f) : this.magicHealing2.getValue(item.item.getUpgradeTier(item));
    }

    @Override
    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(new AphArea(120.0f, AphColors.pink_witch_dark).setHealingArea(this.getHealing(item)), new AphArea(120.0f, AphColors.pink_witch).setHealingArea(this.getHealing2(item)));
    }
}

