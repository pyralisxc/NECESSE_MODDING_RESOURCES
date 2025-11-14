/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.engine.registries.RegionLayersList;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.SubRegionDataRegionLayer;
import necesse.level.maps.regionSystem.layers.BiomeBlendingRegionLayer;
import necesse.level.maps.regionSystem.layers.BiomeRegionLayer;
import necesse.level.maps.regionSystem.layers.JobsRegionLayer;
import necesse.level.maps.regionSystem.layers.LiquidDataRegionLayer;
import necesse.level.maps.regionSystem.layers.LogicRegionLayer;
import necesse.level.maps.regionSystem.layers.ObjectRegionLayer;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.SplattingRegionLayer;
import necesse.level.maps.regionSystem.layers.TileRegionLayer;
import necesse.level.maps.regionSystem.layers.TilesProtectedRegionLayer;
import necesse.level.maps.regionSystem.layers.WireDataRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ClientTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.FrameTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.LevelJobsSubmitterRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.RegionPacketHandlerRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ServerTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickEffectRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.WireUpdateRegionLayer;
import necesse.level.maps.regionSystem.layers.lighting.LightingRegionLayer;

public class RegionLayerRegistry
extends ClassedGameRegistry<RegionLayer, LayerRegistryElement> {
    public static int BIOME_LAYER;
    public static int BIOME_BLENDING_LAYER;
    public static int TILE_LAYER;
    public static int SPLATTING_LAYER;
    public static int OBJECT_LAYER;
    public static int LOGIC_LAYER;
    public static int WIRE_LAYER;
    public static int TILE_PROTECTED_LAYER;
    public static int JOBS_LAYER;
    public static int LIQUID_DATA_LAYER;
    public static int SUB_REGION_DATA_LAYER;
    public static int LIGHT_LAYER;
    public static final RegionLayerRegistry instance;
    private static final Class<?>[] interfaces;
    private static final int[] totalInterfaces;

    public RegionLayerRegistry() {
        super("RegionLayer", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        BIOME_LAYER = RegionLayerRegistry.registerLayer("biomes", BiomeRegionLayer.class);
        BIOME_BLENDING_LAYER = RegionLayerRegistry.registerLayer("biomeBlending", BiomeBlendingRegionLayer.class);
        TILE_LAYER = RegionLayerRegistry.registerLayer("tiles", TileRegionLayer.class);
        SPLATTING_LAYER = RegionLayerRegistry.registerLayer("splatting", SplattingRegionLayer.class);
        OBJECT_LAYER = RegionLayerRegistry.registerLayer("objects", ObjectRegionLayer.class);
        LOGIC_LAYER = RegionLayerRegistry.registerLayer("logic", LogicRegionLayer.class);
        WIRE_LAYER = RegionLayerRegistry.registerLayer("wire", WireDataRegionLayer.class);
        TILE_PROTECTED_LAYER = RegionLayerRegistry.registerLayer("tilesProtected", TilesProtectedRegionLayer.class);
        JOBS_LAYER = RegionLayerRegistry.registerLayer("jobs", JobsRegionLayer.class);
        LIQUID_DATA_LAYER = RegionLayerRegistry.registerLayer("liquidData", LiquidDataRegionLayer.class);
        SUB_REGION_DATA_LAYER = RegionLayerRegistry.registerLayer("subRegionData", SubRegionDataRegionLayer.class);
        LIGHT_LAYER = RegionLayerRegistry.registerLayer("lightData", LightingRegionLayer.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    @Override
    protected void onRegister(LayerRegistryElement object, int id, String stringID, boolean isReplace) {
        super.onRegister(object, id, stringID, isReplace);
        for (int i = 0; i < interfaces.length; ++i) {
            if (!object.implementsInterfaces[i]) continue;
            int n = i;
            totalInterfaces[n] = totalInterfaces[n] + 1;
        }
    }

    public static int registerLayer(String stringID, Class<? extends RegionLayer> layerClass) {
        try {
            if (LoadedMod.isRunningModClientSide()) {
                throw new IllegalStateException("Client/server only mods cannot register region layers");
            }
            return instance.register(stringID, new LayerRegistryElement(layerClass));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(layerClass.getSimpleName() + " does not have a constructor with single parameter: Region");
        }
    }

    public static int replaceLayer(String stringID, Class<? extends RegionLayer> layerClass) {
        try {
            return instance.replace(stringID, new LayerRegistryElement(layerClass));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(layerClass.getSimpleName() + " does not have a constructor with single parameter: Region");
        }
    }

    public static int getLayerID(Class<? extends RegionLayer> layerClass) {
        try {
            return instance.getElementIDRaw(layerClass);
        }
        catch (NoSuchElementException e) {
            System.err.println("Could not find RegionLayer id for " + layerClass.getSimpleName());
            return -1;
        }
    }

    public static int getLayerID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static RegionLayersList getNewLayersArray(Region region) {
        RegionLayer[] allLayers = new RegionLayer[instance.size()];
        int interferface = 0;
        ArrayList<FrameTickRegionLayer> frameTickLayers = new ArrayList<FrameTickRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<ClientTickRegionLayer> clientTickLayers = new ArrayList<ClientTickRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<ServerTickRegionLayer> serverTickLayers = new ArrayList<ServerTickRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<TileTickRegionLayer> tileTickLayers = new ArrayList<TileTickRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<TileTickEffectRegionLayer> tileTickEffectLayers = new ArrayList<TileTickEffectRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<WireUpdateRegionLayer> wireUpdateLayers = new ArrayList<WireUpdateRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<LevelJobsSubmitterRegionLayer> levelJobsSubmitterLayers = new ArrayList<LevelJobsSubmitterRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<RegionPacketHandlerRegionLayer> regionPacketHandlerLayers = new ArrayList<RegionPacketHandlerRegionLayer>(totalInterfaces[interferface++]);
        ArrayList<SaveDataRegionLayer> saveDataLayers = new ArrayList<SaveDataRegionLayer>(totalInterfaces[interferface++]);
        for (int i = 0; i < allLayers.length; ++i) {
            LayerRegistryElement e = (LayerRegistryElement)instance.getElement(i);
            try {
                RegionLayer regionLayer;
                allLayers[i] = regionLayer = e.newRegionLayer(region, frameTickLayers, clientTickLayers, serverTickLayers, tileTickLayers, tileTickEffectLayers, wireUpdateLayers, levelJobsSubmitterLayers, regionPacketHandlerLayers, saveDataLayers);
                continue;
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                throw new RuntimeException("Could not create new RegionLayer object for " + e.layerClass.getSimpleName(), ex);
            }
        }
        return new RegionLayersList(allLayers, frameTickLayers, clientTickLayers, serverTickLayers, tileTickLayers, tileTickEffectLayers, wireUpdateLayers, levelJobsSubmitterLayers, regionPacketHandlerLayers, saveDataLayers);
    }

    static {
        instance = new RegionLayerRegistry();
        interfaces = new Class[]{FrameTickRegionLayer.class, ClientTickRegionLayer.class, ServerTickRegionLayer.class, TileTickRegionLayer.class, TileTickEffectRegionLayer.class, WireUpdateRegionLayer.class, LevelJobsSubmitterRegionLayer.class, RegionPacketHandlerRegionLayer.class, SaveDataRegionLayer.class};
        totalInterfaces = new int[interfaces.length];
    }

    protected static class LayerRegistryElement
    extends ClassIDDataContainer<RegionLayer> {
        private final Class<? extends RegionLayer> layerClass;
        private final boolean[] implementsInterfaces;

        public LayerRegistryElement(Class<? extends RegionLayer> layerClass) throws NoSuchMethodException {
            super(layerClass, Region.class);
            this.layerClass = layerClass;
            this.implementsInterfaces = new boolean[interfaces.length];
            for (int i = 0; i < interfaces.length; ++i) {
                this.implementsInterfaces[i] = interfaces[i].isAssignableFrom(layerClass);
            }
        }

        public RegionLayer newRegionLayer(Region region, ArrayList<Object> ... lists) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            RegionLayer layer = (RegionLayer)this.newInstance(region);
            layer.idData.setData(this.getID(), this.getStringID());
            for (int i = 0; i < interfaces.length; ++i) {
                if (!this.implementsInterfaces[i]) continue;
                lists[i].add(interfaces[i].cast(layer));
            }
            return layer;
        }
    }
}

