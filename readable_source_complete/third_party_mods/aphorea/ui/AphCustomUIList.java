/*
 * Decompiled with CFR 0.152.
 */
package aphorea.ui;

import aphorea.ui.AphCustomUI;
import aphorea.ui.GlacialSaberAttackUIManger;
import aphorea.ui.GunAttackUIManger;
import aphorea.ui.SaberAttackUIManger;
import java.util.HashMap;
import java.util.Map;

public abstract class AphCustomUIList {
    public static Map<String, AphCustomUI> list = new HashMap<String, AphCustomUI>();
    public static GunAttackUIManger gunAttack = new GunAttackUIManger("gunattack");
    public static SaberAttackUIManger saberAttack = new SaberAttackUIManger("saberattack");
    public static GlacialSaberAttackUIManger glacialSaberAttack = new GlacialSaberAttackUIManger("glacialsaberattack");
}

