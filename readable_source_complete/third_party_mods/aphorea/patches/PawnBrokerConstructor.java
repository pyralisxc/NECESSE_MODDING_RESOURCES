/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModConstructorPatch
 *  necesse.entity.mobs.friendly.human.humanShop.PawnBrokerHumanMob
 *  necesse.entity.mobs.friendly.human.humanShop.SellingShopItem
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.entity.mobs.friendly.human.humanShop.PawnBrokerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import net.bytebuddy.asm.Advice;

@ModConstructorPatch(target=PawnBrokerHumanMob.class, arguments={})
public class PawnBrokerConstructor {
    @Advice.OnMethodExit
    static void onExit(@Advice.This PawnBrokerHumanMob This2) {
        This2.shop.addSellingItem("pawningrune", new SellingShopItem(2, 1).setRandomPrice(1800, 2000));
    }
}

