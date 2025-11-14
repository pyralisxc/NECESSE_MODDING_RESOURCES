/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import necesse.engine.registries.GameRegistry;
import necesse.level.maps.mapData.GameMapIcon;
import necesse.level.maps.mapData.ItemGameMapIcon;
import necesse.level.maps.mapData.MobGameMapIcon;
import necesse.level.maps.mapData.TextureGameMapIcon;

public class MapIconRegistry
extends GameRegistry<GameMapIcon> {
    public static final MapIconRegistry instance = new MapIconRegistry();
    public static GameMapIcon defaultIcon;

    private MapIconRegistry() {
        super("MapIcon", 32762);
    }

    @Override
    public void registerCore() {
        defaultIcon = MapIconRegistry.registerIcon("poi", new TextureGameMapIcon("ui/mapicons/poi", -14));
        MapIconRegistry.registerIcon("checkmark", new TextureGameMapIcon("ui/mapicons/checkmark"));
        MapIconRegistry.registerIcon("cross", new TextureGameMapIcon("ui/mapicons/cross"));
        MapIconRegistry.registerIcon("village", new TextureGameMapIcon("ui/mapicons/village"));
        MapIconRegistry.registerIcon("voidwizard", new MobGameMapIcon("voidwizard"));
        MapIconRegistry.registerIcon("piratebanner", new TextureGameMapIcon("ui/mapicons/piratebanner"));
        MapIconRegistry.registerIcon("strikebanner", new ItemGameMapIcon("strikebanner"));
        MapIconRegistry.registerIcon("bannerofpeace", new ItemGameMapIcon("bannerofpeace"));
        MapIconRegistry.registerIcon("bannerofwar", new ItemGameMapIcon("bannerofwar"));
    }

    @Override
    protected void onRegister(GameMapIcon object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static <T extends GameMapIcon> T registerIcon(String stringID, T preset) {
        instance.register(stringID, preset);
        return preset;
    }

    public static GameMapIcon getIcon(int id) {
        return (GameMapIcon)instance.getElement(id);
    }

    public static GameMapIcon getIcon(String stringID) {
        return (GameMapIcon)instance.getElement(stringID);
    }

    public static Iterable<GameMapIcon> getAllIcons() {
        return instance.getElements();
    }
}

