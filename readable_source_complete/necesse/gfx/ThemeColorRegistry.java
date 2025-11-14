/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.gfx.ThemeColorRange;

public class ThemeColorRegistry
extends GameRegistry<ThemeColorRangeRegistryElement> {
    public static final ThemeColorRegistry instance = new ThemeColorRegistry();
    public static ThemeColorRange TEST_COLOR;
    public static ThemeColorRange BLACKWHITE;
    public static ThemeColorRange FIRE;
    public static ThemeColorRange ICE;
    public static ThemeColorRange LIGHTNING;
    public static ThemeColorRange POISON;
    public static ThemeColorRange WEAKNESS;
    public static ThemeColorRange DAMAGE_DECREASE;
    public static ThemeColorRange ATTACKSLOW;
    public static ThemeColorRange NECROTIC;
    public static ThemeColorRange POLYMORPH;
    public static ThemeColorRange SMOKE;
    public static ThemeColorRange BLOOD;
    public static ThemeColorRange WATER;
    public static ThemeColorRange EMBERGLOW;
    public static ThemeColorRange HEAL;
    public static ThemeColorRange EXPERIENCE;
    public static ThemeColorRange SLIME;
    public static ThemeColorRange SAND;
    public static ThemeColorRange SAPPHIRE;
    public static ThemeColorRange RUBY;
    public static ThemeColorRange AMETHYST;
    public static ThemeColorRange EMERALD;
    public static ThemeColorRange TOPAZ;
    public static ThemeColorRange DEEPFROST;
    public static ThemeColorRange SOULSEED;
    public static ThemeColorRange PURPLE;
    public static ThemeColorRange RED;
    public static ThemeColorRange ORANGE;
    public static ThemeColorRange YELLOW;
    public static ThemeColorRange GREEN;
    public static ThemeColorRange BLUE;

    private ThemeColorRegistry() {
        super("ThemeColor", 32762);
    }

    public static ThemeColorRange combine(ThemeColorRange ... colorRanges) {
        ThemeColorRange finalRange = new ThemeColorRange(Color.black);
        ThemeColorRange prevRange = colorRanges[0];
        for (int i = 1; i < colorRanges.length; ++i) {
            prevRange = finalRange = new ThemeColorRange(prevRange, colorRanges[i].colors);
        }
        return finalRange;
    }

    @Override
    public void registerCore() {
        TEST_COLOR = ThemeColorRegistry.registerColorRange("testcolor", new ThemeColorRange(new Color(238, 0, 255), new Color(65, 255, 0)));
        BLACKWHITE = ThemeColorRegistry.registerColorRange("blackwhite", new ThemeColorRange(new Color(0, 0, 0), new Color(255, 255, 255), new Color(129, 129, 129)));
        PURPLE = ThemeColorRegistry.registerColorRange("purple", new ThemeColorRange(new Color(108, 105, 227), new Color(97, 73, 255), new Color(120, 82, 253), new Color(154, 95, 243), new Color(148, 71, 244)));
        RED = ThemeColorRegistry.registerColorRange("red", new ThemeColorRange(new Color(207, 100, 100), new Color(239, 48, 48), new Color(237, 52, 52), new Color(243, 95, 95), new Color(250, 52, 52)));
        ORANGE = ThemeColorRegistry.registerColorRange("orange", new ThemeColorRange(new Color(214, 115, 70), new Color(248, 117, 41), new Color(236, 116, 46), new Color(251, 121, 51), new Color(255, 108, 46)));
        YELLOW = ThemeColorRegistry.registerColorRange("yellow", new ThemeColorRange(new Color(214, 185, 70), new Color(248, 220, 41), new Color(236, 211, 46), new Color(251, 218, 51), new Color(255, 220, 46)));
        GREEN = ThemeColorRegistry.registerColorRange("green", new ThemeColorRange(new Color(123, 214, 70), new Color(79, 248, 41), new Color(87, 236, 46), new Color(58, 251, 51), new Color(123, 255, 46)));
        BLUE = ThemeColorRegistry.registerColorRange("blue", new ThemeColorRange(new Color(63, 192, 239), new Color(50, 140, 255), new Color(73, 139, 225), new Color(38, 128, 255), new Color(50, 172, 255)));
        SMOKE = ThemeColorRegistry.registerColorRange("smoke", new ThemeColorRange(new Color(45, 45, 45, 70), new Color(121, 121, 121, 70), new Color(120, 120, 120, 70), new Color(189, 189, 189, 70), new Color(237, 237, 237, 70), new Color(214, 214, 214, 70), new Color(80, 80, 80, 70), new Color(60, 60, 60, 70), new Color(30, 30, 30, 70)));
        EMBERGLOW = ThemeColorRegistry.registerColorRange("emberglow", new ThemeColorRange(new Color(45, 45, 45), new Color(63, 44, 44), new Color(42, 37, 35), new Color(70, 63, 63), new Color(60, 60, 60), new Color(30, 30, 30), new Color(175, 11, 7), new Color(227, 72, 13)));
        FIRE = ThemeColorRegistry.registerColorRange("fire", new ThemeColorRange(new Color(176, 54, 0), new Color(255, 111, 22), new Color(227, 72, 13), new Color(229, 31, 0), new Color(255, 142, 62), new Color(255, 121, 62), new Color(255, 109, 46), new Color(255, 96, 60), new Color(255, 166, 0), new Color(255, 171, 62), new Color(255, 187, 84), new Color(255, 183, 100)));
        WATER = ThemeColorRegistry.registerColorRange("water", new ThemeColorRange(new Color(0, 105, 148), new Color(0, 168, 232), new Color(0, 204, 204), new Color(64, 224, 208), new Color(72, 202, 228), new Color(0, 191, 255), new Color(28, 169, 201), new Color(0, 255, 255), new Color(102, 205, 170), new Color(32, 178, 170), new Color(0, 139, 139), new Color(70, 130, 180)));
        ICE = ThemeColorRegistry.registerColorRange("ice", new ThemeColorRange(new Color(208, 255, 247), new Color(198, 228, 255), new Color(209, 249, 255), new Color(255, 255, 255), new Color(240, 251, 255), new Color(223, 250, 255)));
        LIGHTNING = ThemeColorRegistry.registerColorRange("lightning", new ThemeColorRange(new Color(255, 255, 204), new Color(255, 238, 153), new Color(255, 255, 153), new Color(255, 225, 81), new Color(255, 225, 81), new Color(255, 211, 0), new Color(255, 217, 28), new Color(255, 226, 0), new Color(255, 225, 95), new Color(255, 228, 112), new Color(255, 243, 145), new Color(153, 204, 255), new Color(255, 248, 224)));
        POISON = ThemeColorRegistry.registerColorRange("poison", new ThemeColorRange(new Color(20, 30, 133), new Color(44, 21, 154), new Color(7, 11, 93), new Color(52, 7, 96), new Color(37, 19, 86), new Color(4, 11, 28), new Color(6, 2, 63), new Color(11, 4, 28)));
        DAMAGE_DECREASE = ThemeColorRegistry.registerColorRange("damagedecrease", new ThemeColorRange(new Color(51, 60, 65), new Color(112, 117, 117), new Color(76, 84, 84), new Color(93, 102, 107), new Color(148, 150, 152)));
        WEAKNESS = ThemeColorRegistry.registerColorRange("weakness", new ThemeColorRange(new Color(65, 51, 51), new Color(117, 112, 112), new Color(84, 76, 76), new Color(93, 102, 107), new Color(152, 148, 148)));
        ATTACKSLOW = ThemeColorRegistry.registerColorRange("attackslow", new ThemeColorRange(new Color(141, 141, 102), new Color(168, 163, 125), new Color(134, 128, 76), new Color(178, 172, 122), new Color(192, 184, 135)));
        HEAL = ThemeColorRegistry.registerColorRange("heal", new ThemeColorRange(new Color(144, 238, 144), new Color(152, 251, 243), new Color(124, 252, 0), new Color(75, 255, 47), new Color(47, 255, 0), new Color(50, 205, 50), new Color(34, 139, 102), new Color(50, 205, 146), new Color(112, 255, 124), new Color(189, 252, 201), new Color(197, 255, 248)));
        SLIME = ThemeColorRegistry.registerColorRange("slime", new ThemeColorRange(new Color(90, 127, 47), new Color(47, 127, 75), new Color(32, 50, 21), new Color(21, 50, 34), new Color(76, 143, 33), new Color(33, 143, 68), new Color(128, 154, 50), new Color(50, 154, 55), new Color(39, 93, 58), new Color(60, 93, 39)));
        SAND = ThemeColorRegistry.registerColorRange("sand", new ThemeColorRange(new Color(226, 194, 123), new Color(210, 180, 108), new Color(245, 222, 179), new Color(194, 166, 90), new Color(247, 233, 193), new Color(230, 211, 143), new Color(240, 230, 140), new Color(217, 195, 106), new Color(184, 161, 74), new Color(243, 226, 158)));
        EXPERIENCE = ThemeColorRegistry.registerColorRange("experience", new ThemeColorRange(new Color(144, 238, 144), new Color(152, 251, 152), new Color(218, 224, 27), new Color(127, 255, 0), new Color(255, 240, 75), new Color(50, 205, 50), new Color(34, 139, 34), new Color(154, 205, 50), new Color(202, 255, 112), new Color(189, 252, 201), new Color(251, 255, 197)));
        POLYMORPH = ThemeColorRegistry.registerColorRange("polymorph", new ThemeColorRange(new Color(255, 255, 255), new Color(255, 255, 255), new Color(255, 255, 255), new Color(255, 255, 255), new Color(171, 255, 239), new Color(245, 222, 255), new Color(210, 255, 175), new Color(192, 171, 255), new Color(255, 244, 139), new Color(255, 245, 222)));
        NECROTIC = ThemeColorRegistry.registerColorRange("necrotic", new ThemeColorRange(new Color(82, 38, 117), new Color(55, 22, 55), new Color(88, 22, 88), new Color(11, 22, 33), new Color(41, 122, 53), new Color(11, 88, 33)));
        BLOOD = ThemeColorRegistry.registerColorRange("blood", new ThemeColorRange(new Color(180, 0, 0), new Color(140, 10, 10), new Color(100, 0, 0), new Color(80, 0, 0), new Color(60, 10, 10), new Color(40, 0, 0)));
        DEEPFROST = ThemeColorRegistry.registerColorRange("deepfrost", new ThemeColorRange(new Color(18, 24, 52), new Color(28, 37, 80), new Color(111, 139, 214), new Color(57, 81, 146), new Color(44, 57, 120)));
        SOULSEED = ThemeColorRegistry.registerColorRange("soulseed", new ThemeColorRange(new Color(248, 93, 128), new Color(255, 125, 175), new Color(26, 51, 44), new Color(73, 137, 96), new Color(85, 182, 125), new Color(13, 29, 56), new Color(39, 81, 114), new Color(27, 56, 76)));
        SAPPHIRE = ThemeColorRegistry.registerColorRange("sapphire", new ThemeColorRange(new Color(116, 245, 253), new Color(193, 251, 255), new Color(82, 210, 255)));
        RUBY = ThemeColorRegistry.registerColorRange("ruby", new ThemeColorRange(new Color(182, 60, 53), new Color(205, 73, 113), new Color(255, 187, 199), new Color(130, 33, 29)));
        AMETHYST = ThemeColorRegistry.registerColorRange("amethyst", new ThemeColorRange(new Color(121, 100, 186), new Color(156, 155, 239), new Color(165, 156, 248), new Color(220, 212, 255)));
        EMERALD = ThemeColorRegistry.registerColorRange("emerald", new ThemeColorRange(new Color(47, 105, 12), new Color(107, 132, 45), new Color(203, 196, 72)));
        TOPAZ = ThemeColorRegistry.registerColorRange("topaz", new ThemeColorRange(new Color(255, 188, 76), new Color(255, 233, 73), new Color(247, 244, 191)));
    }

    @Override
    protected void onRegister(ThemeColorRangeRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static ThemeColorRange getColorByID(int id) {
        return ((ThemeColorRangeRegistryElement)ThemeColorRegistry.instance.getElement((int)id)).colorRange;
    }

    public static <T extends ThemeColorRange> T registerColorRange(String stringID, T colorRange) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register buffs");
        }
        return ThemeColorRegistry.instance.registerObj((String)stringID, new ThemeColorRangeRegistryElement<T>(colorRange)).colorRange;
    }

    protected static class ThemeColorRangeRegistryElement<T extends ThemeColorRange>
    implements IDDataContainer {
        public final T colorRange;

        public ThemeColorRangeRegistryElement(T colorRange) {
            this.colorRange = colorRange;
        }

        @Override
        public IDData getIDData() {
            return ((ThemeColorRange)this.colorRange).idData;
        }
    }
}

