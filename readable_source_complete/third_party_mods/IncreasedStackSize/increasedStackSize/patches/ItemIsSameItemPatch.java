/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GameLog
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.armorItem.ArmorItem
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$OnNonDefaultValue
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package increasedStackSize.patches;

import necesse.engine.GameLog;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=Item.class, name="isSameItem", arguments={Level.class, InventoryItem.class, InventoryItem.class, String.class})
public class ItemIsSameItemPatch {
    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    static boolean onEnter() {
        return true;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This Item thisItem, @Advice.Argument(value=1) InventoryItem me, @Advice.Argument(value=2) InventoryItem them, @Advice.Argument(value=3) String purpose, @Advice.Return(readOnly=false) boolean isSame) {
        if (thisItem != them.item) {
            isSame = false;
        } else {
            GameLog.debug.println("Purpose: " + purpose);
            isSame = true;
            if (thisItem.isEnchantable(me) && them.item.isEnchantable(them)) {
                if (ArmorItem.class.isAssignableFrom(thisItem.getClass())) {
                    isSame = ((ArmorItem)thisItem).getEnchantment(me) == ((ArmorItem)them.item).getEnchantment(them);
                } else if (ToolItem.class.isAssignableFrom(thisItem.getClass())) {
                    isSame = ((ToolItem)thisItem).getEnchantment(me) == ((ToolItem)them.item).getEnchantment(them);
                } else if (TrinketItem.class.isAssignableFrom(thisItem.getClass())) {
                    isSame = ((TrinketItem)thisItem).getEnchantment(me) == ((TrinketItem)them.item).getEnchantment(them);
                }
            }
        }
    }
}

