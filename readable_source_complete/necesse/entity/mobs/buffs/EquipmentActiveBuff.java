/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.util.ArrayList;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.StatsBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class EquipmentActiveBuff
extends ActiveBuff {
    private ArrayList<ArmorBuffHandler> armorBuffs = new ArrayList(3);

    public EquipmentActiveBuff(StatsBuff equipmentBuff, Mob owner) {
        super((Buff)equipmentBuff, owner, 0, (Attacker)null);
    }

    public void addEquipmentEnchant(EquipmentItemEnchant enchant) {
        for (Modifier modifier : BuffModifiers.LIST) {
            this.addModifier(modifier, enchant.getModifier(modifier));
            this.addModifierLimits(modifier, enchant.getLimits(modifier));
        }
    }

    public void addArmorBuff(Mob mob, InventoryItem armorItem, InventoryItem cosmeticArmorItem) {
        this.armorBuffs.add(new ArmorBuffHandler(mob, armorItem, cosmeticArmorItem));
    }

    @Override
    public void onBeforeHit(MobBeforeHitEvent event) {
        super.onBeforeHit(event);
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.onBeforeHit(this.owner, event);
        }
    }

    @Override
    public void onBeforeAttacked(MobBeforeHitEvent event) {
        super.onBeforeAttacked(event);
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.onBeforeAttacked(this.owner, event);
        }
    }

    @Override
    public void onBeforeHitCalculated(MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(event);
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.onBeforeHitCalculated(this.owner, event);
        }
    }

    @Override
    public void onBeforeAttackedCalculated(MobBeforeHitCalculatedEvent event) {
        super.onBeforeAttackedCalculated(event);
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.onBeforeAttackedCalculated(this.owner, event);
        }
    }

    @Override
    public void onWasHit(MobWasHitEvent event) {
        super.onWasHit(event);
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.onWasHitLogic(this.owner, event);
        }
    }

    @Override
    public void onHasAttacked(MobWasHitEvent event) {
        super.onHasAttacked(event);
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.onHasAttackedLogic(this.owner, event);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.serverTick(this.owner);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.modifiers == null) continue;
            handler.modifiers.clientTick(this.owner);
        }
    }

    public void tickEffects(Mob mob) {
        for (ArmorBuffHandler handler : this.armorBuffs) {
            if (handler.effectModifier == null) continue;
            handler.effectModifier.tickEffect(mob, handler.effectModifierCosmetic);
        }
    }

    private class ArmorBuffHandler {
        public ArmorModifiers modifiers;
        public ArmorModifiers effectModifier;
        public boolean effectModifierCosmetic;

        public ArmorBuffHandler(Mob mob, InventoryItem armor, InventoryItem cosmetic) {
            ArmorModifiers armorModifiers;
            ArmorItem armorItem;
            if (armor != null && armor.item.type == Item.Type.ARMOR) {
                armorItem = (ArmorItem)armor.item;
                EquipmentActiveBuff.this.addModifier(BuffModifiers.ARMOR_FLAT, armorItem.getTotalArmorValue(armor, mob));
                armorModifiers = armorItem.getArmorModifiers(armor, mob);
                if (armorModifiers != null) {
                    for (Modifier modifier : BuffModifiers.LIST) {
                        EquipmentActiveBuff.this.addModifier(modifier, armorModifiers.getModifier(modifier));
                        EquipmentActiveBuff.this.addModifierLimits(modifier, armorModifiers.getLimits(modifier));
                    }
                    this.modifiers = armorModifiers;
                    this.effectModifier = armorModifiers;
                    this.effectModifierCosmetic = false;
                }
            }
            if (cosmetic != null && cosmetic.item.type == Item.Type.ARMOR && (armorModifiers = (armorItem = (ArmorItem)cosmetic.item).getArmorModifiers(cosmetic, mob)) != null) {
                this.effectModifier = armorModifiers;
                this.effectModifierCosmetic = true;
            }
        }
    }
}

