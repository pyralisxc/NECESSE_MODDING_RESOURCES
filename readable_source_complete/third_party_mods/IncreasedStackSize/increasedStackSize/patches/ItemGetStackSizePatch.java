/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.inventory.item.Item
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$OnNonDefaultValue
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package increasedStackSize.patches;

import increasedStackSize.Config;
import increasedStackSize.IncreasedStackSize;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.item.Item;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=Item.class, name="getStackSize", arguments={})
public class ItemGetStackSizePatch {
    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    static boolean onEnter() {
        return true;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This Item thisItem, @Advice.FieldValue(value="stackSize") int originalStackSize, @Advice.Return(readOnly=false) int stackSize) {
        stackSize = !Config.isBlacklisted(thisItem) ? originalStackSize * IncreasedStackSize.stackSizeMultiplier : originalStackSize;
    }
}

