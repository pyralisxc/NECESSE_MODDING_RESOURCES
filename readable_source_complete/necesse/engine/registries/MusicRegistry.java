/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.awt.Color;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.StaticObjectGameRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.StringsCustomTextureGameMusic;

public class MusicRegistry
extends StaticObjectGameRegistry<MusicRegistryElement> {
    public static GameMusic AdventureBegins;
    public static GameMusic ForestPath;
    public static GameMusic AwakeningTwilight;
    public static GameMusic DepthsOfTheForest;
    public static GameMusic SecretsOfTheForest;
    public static GameMusic MeadowMeandering;
    public static GameMusic FieldsOfSerenity;
    public static GameMusic RunecarvedWalls;
    public static GameMusic ForgottenDepths;
    public static GameMusic AuroraTundra;
    public static GameMusic PolarNight;
    public static GameMusic GlaciersEmbrace;
    public static GameMusic SubzeroSanctum;
    public static GameMusic WatersideSerenade;
    public static GameMusic GatorsLullaby;
    public static GameMusic SwampCavern;
    public static GameMusic MurkyMire;
    public static GameMusic SandCatacombs;
    public static GameMusic OasisSerenade;
    public static GameMusic NightInTheDunes;
    public static GameMusic DustyHollows;
    public static GameMusic LostTemple;
    public static GameMusic VoidsEmbrace;
    public static GameMusic PiratesHorizon;
    public static GameMusic SlimeSurge;
    public static GameMusic VenomousReckoning;
    public static GameMusic PastBehindGlass;
    public static GameMusic InvasionoftheCrypt;
    public static GameMusic SettlementofHauntedDreams;
    public static GameMusic StormingTheHamletPart1;
    public static GameMusic StormingTheHamletPart2;
    public static GameMusic TheFirstTrial;
    public static GameMusic QueenSpidersDance;
    public static GameMusic PestWardensCharge;
    public static GameMusic WizardsRematch;
    public static GameMusic WizardsAwakening;
    public static GameMusic TheRuneboundTrialPart1;
    public static GameMusic TheRuneboundTrialPart2;
    public static GameMusic RumbleOfTheSwampGuardian;
    public static GameMusic AncientVulturesFeast;
    public static GameMusic PirateCaptainsLair;
    public static GameMusic ReapersRequiem;
    public static GameMusic BattleForTheFrozenReign;
    public static GameMusic TheCursedTriumph;
    public static GameMusic SymphonyOfTwins;
    public static GameMusic WrathOfTheEmpress;
    public static GameMusic MoonlightsRehearsal;
    public static GameMusic SunlightsExam;
    public static GameMusic DragonsHoard;
    public static GameMusic MotherSlimesTremble;
    public static GameMusic TheSwarmoftheNight;
    public static GameMusic AscendedReturn;
    public static GameMusic AscendedMadness;
    public static GameMusic FractureoftheVoid;
    public static GameMusic TheEldersJingleJam;
    public static GameMusic GatorsLullabyStrings;
    public static GameMusic DustyHollowsStrings;
    public static GameMusic LostTempleStrings;
    public static GameMusic VoidsEmbraceStrings;
    public static GameMusic AdventureBeginsStrings;
    public static GameMusic DepthsOfTheForestStrings;
    public static GameMusic ForestPathStrings;
    public static GameMusic AwakeningTwilightStrings;
    public static GameMusic Home;
    public static GameMusic WaterFae;
    public static GameMusic Muses;
    public static GameMusic Running;
    public static GameMusic GrindTheAlarms;
    public static GameMusic ByTheField;
    public static GameMusic SunStones;
    public static GameMusic CaravanTusks;
    public static GameMusic HomeAtLast;
    public static GameMusic TellTale;
    public static GameMusic IcyRuse;
    public static GameMusic IceStar;
    public static GameMusic EyesOfTheDesert;
    public static GameMusic Rialto;
    public static GameMusic SilverLake;
    public static GameMusic Away;
    public static GameMusic Kronos;
    public static GameMusic LostGrip;
    public static GameMusic ElekTrak;
    public static GameMusic TheControlRoom;
    public static GameMusic AirlockFailure;
    public static GameMusic KonsoleGlitch;
    public static GameMusic Beatdown;
    public static GameMusic Siege;
    public static GameMusic Halodrome;
    public static GameMusic Millenium;
    public static GameMusic Kandiru;
    public static final MusicRegistry instance;

    private MusicRegistry() {
        super("Music", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "loading"));
        AdventureBegins = MusicRegistry.registerMusic("adventurebegins", "music/adventurebegins", new StaticMessage("Adventure Begins"), new Color(47, 105, 12), new Color(47, 105, 12), new LocalMessage("itemtooltip", "fromnecesseost"));
        ForestPath = MusicRegistry.registerMusic("forestpath", "music/forestpath", new StaticMessage("Forest Path"), new Color(47, 105, 12), new Color(81, 136, 34), new LocalMessage("itemtooltip", "fromnecesseost"));
        AwakeningTwilight = MusicRegistry.registerMusic("awakeningtwilight", "music/awakeningtwilight", new StaticMessage("Awakening Twilight"), new Color(47, 105, 12), new Color(81, 101, 174), new LocalMessage("itemtooltip", "fromnecesseost"));
        DepthsOfTheForest = MusicRegistry.registerMusic("depthsoftheforest", "music/depthsoftheforest", new StaticMessage("Depths of the Forest"), new Color(47, 105, 12), new Color(126, 108, 84), new LocalMessage("itemtooltip", "fromnecesseost"));
        SecretsOfTheForest = MusicRegistry.registerMusic("secretsoftheforest", "music/secretsoftheforest", new StaticMessage("Secrets of The Forest"), new Color(47, 105, 12), new Color(209, 170, 57), new LocalMessage("itemtooltip", "fromnecesseost"));
        MeadowMeandering = MusicRegistry.registerMusic("meadowmeandering", "music/meadowmeandering", new StaticMessage("Meadow Meandering"), new Color(201, 156, 43), new Color(200, 217, 41), 4000, 7000, new LocalMessage("itemtooltip", "fromnecesseost"));
        FieldsOfSerenity = MusicRegistry.registerMusic("fieldsofserenity", "music/fieldsofserenity", new StaticMessage("Fields of Serenity"), new Color(201, 156, 43), new Color(81, 101, 174), 2000, 3000, new LocalMessage("itemtooltip", "fromnecesseost"));
        RunecarvedWalls = MusicRegistry.registerMusic("runecarvedwalls", "music/runecarvedwalls", new StaticMessage("Runecarved Walls"), new Color(201, 156, 43), new Color(126, 108, 84), new LocalMessage("itemtooltip", "fromnecesseost"));
        ForgottenDepths = MusicRegistry.registerMusic("forgottendepths", "music/forgottendepths", new StaticMessage("Forgotten Depths"), new Color(201, 156, 43), new Color(4, 197, 139), new LocalMessage("itemtooltip", "fromnecesseost"));
        AuroraTundra = MusicRegistry.registerMusic("auroratundra", "music/auroratundra", new StaticMessage("Aurora Tundra"), new Color(183, 232, 245), new Color(49, 142, 184), new LocalMessage("itemtooltip", "fromnecesseost"));
        PolarNight = MusicRegistry.registerMusic("polarnight", "music/polarnight", new StaticMessage("Polar Night"), new Color(183, 232, 245), new Color(121, 100, 186), new LocalMessage("itemtooltip", "fromnecesseost"));
        GlaciersEmbrace = MusicRegistry.registerMusic("glaciersembrace", "music/glaciersembrace", new StaticMessage("Glaciers Embrace"), new Color(183, 232, 245), new Color(138, 193, 150), new LocalMessage("itemtooltip", "fromnecesseost"));
        SubzeroSanctum = MusicRegistry.registerMusic("subzerosanctum", "music/subzerosanctum", new StaticMessage("Subzero Sanctum"), new Color(20, 118, 192), new Color(183, 232, 245), new LocalMessage("itemtooltip", "fromnecesseost"));
        WatersideSerenade = MusicRegistry.registerMusic("watersideserenade", "music/watersideserenade", new StaticMessage("Waterside Serenade"), new Color(150, 154, 38), new Color(120, 158, 36), new LocalMessage("itemtooltip", "fromnecesseost"));
        GatorsLullaby = MusicRegistry.registerMusic("gatorslullaby", "music/gatorslullaby", new StaticMessage("Gator's Lullaby"), new Color(150, 154, 38), new Color(81, 101, 174), new LocalMessage("itemtooltip", "fromnecesseost"));
        MurkyMire = MusicRegistry.registerMusic("murkymire", "music/murkymire", new StaticMessage("Murky Mire"), new Color(150, 154, 38), new Color(126, 97, 68), new LocalMessage("itemtooltip", "fromnecesseost"));
        SwampCavern = MusicRegistry.registerMusic("swampcavern", "music/swampcavern", new StaticMessage("Swamp Cavern"), new Color(150, 154, 38), new Color(191, 90, 62), new LocalMessage("itemtooltip", "fromnecesseost"));
        OasisSerenade = MusicRegistry.registerMusic("oasisserenade", "music/oasisserenade", new StaticMessage("Oasis Serenade"), new Color(232, 203, 130), new Color(178, 144, 98), new LocalMessage("itemtooltip", "fromnecesseost"));
        NightInTheDunes = MusicRegistry.registerMusic("nightinthedunes", "music/nightinthedunes", new StaticMessage("Night in the Dunes"), new Color(232, 203, 130), new Color(100, 101, 157), new LocalMessage("itemtooltip", "fromnecesseost"));
        DustyHollows = MusicRegistry.registerMusic("dustyhollows", "music/dustyhollows", new StaticMessage("Dusty Hollows"), new Color(232, 203, 130), new Color(172, 111, 110), new LocalMessage("itemtooltip", "fromnecesseost"));
        SandCatacombs = MusicRegistry.registerMusic("sandcatacombs", "music/sandcatacombs", new StaticMessage("Sand Catacombs"), new Color(232, 203, 130), new Color(182, 60, 53), new LocalMessage("itemtooltip", "fromnecesseost"));
        LostTemple = MusicRegistry.registerMusic("losttemple", "music/losttemple", new StaticMessage("Lost Temple"), new Color(232, 203, 130), new Color(217, 92, 35), new LocalMessage("itemtooltip", "fromnecesseost"));
        VoidsEmbrace = MusicRegistry.registerMusic("voidsembrace", "music/voidsembrace", new StaticMessage("Void's Embrace"), new Color(121, 100, 186), new Color(169, 150, 236), new LocalMessage("itemtooltip", "fromnecesseost"));
        PiratesHorizon = MusicRegistry.registerMusic("pirateshorizon", "music/pirateshorizon", new StaticMessage("Pirate's Horizon"), new Color(255, 177, 8), new Color(156, 51, 39), new LocalMessage("itemtooltip", "fromnecesseost"));
        SlimeSurge = MusicRegistry.registerMusic("slimesurge", "music/slimesurge", new StaticMessage("Slime Surge"), new Color(130, 210, 43), new Color(173, 65, 28), 2000, 5000, new LocalMessage("itemtooltip", "fromnecesseost"));
        VenomousReckoning = MusicRegistry.registerMusic("venomousreckoning", "music/venomousreckoning", new StaticMessage("Venomous Reckoning"), new Color(127, 189, 57), new Color(208, 204, 50), new LocalMessage("itemtooltip", "fromnecesseost"));
        PastBehindGlass = MusicRegistry.registerMusic("pastbehindglass", "music/pastbehindglass", new StaticMessage("Past Behind Glass"), new Color(198, 236, 255), new Color(248, 198, 218), new LocalMessage("itemtooltip", "fromnecesseost"));
        InvasionoftheCrypt = MusicRegistry.registerMusic("invasionofthecrypt", "music/invasionofthecrypt", new StaticMessage("Invasion of the Crypt"), new Color(24, 178, 26), new Color(16, 8, 52), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        SettlementofHauntedDreams = MusicRegistry.registerMusic("settlementofhaunteddreams", "music/settlementofhaunteddreams", new StaticMessage("Settlement of Haunted Dreams"), new Color(175, 30, 0), new Color(28, 0, 173), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        StormingTheHamletPart1 = MusicRegistry.registerMusic("stormingthehamletpart1", "music/stormingthehamletpart1", new StaticMessage("Storming the Hamlet Part 1"), new Color(255, 177, 8), new Color(191, 90, 62), new LocalMessage("itemtooltip", "fromnecesseost"));
        StormingTheHamletPart2 = MusicRegistry.registerMusic("stormingthehamletpart2", "music/stormingthehamletpart2", new StaticMessage("Storming the Hamlet Part 2"), new Color(255, 177, 8), new Color(156, 51, 39), new LocalMessage("itemtooltip", "fromnecesseost"));
        TheFirstTrial = MusicRegistry.registerMusic("firsttrial", "music/firsttrial", new StaticMessage("First Trial"), new Color(156, 51, 39), new Color(81, 136, 34), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        QueenSpidersDance = MusicRegistry.registerMusic("queenspidersdance", "music/queenspidersdance", new StaticMessage("Queen Spider's Dance"), new Color(156, 51, 39), new Color(81, 101, 174), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        WizardsAwakening = MusicRegistry.registerMusic("wizardsawakening", "music/wizardsawakening", new StaticMessage("Wizard's Awakening"), new Color(156, 51, 39), new Color(149, 133, 241), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        TheRuneboundTrialPart1 = MusicRegistry.registerMusic("theruneboundtrialpart1", "music/theruneboundtrialpart1", new StaticMessage("The Runebound Trail Part 1"), new Color(156, 51, 39), new Color(122, 68, 22), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        TheRuneboundTrialPart2 = MusicRegistry.registerMusic("theruneboundtrialpart2", "music/theruneboundtrialpart2", new StaticMessage("The Runebound Trail Part 2"), new Color(156, 51, 39), new Color(126, 108, 84), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        RumbleOfTheSwampGuardian = MusicRegistry.registerMusic("rumbleoftheswampguardian", "music/rumbleoftheswampguardian", new StaticMessage("Rumble of the Swamp Guardian"), new Color(156, 51, 39), new Color(150, 154, 38), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        AncientVulturesFeast = MusicRegistry.registerMusic("ancientvulturesfeast", "music/ancientvulturesfeast", new StaticMessage("Ancient Vulture's Feast"), new Color(156, 51, 39), new Color(97, 115, 8), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        PirateCaptainsLair = MusicRegistry.registerMusic("piratecaptainslair", "music/piratecaptainslair", new StaticMessage("Pirate Captain's Lair"), new Color(156, 51, 39), new Color(255, 177, 8), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        ReapersRequiem = MusicRegistry.registerMusic("reapersrequiem", "music/reapersrequiem", new StaticMessage("Reapers Requiem"), new Color(156, 51, 39), new Color(65, 116, 85), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        BattleForTheFrozenReign = MusicRegistry.registerMusic("battleforthefrozenreign", "music/battleforthefrozenreign", new StaticMessage("Battle for the Frozen Reign"), new Color(156, 51, 39), new Color(232, 203, 130), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        TheCursedTriumph = MusicRegistry.registerMusic("thecursedtriumph", "music/thecursedtriumph", new StaticMessage("The Cursed Triumph"), new Color(156, 51, 39), new Color(4, 197, 139), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        PestWardensCharge = MusicRegistry.registerMusic("pestwardenscharge", "music/pestwardenscharge", new StaticMessage("Pest Warden's Charge"), new Color(156, 51, 39), new Color(97, 115, 8), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        SymphonyOfTwins = MusicRegistry.registerMusic("symphonyoftwins", "music/symphonyoftwins", new StaticMessage("Symphony of Twins"), new Color(156, 51, 39), new Color(232, 203, 130), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        WizardsRematch = MusicRegistry.registerMusic("wizardsrematch", "music/wizardsrematch", new StaticMessage("Wizard's Rematch"), new Color(156, 51, 39), new Color(121, 100, 186), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        WrathOfTheEmpress = MusicRegistry.registerMusic("wrathoftheempress", "music/wrathoftheempress", new StaticMessage("Wrath of the Empress"), new Color(156, 51, 39), new Color(0, 107, 109), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        MoonlightsRehearsal = MusicRegistry.registerMusic("moonlightsrehearsal", "music/moonlightsrehearsal", new StaticMessage("Moonlight's Rehearsal"), new Color(156, 51, 39), new Color(4, 100, 194), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        SunlightsExam = MusicRegistry.registerMusic("sunlightsexam", "music/sunlightsexam", new StaticMessage("Sunlight's Exam"), new Color(156, 51, 39), new Color(255, 207, 67), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        DragonsHoard = MusicRegistry.registerMusic("dragonshoard", "music/dragonshoard", new StaticMessage("Dragon's Hoard"), new Color(156, 51, 39), new Color(220, 212, 255), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        MotherSlimesTremble = MusicRegistry.registerMusic("motherslimestremble", "music/motherslimestremble", new StaticMessage("Mother Slime's Tremble"), new Color(156, 51, 39), new Color(41, 224, 67), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        TheSwarmoftheNight = MusicRegistry.registerMusic("theswarmofthenight", "music/theswarmofthenight", new StaticMessage("The Swarm of the Night"), new Color(156, 51, 39), new Color(16, 8, 52), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        AscendedReturn = MusicRegistry.registerMusic("ascendedreturn", "music/ascendedreturn", new StaticMessage("Ascended Return"), new Color(227, 105, 52), new Color(133, 13, 175), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        AscendedMadness = MusicRegistry.registerMusic("ascendedmadness", "music/ascendedmadness", new StaticMessage("Ascended Madness"), new Color(52, 55, 227), new Color(133, 13, 175), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        FractureoftheVoid = MusicRegistry.registerMusic("fractureofthevoid", "music/fractureofthevoid", new StaticMessage("Fracture of The Void"), new Color(65, 126, 166), new Color(41, 5, 133), 1000, 4000, new LocalMessage("itemtooltip", "fromnecesseost"));
        TheEldersJingleJam = MusicRegistry.registerMusic("theeldersjinglejam", "music/theeldersjinglejam", new StaticMessage("The Elder's Jingle Jam"), new Color(228, 92, 95), new Color(205, 210, 218), new LocalMessage("itemtooltip", "fromnecesseost"));
        GatorsLullabyStrings = MusicRegistry.registerStringsMusic("gatorslullabystrings", "music/gatorslullabystrings", new StaticMessage("Gators Lullaby Strings"), new Color(150, 154, 38), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        DustyHollowsStrings = MusicRegistry.registerStringsMusic("dustyhollowsstrings", "music/dustyhollowsstrings", new StaticMessage("Dusty Hollows Strings"), new Color(172, 111, 110), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        LostTempleStrings = MusicRegistry.registerStringsMusic("losttemplestrings", "music/losttemplestrings", new StaticMessage("Lost Temple Strings"), new Color(217, 92, 35), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        VoidsEmbraceStrings = MusicRegistry.registerStringsMusic("voidsembracestrings", "music/voidsembracestrings", new StaticMessage("Void's Embrace Strings"), new Color(169, 150, 236), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        AdventureBeginsStrings = MusicRegistry.registerStringsMusic("adventurebeginsstrings", "music/adventurebeginsstrings", new StaticMessage("Adventure Begins Strings"), new Color(47, 105, 12), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        DepthsOfTheForestStrings = MusicRegistry.registerStringsMusic("depthsoftheforeststrings", "music/depthsoftheforeststrings", new StaticMessage("Depths Of The Forest Strings"), new Color(126, 108, 84), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        ForestPathStrings = MusicRegistry.registerStringsMusic("forestpathstrings", "music/forestpathstrings", new StaticMessage("Forest Path Strings"), new Color(81, 136, 34), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        AwakeningTwilightStrings = MusicRegistry.registerStringsMusic("awakeningtwilightstrings", "music/awakeningtwilightstrings", new StaticMessage("Awakening Twilight Strings"), new Color(81, 101, 174), new Color(255, 255, 255), new LocalMessage("itemtooltip", "performedbyhermes"));
        float oldMusicVolumeModifier = 0.6f;
        Home = MusicRegistry.registerMusic("home", "music/home", new StaticMessage("Home"), new Color(125, 164, 45), new Color(47, 105, 12), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        WaterFae = MusicRegistry.registerMusic("waterfae", "music/waterfae", new StaticMessage("WaterFae"), new Color(81, 136, 34), new Color(47, 105, 12), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Muses = MusicRegistry.registerMusic("muses", "music/muses", new StaticMessage("Muses"), new Color(81, 101, 174), new Color(47, 105, 12), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Running = MusicRegistry.registerMusic("running", "music/running", new StaticMessage("Running"), new Color(126, 108, 84), new Color(47, 105, 12), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        GrindTheAlarms = MusicRegistry.registerMusic("grindthealarms", "music/grindthealarms", new StaticMessage("GrindTheAlarms"), new Color(209, 170, 57), new Color(47, 105, 12), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        HomeAtLast = MusicRegistry.registerMusic("homeatlast", "music/homeatlast", new StaticMessage("HomeAtLast"), new Color(49, 142, 184), new Color(183, 232, 245), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        TellTale = MusicRegistry.registerMusic("telltale", "music/telltale", new StaticMessage("TellTale"), new Color(121, 100, 186), new Color(183, 232, 245), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        IcyRuse = MusicRegistry.registerMusic("icyruse", "music/icyruse", new StaticMessage("IcyRuse"), new Color(138, 193, 150), new Color(183, 232, 245), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        IceStar = MusicRegistry.registerMusic("icestar", "music/icestar", new StaticMessage("IceStar"), new Color(20, 118, 192), new Color(183, 232, 245), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        EyesOfTheDesert = MusicRegistry.registerMusic("eyesofthedesert", "music/eyesofthedesert", new StaticMessage("EyesOfTheDesert"), new Color(120, 158, 36), new Color(150, 154, 38), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Rialto = MusicRegistry.registerMusic("rialto", "music/rialto", new StaticMessage("Rialto"), new Color(81, 101, 174), new Color(150, 154, 38), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        SilverLake = MusicRegistry.registerMusic("silverlake", "music/silverlake", new StaticMessage("SilverLake"), new Color(126, 97, 68), new Color(150, 154, 38), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        ByTheField = MusicRegistry.registerMusic("bythefield", "music/bythefield", new StaticMessage("ByTheField"), new Color(178, 144, 98), new Color(232, 203, 130), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        SunStones = MusicRegistry.registerMusic("sunstones", "music/sunstones", new StaticMessage("SunStones"), new Color(100, 101, 157), new Color(232, 203, 130), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        CaravanTusks = MusicRegistry.registerMusic("caravantusks", "music/caravantusks", new StaticMessage("CaravanTusks"), new Color(172, 111, 110), new Color(232, 203, 130), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Away = MusicRegistry.registerMusic("away", "music/away", new StaticMessage("Away"), new Color(255, 177, 8), new Color(156, 51, 39), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Kronos = MusicRegistry.registerMusic("kronos", "music/kronos", new StaticMessage("Kronos"), new Color(169, 150, 236), new Color(121, 100, 186), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        LostGrip = MusicRegistry.registerMusic("lostgrip", "music/lostgrip", new StaticMessage("LostGrip"), new Color(191, 90, 62), new Color(255, 177, 8), new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        ElekTrak = MusicRegistry.registerMusic("elektrak", "music/elektrak", new StaticMessage("ElekTrak"), new Color(81, 136, 34), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        TheControlRoom = MusicRegistry.registerMusic("thecontrolroom", "music/thecontrolroom", new StaticMessage("TheControlRoom"), new Color(81, 101, 174), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        AirlockFailure = MusicRegistry.registerMusic("airlockfailure", "music/airlockfailure", new StaticMessage("AirlockFailure"), new Color(149, 133, 241), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        KonsoleGlitch = MusicRegistry.registerMusic("konsoleglitch", "music/konsoleglitch", new StaticMessage("KonsoleGlitch"), new Color(150, 154, 38), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Beatdown = MusicRegistry.registerMusic("beatdown", "music/beatdown", new StaticMessage("Beatdown"), new Color(97, 115, 8), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Siege = MusicRegistry.registerMusic("siege", "music/siege", new StaticMessage("Siege"), new Color(255, 177, 8), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Halodrome = MusicRegistry.registerMusic("halodrome", "music/halodrome", new StaticMessage("Halodrome"), new Color(65, 116, 85), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Millenium = MusicRegistry.registerMusic("millenium", "music/millenium", new StaticMessage("Millenium"), new Color(183, 232, 245), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
        Kandiru = MusicRegistry.registerMusic("kandiru", "music/kandiru", new StaticMessage("Kandiru"), new Color(232, 203, 130), new Color(156, 51, 39), 1000, 4000, new LocalMessage("itemtooltip", "fromlegacymusic")).setVolumeModifier(oldMusicVolumeModifier);
    }

    @Override
    protected void onRegister(MusicRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static GameMusic registerMusic(String stringID, String filePath, GameMessage trackName, Color color1, Color color2, GameMessage optionalTooltip) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register music");
        }
        return MusicRegistry.instance.registerObj((String)stringID, new MusicRegistryElement((GameMusic)new GameMusic((String)filePath, (GameMessage)trackName, (Color)color1, (Color)color2, (GameMessage)optionalTooltip))).music;
    }

    public static GameMusic registerMusic(String stringID, String filePath, GameMessage trackName, Color color1, Color color2, int fadeInMillis, int fadeOutMillis, GameMessage optionalTooltip) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register music");
        }
        return MusicRegistry.instance.registerObj((String)stringID, new MusicRegistryElement((GameMusic)new GameMusic((String)filePath, (GameMessage)trackName, (Color)color1, (Color)color2, (int)fadeInMillis, (int)fadeOutMillis, (GameMessage)optionalTooltip))).music;
    }

    public static GameMusic registerStringsMusic(String stringID, String filePath, GameMessage trackName, Color color1, Color color2, GameMessage optionalTooltip) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register music");
        }
        return MusicRegistry.instance.registerObj((String)stringID, new MusicRegistryElement((GameMusic)new StringsCustomTextureGameMusic((String)filePath, (GameMessage)trackName, (Color)color1, (Color)color2, (GameMessage)optionalTooltip))).music;
    }

    public static List<GameMusic> getMusic() {
        return instance.streamElements().map(e -> e.music).collect(Collectors.toList());
    }

    public static GameMusic getMusic(String stringID) {
        return MusicRegistry.getMusic(MusicRegistry.getMusicID(stringID));
    }

    public static GameMusic getMusic(int id) {
        return ((MusicRegistryElement)MusicRegistry.instance.getElement((int)id)).music;
    }

    public static int getMusicID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getMusicIDRaw(String stringID) throws NoSuchElementException {
        return instance.getElementIDRaw(stringID);
    }

    public static String getMusicStringID(int id) {
        return instance.getElementStringID(id);
    }

    static {
        instance = new MusicRegistry();
    }

    protected static class MusicRegistryElement
    implements IDDataContainer {
        public final GameMusic music;

        public MusicRegistryElement(GameMusic music) {
            this.music = music;
        }

        @Override
        public IDData getIDData() {
            return this.music.idData;
        }
    }
}

