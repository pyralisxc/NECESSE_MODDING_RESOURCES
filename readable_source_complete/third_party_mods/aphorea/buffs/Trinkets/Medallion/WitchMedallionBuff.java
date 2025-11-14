/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.GameDamage
 */
package aphorea.buffs.Trinkets.Medallion;

import aphorea.buffs.Trinkets.AphAreaWhenHealTrinketBuff;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;

public class WitchMedallionBuff
extends AphAreaWhenHealTrinketBuff {
    static int range = 200;
    static Color color = AphColors.dark_magic;
    public static AphAreaList areaList = new AphAreaList(new AphArea((float)range, color).setDamageArea(new GameDamage(DamageTypeRegistry.MAGIC, 15.0f, 5.0f)));

    public WitchMedallionBuff() {
        super(30, areaList);
    }
}

