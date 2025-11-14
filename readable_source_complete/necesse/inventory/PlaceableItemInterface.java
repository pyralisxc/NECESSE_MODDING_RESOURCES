/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public interface PlaceableItemInterface {
    public static int getPlaceRange(ItemAttackerMob attackerMob) {
        return (int)((3.5f + (attackerMob == null ? 0.0f : attackerMob.buffManager.getModifier(BuffModifiers.BUILD_RANGE).floatValue())) * 32.0f);
    }

    default public int getPlaceRange(InventoryItem item, ItemAttackerMob attackerMob) {
        return PlaceableItemInterface.getPlaceRange(attackerMob);
    }

    public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7);
}

