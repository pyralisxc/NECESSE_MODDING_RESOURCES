/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.IDData;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.settler.DietThought;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;
import necesse.level.maps.levelData.settlementData.settler.MoodDescription;
import necesse.level.maps.levelData.settlementData.settler.PopulationThought;
import necesse.level.maps.levelData.settlementData.settler.RoomQuality;
import necesse.level.maps.levelData.settlementData.settler.RoomSize;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public abstract class Settler {
    public static TreeSet<MoodDescription> moods = new TreeSet<MoodDescription>(Comparator.comparingInt(s -> s.minHappiness));
    public static TreeSet<PopulationThought> populationThoughts = new TreeSet<PopulationThought>(Comparator.comparingInt(s -> s.population));
    public static TreeSet<RoomSize> roomSizes = new TreeSet<RoomSize>(Comparator.comparingInt(s -> s.minSize));
    public static TreeSet<RoomQuality> roomQualities = new TreeSet<RoomQuality>(Comparator.comparingInt(s -> s.minScore));
    public static TreeSet<FoodQuality> foodQualities = new TreeSet<FoodQuality>(Comparator.comparingInt(s -> s.happinessIncrease));
    public static TreeSet<DietThought> dietThoughts = new TreeSet<DietThought>(Comparator.comparingInt(s -> s.variety));
    public static FoodQuality FOOD_SIMPLE = new FoodQuality(new LocalMessage("settlement", "foodsimple"), 10, "B-C-A", "simple");
    public static FoodQuality FOOD_FINE = new FoodQuality(new LocalMessage("settlement", "foodfine"), 20, "B-C-B", "fine");
    public static FoodQuality FOOD_GOURMET = new FoodQuality(new LocalMessage("settlement", "foodgourmet"), 35, "B-C-C", "gourmet");
    public static MobSpawnArea SETTLER_SPAWN_AREA;
    public final IDData idData = new IDData();
    public final String mobStringID;
    public boolean isPartOfCompleteHost = true;
    public GameTexture texture;

    public static MoodDescription getMood(int happiness) {
        for (MoodDescription description : moods.descendingSet()) {
            if (happiness < description.minHappiness) continue;
            return description;
        }
        return moods.first();
    }

    public static PopulationThought getPopulationThough(int totalSettlers) {
        for (PopulationThought thought : populationThoughts.descendingSet()) {
            if (totalSettlers < thought.population) continue;
            return thought;
        }
        return null;
    }

    public static RoomSize getRoomSize(int roomSize) {
        for (RoomSize size : roomSizes.descendingSet()) {
            if (roomSize < size.minSize) continue;
            return size;
        }
        return null;
    }

    public static RoomQuality getRoomQuality(int furnitureScore) {
        for (RoomQuality quality : roomQualities.descendingSet()) {
            if (furnitureScore < quality.minScore) continue;
            return quality;
        }
        return null;
    }

    public static DietThought getDietThought(int variety) {
        for (DietThought thought : dietThoughts.descendingSet()) {
            if (variety < thought.variety) continue;
            return thought;
        }
        return null;
    }

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public Settler(String mobStringID) {
        if (SettlerRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct Settler objects when settler registry is closed, since they are a static registered objects. Use SettlerRegistry.getSettler(...) to get settlers.");
        }
        this.mobStringID = mobStringID;
    }

    public void onSettlerRegistryClosed() {
        Mob m;
        if (this.mobStringID != null && !((m = MobRegistry.getMob(this.mobStringID, null)) instanceof SettlerMob)) {
            throw new IllegalArgumentException(this.mobStringID + " mob does not exist or does not implement SettlerMob interface.");
        }
    }

    public float getArriveAsRecruitAfterDeathChance(ServerSettlementData settlement) {
        return 0.9f;
    }

    public boolean canSpawnInSettlement(ServerSettlementData settlement, PlayerStats stats) {
        return false;
    }

    public boolean canMoveOut(LevelSettler settler, ServerSettlementData settlement) {
        return true;
    }

    public boolean canBanish(LevelSettler settler, ServerSettlementData settlement) {
        return true;
    }

    public double getSpawnChance(Server server, ServerClient client, Level level) {
        return 0.0;
    }

    public GameMessage getAcquireTip() {
        return null;
    }

    public void spawnAtClient(Server server, ServerClient client, Level level) {
    }

    public boolean isValidBed(SettlementBed bed) {
        return this.canUseBed(bed) == null;
    }

    public GameMessage canUseBed(SettlementBed bed) {
        return null;
    }

    private static Point getFaceHairTextureOffset() {
        return new Point(-16, -12);
    }

    public static DrawOptions getHumanFaceDrawOptions(HumanDrawOptions humanDrawOptions, int size, int drawX, int drawY) {
        return Settler.getHumanFaceDrawOptions(humanDrawOptions, size, drawX, drawY, null);
    }

    public static DrawOptions getHumanFaceDrawOptions(HumanDrawOptions humanDrawOptions, int size, int drawX, int drawY, Consumer<HumanDrawOptions> additionalModifiers) {
        Point offset = Settler.getFaceHairTextureOffset();
        float sizeChange = 32.0f / (float)size;
        int offsetX = (int)((float)offset.x / sizeChange);
        int offsetY = (int)((float)offset.y / sizeChange);
        humanDrawOptions = humanDrawOptions.sprite(0, 2).dir(2).bodyTexture(null).feetTexture(null).size(size * 2, size * 2).leftArmsTexture(null).rightArmsTexture(null).chestplate(null).boots(null).holdItem(null);
        if (additionalModifiers != null) {
            additionalModifiers.accept(humanDrawOptions);
        }
        return humanDrawOptions.pos(drawX + offsetX, drawY + offsetY);
    }

    public DrawOptions getSettlerFaceDrawOptions(int drawX, int drawY, int size, Mob settlerMob) {
        if (settlerMob != null && settlerMob instanceof HumanMob) {
            HumanMob humanMob = (HumanMob)settlerMob;
            HumanDrawOptions humanOptions = new HumanDrawOptions(null, humanMob.look, !humanMob.customLook);
            humanMob.setDefaultArmor(humanOptions);
            return Settler.getHumanFaceDrawOptions(humanOptions, size, drawX, drawY);
        }
        return this.getSettlerIcon().initDraw().size(size).pos(drawX, drawY);
    }

    public DrawOptions getSettlerFlagDrawOptions(int midDrawX, int drawY, Mob settlerMob) {
        DrawOptionsList options = new DrawOptionsList();
        GameTexture texture = Settings.UI.settler_house;
        options.add(texture.initDraw().pos(midDrawX - texture.getWidth() / 2, drawY));
        options.add(this.getSettlerFaceDrawOptions(midDrawX - 16, drawY + texture.getHeight() / 2 - 16, 32, settlerMob));
        return options;
    }

    public DrawOptions getSettlerFlagDrawOptionsTile(int tileX, int tileY, GameCamera camera, Mob settlerMob) {
        int drawX = camera.getTileDrawX(tileX) + 16;
        int drawY = camera.getTileDrawY(tileY);
        if (drawX < -64 || drawY < -64 || drawX > camera.getWidth() || drawY > camera.getHeight()) {
            return () -> {};
        }
        return this.getSettlerFlagDrawOptions(drawX, drawY, settlerMob);
    }

    public boolean isMouseOverSettlerFlag(int tileX, int tileY, GameCamera camera) {
        return tileX == camera.getMouseLevelTilePosX() && tileY == camera.getMouseLevelTilePosY();
    }

    public GameTexture getSettlerIcon() {
        return this.texture;
    }

    public void loadTextures() {
        try {
            try {
                this.texture = GameTexture.fromFileRaw("mobs/icons/" + this.getStringID() + "human");
            }
            catch (FileNotFoundException e) {
                this.texture = GameTexture.fromFileRaw("mobs/icons/" + this.getStringID());
            }
        }
        catch (FileNotFoundException e) {
            this.texture = GameTexture.fromFile("settlers/" + this.getStringID());
        }
    }

    public String getGenericMobName() {
        return MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobStringID));
    }

    public void onMoveIn(LevelSettler settler) {
    }

    public final SettlerMob getNewSettlerMob(ServerSettlementData settlement) {
        return this.getNewSettlerMob(settlement.getLevel(), settlement);
    }

    public SettlerMob getNewSettlerMob(Level level, ServerSettlementData settlement) {
        SettlerMob settlerMob = (SettlerMob)((Object)MobRegistry.getMob(this.mobStringID, level));
        if (settlement != null) {
            Point spawnPos = Settler.getNewSettlerSpawnPos(settlerMob.getMob(), settlement);
            if (spawnPos != null) {
                settlerMob.getMob().setPos(spawnPos.x, spawnPos.y, true);
                return settlerMob;
            }
        } else {
            return settlerMob;
        }
        return null;
    }

    public static Point getNewSettlerSpawnPos(Mob mob, ServerSettlementData settlement) {
        return settlement.findRandomSpawnLevelPos(mob, true);
    }

    protected Supplier<HumanMob> getNewRecruitMob(ServerSettlementData data) {
        return () -> {
            Mob mob = MobRegistry.getMob(this.mobStringID, data.getLevel());
            if (mob instanceof HumanMob) {
                return (HumanMob)mob;
            }
            return null;
        };
    }

    protected boolean doesSettlementHaveThisSettler(ServerSettlementData data) {
        return data.settlers.stream().anyMatch(s -> s.settler == this);
    }

    public void addNewRecruitSettler(ServerSettlementData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
    }

    public static void tickServerClientSpawn(Server server, ServerClient client) {
        for (Settler settler : SettlerRegistry.getSettlers()) {
            if (!GameRandom.globalRandom.getChance(settler.getSpawnChance(server, client, client.getLevel()))) continue;
            settler.spawnAtClient(server, client, client.getLevel());
        }
    }

    protected Point getSpawnLocation(ServerClient client, Level level, Mob mob, MobSpawnArea spawnArea, Function<Point, Integer> ticketsGetter) {
        Point spawnPos = EntityManager.getMobSpawnTile(level, client.playerMob.getX(), client.playerMob.getY(), spawnArea, ticketsGetter);
        if (spawnPos != null && !mob.collidesWith(level, spawnPos.x * 32 + 16, spawnPos.y * 32 + 16)) {
            return new Point(spawnPos.x * 32 + 16, spawnPos.y * 32 + 16);
        }
        return null;
    }

    protected Point getSpawnLocation(ServerClient client, Level level, Mob mob, MobSpawnArea spawnArea) {
        return this.getSpawnLocation(client, level, mob, spawnArea, tile -> {
            if (level.isSolidTile(tile.x, tile.y)) {
                return 0;
            }
            if (level.isLiquidTile(tile.x, tile.y)) {
                return 0;
            }
            return 100;
        });
    }

    static {
        moods.add(new MoodDescription(new LocalMessage("settlement", "moodveryunhappy"), 0));
        moods.add(new MoodDescription(new LocalMessage("settlement", "moodunhappy"), 25));
        moods.add(new MoodDescription(new LocalMessage("settlement", "moodsomewhathappy"), 50));
        moods.add(new MoodDescription(new LocalMessage("settlement", "moodveryhappy"), 70));
        moods.add(new MoodDescription(new LocalMessage("settlement", "moodextremelyhappy"), 90));
        populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "tinysettlement"), 0, 40));
        populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "smallsettlement"), 6, 30));
        populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "averagesettlement"), 12, 20));
        populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "largesettlement"), 18, 10));
        populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "hugesettlement"), 24, 0));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizebaby"), 0, 0));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizetiny"), 10, 4));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizesmall"), 20, 8));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizemediocre"), 25, 10));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizedecent"), 30, 12));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizelarge"), 40, 15));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizehuge"), 50, 18));
        roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizeenormous"), 60, 20));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomplain"), 0, 0));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomdull"), 1, 4));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomsimple"), 2, 7));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomnormal"), 3, 10));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomgood"), 4, 13));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomimpressive"), 5, 15));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomwonderful"), 6, 17));
        roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomunrivaled"), 7, 20));
        foodQualities.add(FOOD_SIMPLE);
        foodQualities.add(FOOD_FINE);
        foodQualities.add(FOOD_GOURMET);
        dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietsame"), 0, 0));
        dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietslightly"), 2, 10));
        dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietsomewhat"), 5, 20));
        dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietnicely"), 8, 30));
        dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietextremely"), 12, 40));
        SETTLER_SPAWN_AREA = new MobSpawnArea(800, 1280);
    }
}

