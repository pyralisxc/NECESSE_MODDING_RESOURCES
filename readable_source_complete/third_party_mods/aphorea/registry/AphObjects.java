/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.inventory.item.toolItem.ToolType
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.RockObject
 *  necesse.level.gameObject.SingleRockObject
 *  necesse.level.gameObject.WallObject
 *  necesse.level.gameObject.container.BookshelfObject
 *  necesse.level.gameObject.container.CabinetObject
 *  necesse.level.gameObject.container.StorageBoxInventoryObject
 *  necesse.level.gameObject.furniture.BedObject
 *  necesse.level.gameObject.furniture.CandelabraObject
 *  necesse.level.gameObject.furniture.ChairObject
 *  necesse.level.gameObject.furniture.DinnerTableObject
 *  necesse.level.gameObject.furniture.doubleBed.DoubleBedBaseObject
 */
package aphorea.registry;

import aphorea.objects.AphSaplingObject;
import aphorea.objects.AphTreeObject;
import aphorea.objects.BabylonEntranceObject;
import aphorea.objects.BabylonExitObject;
import aphorea.objects.BabylonTowerObject;
import aphorea.objects.FakeSpinelChest;
import aphorea.objects.GoldWitchStatue;
import aphorea.objects.InfectedGrassObject;
import aphorea.objects.InfectedTrialEntranceObject;
import aphorea.objects.RockyWallObject;
import aphorea.objects.RunesTable;
import aphorea.objects.SpinelClusterObject;
import aphorea.objects.SpinelClusterSmallObject;
import aphorea.objects.TungstenGelRockOreObject;
import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.engine.registries.ObjectRegistry;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.container.BookshelfObject;
import necesse.level.gameObject.container.CabinetObject;
import necesse.level.gameObject.container.StorageBoxInventoryObject;
import necesse.level.gameObject.furniture.BedObject;
import necesse.level.gameObject.furniture.CandelabraObject;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.gameObject.furniture.DinnerTableObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBedBaseObject;

public class AphObjects {
    public static int GEL_ROCK;

    public static void registerCore() {
        ObjectRegistry.registerObject((String)"goldwitchstatue", (GameObject)new GoldWitchStatue(), (float)-1.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"fakespinelchest", (GameObject)new FakeSpinelChest(), (float)-1.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"spinelchest", (GameObject)new StorageBoxInventoryObject("spinelchest", 40, AphColors.spinel, new String[0]), (float)20.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"infectedtrialentrance", (GameObject)new InfectedTrialEntranceObject(), (float)0.0f, (boolean)false);
        AphObjects.tech();
        AphObjects.multiTileObjects();
        AphObjects.surfaceObjects();
        AphObjects.caveObjects();
        WallObject.registerWallObjects((String)"infectedwood", (String)"infectedwoodwall", (float)0.0f, (Color)AphColors.infected_wood, (ToolType)ToolType.ALL, (float)2.0f, (float)6.0f);
        AphObjects.gelRock();
        DinnerTableObject.registerDinnerTable((String)"infecteddinnertable", (String)"infecteddinnertable", (Color)AphColors.infected_wood, (float)20.0f);
        ObjectRegistry.registerObject((String)"infectedchair", (GameObject)new ChairObject("infectedchair", AphColors.infected_wood, new String[0]), (float)5.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"infectedbookshelf", (GameObject)new BookshelfObject("infectedbookshelf", AphColors.infected_wood, new String[0]), (float)10.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"infectedcabinet", (GameObject)new CabinetObject("infectedcabinet", AphColors.infected_wood, new String[0]), (float)10.0f, (boolean)true);
        BedObject.registerBed((String)"infectedbed", (String)"infectedbed", (Color)AphColors.infected_wood, (float)100.0f, (String[])new String[0]);
        DoubleBedBaseObject.registerDoubleBed((String)"infecteddoublebed", (String)"infecteddoublebed", (Color)AphColors.infected_wood, (float)150.0f, (String[])new String[0]);
        ObjectRegistry.registerObject((String)"infectedcandelabra", (GameObject)new CandelabraObject("infectedcandelabra", AphColors.infected_wood, 50.0f, 0.1f, new String[0]), (float)10.0f, (boolean)true);
    }

    public static void tech() {
        ObjectRegistry.registerObject((String)"runestable", (GameObject)new RunesTable(), (float)-1.0f, (boolean)true);
    }

    public static void multiTileObjects() {
        BabylonTowerObject.registerObject();
        BabylonEntranceObject.registerObject();
        BabylonExitObject.registerObject();
    }

    public static void surfaceObjects() {
        ObjectRegistry.registerObject((String)"infectedgrass", (GameObject)new InfectedGrassObject(), (float)-1.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"infectedsapling", (GameObject)new AphSaplingObject("infectedsapling", "infectedtree", 900, 1800, true, 50, 340.0f, 0.6f, "infectedgrasstile"), (float)10.0f, (boolean)true);
        ObjectRegistry.registerObject((String)"infectedtree", (GameObject)new AphTreeObject("infectedtree", "infectedlog", "infectedsapling", AphColors.infected_dark, 45, 60, 110, "infectedleaves", 100, 340.0f, 0.6f), (float)0.0f, (boolean)false);
    }

    public static void caveObjects() {
        SpinelClusterObject.registerCrystalCluster("spinelcluster", "spinelcluster", AphColors.spinel, 337.0f, -1.0f, true);
        ObjectRegistry.registerObject((String)"spinelclustersmall", (GameObject)new SpinelClusterSmallObject("spinelcluster_small", AphColors.spinel, 337.0f), (float)-1.0f, (boolean)true);
    }

    public static void gelRock() {
        RockyWallObject gelRock = new RockyWallObject("gelrock", AphColors.rock, "rockygel", 0, 1, 1);
        GEL_ROCK = ObjectRegistry.registerObject((String)"gelrock", (GameObject)gelRock, (float)-1.0f, (boolean)true);
        gelRock.toolTier = 3.0f;
        SingleRockObject.registerSurfaceRock((RockObject)gelRock, (String)"surfacegelrock", (Color)AphColors.rock_light, (int)1, (int)2, (int)1, (float)-1.0f, (boolean)true, (String[])new String[0]);
        ObjectRegistry.registerObject((String)"tungstenoregelrock", (GameObject)new TungstenGelRockOreObject(gelRock, "oremask", "tungstenore", AphColors.tungsten), (float)-1.0f, (boolean)true);
    }
}

