/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class BannerSet
extends PresetSet<BannerSet> {
    public static final BannerSet frost = (BannerSet)new BannerSet("frostbanner").all(BiomeRegistry.SNOW);
    public static final BannerSet dryad = (BannerSet)new BannerSet("dryadbanner").all(BiomeRegistry.FOREST, BiomeRegistry.SWAMP);
    public static final BannerSet eggcellent = (BannerSet)new BannerSet("eggcellentbanner").all(BiomeRegistry.PLAINS, BiomeRegistry.DESERT);
    public static final BannerSet fishian = (BannerSet)new BannerSet("fishianbanner").all(BiomeRegistry.SWAMP);
    public static final BannerSet pirate = (BannerSet)new BannerSet("piratebanner").all(BiomeRegistry.SWAMP, BiomeRegistry.FOREST, BiomeRegistry.PLAINS);
    public static final BannerSet vampiric = (BannerSet)new BannerSet("vampiricbanner").all(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.PLAINS);
    public final int small;
    public final int large;

    public BannerSet(String bannerID) {
        this.small = ObjectRegistry.getObjectID(bannerID);
        this.objectArrays = new int[][]{{this.small}, {this.large = ObjectRegistry.getObjectID("large" + bannerID)}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(BannerSet.class);
    }
}

