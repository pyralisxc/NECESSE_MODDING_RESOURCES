/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.AbstractGameTextureCache;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkinCache;
import necesse.gfx.GameSkinColors;
import necesse.gfx.GameSkinLoader;
import necesse.gfx.HumanGender;
import necesse.gfx.gameTexture.GameTexture;

public class GameHair {
    public static int COMMON_HAIR_COLOR_WEIGHT = 255;
    public static int UNCOMMON_HAIR_COLOR_WEIGHT = 153;
    public static int RARE_HAIR_COLOR_WEIGHT = 64;
    public static boolean printDebugs = false;
    public static GameSkinColors colors;
    private static ArrayList<GameHair> hairs;
    private static ArrayList<GameHair> facialFeatures;
    public final int hairIndex;
    public final int weight;
    private ArrayList<GameTexture> textures;
    private ArrayList<GameTexture> backTextures;
    private ArrayList<GameTexture> wigTextures;
    private final HumanGender gender;
    private final boolean isFacialFeature;

    public static void loadHairTypes() {
        hairs = new ArrayList();
        hairs.add(new GameHair(0, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(1, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(2, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(3, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(4, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(5, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(6, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(7, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(8, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(9, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(10, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(11, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(12, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(13, 50, HumanGender.FEMALE, false));
        hairs.add(new GameHair(14, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(15, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(16, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(17, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(18, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(19, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(20, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(21, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(22, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(23, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(24, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(25, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(26, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(27, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(28, 100, HumanGender.NEUTRAL, false));
        hairs.add(new GameHair(29, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(30, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(31, 100, HumanGender.FEMALE, false));
        hairs.add(new GameHair(32, 100, HumanGender.MALE, false));
        hairs.add(new GameHair(33, 100, HumanGender.FEMALE, false));
        facialFeatures = new ArrayList();
        facialFeatures.add(new GameHair(0, 500, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(1, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(2, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(3, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(4, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(5, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(6, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(7, 100, HumanGender.MALE, true));
        facialFeatures.add(new GameHair(8, 100, HumanGender.MALE, true));
    }

    public static void loadHairTextures(GameSkinLoader loader) {
        if (hairs == null) {
            throw new NullPointerException("Load hair before loading hair textures.");
        }
        GameSkinCache cache = new GameSkinCache("version/hairs");
        cache.loadCache();
        GameHair.loadHairTextures(loader, cache, true);
        cache.saveCache();
    }

    public static void loadHairTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal) {
        GameHair.loadColors();
        for (GameHair c : hairs) {
            c.loadTextures(loader, cache, makeFinal, "hair", "hair");
        }
        for (GameHair c : facialFeatures) {
            c.loadTextures(loader, cache, makeFinal, "facialfeature", "facialfeature");
        }
        loader.waitForCurrentTasks();
    }

    private static void loadColors() {
        colors = new GameSkinColors();
        String colorsPath = "player/hair/haircolors";
        try {
            GameTexture colorTexture = GameTexture.fromFileRaw(colorsPath, true);
            colors.addBaseColors(colorTexture, 0, 1, colorTexture.getWidth() - 1);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find hair colors texture file at " + colorsPath);
        }
    }

    public GameHair(int hairIndex, int weight, HumanGender gender, boolean isFacialFeature) {
        this.weight = weight;
        this.hairIndex = hairIndex;
        this.gender = gender;
        this.isFacialFeature = isFacialFeature;
    }

    public GameTexture getHairTexture(int color) {
        if (this.hairIndex == 0) {
            return null;
        }
        return this.textures.get(color % this.textures.size());
    }

    public GameTexture getBackHairTexture(int color) {
        if (this.hairIndex == 0 || this.backTextures == null) {
            return null;
        }
        return this.backTextures.get(color % this.backTextures.size());
    }

    public GameTexture getWigTexture(int color) {
        if (this.hairIndex == 0) {
            return this.wigTextures.get(0);
        }
        return this.wigTextures.get(color % this.wigTextures.size());
    }

    public static GameHair getHair(int id) {
        return hairs.get(id % hairs.size());
    }

    public static GameHair getFacialFeature(int id) {
        return facialFeatures.get(id % facialFeatures.size());
    }

    public static HumanGender getHairGender(int id) {
        return GameHair.hairs.get((int)(id % GameHair.hairs.size())).gender;
    }

    public static HumanGender getFacialFeatureGender(int id) {
        return GameHair.facialFeatures.get((int)(id % GameHair.facialFeatures.size())).gender;
    }

    public static ArrayList<Integer> getMaleHairIDs() {
        ArrayList<Integer> maleHairIDs = new ArrayList<Integer>();
        for (GameHair hair : hairs) {
            if (hair.gender != HumanGender.MALE) continue;
            maleHairIDs.add(hair.hairIndex);
        }
        return maleHairIDs;
    }

    public static ArrayList<Integer> getFemaleHairIDs() {
        ArrayList<Integer> femaleHairIDs = new ArrayList<Integer>();
        for (GameHair hair : hairs) {
            if (hair.gender != HumanGender.FEMALE) continue;
            femaleHairIDs.add(hair.hairIndex);
        }
        return femaleHairIDs;
    }

    public static int getTotalHair() {
        return hairs.size();
    }

    public static int getTotalFacialFeatures() {
        return facialFeatures.size();
    }

    public static int getTotalHairColors() {
        return colors.getSize();
    }

    public static int getRandomHairColor(GameRandom random) {
        if (colors == null || colors.getSize() <= 0) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < colors.getSize(); ++i) {
            ticketList.addObject(colors.getWeight(i), (Object)i);
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    public static int getRandomHairColorAboveColorWeight(GameRandom random, int weight) {
        if (colors == null || colors.getSize() <= 0) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < colors.getSize(); ++i) {
            if (colors.getWeight(i) < weight) continue;
            ticketList.addObject(colors.getWeight(i), (Object)i);
        }
        if (ticketList.isEmpty()) {
            return random.nextInt();
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    public static int getRandomHairColorAtSpecificWeight(GameRandom random, int weight) {
        if (colors == null || colors.getSize() <= 0) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < colors.getSize(); ++i) {
            if (colors.getWeight(i) != weight) continue;
            ticketList.addObject(colors.getWeight(i), (Object)i);
        }
        if (ticketList.isEmpty()) {
            return random.nextInt();
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    public static int getRandomHair(GameRandom random) {
        if (hairs == null || hairs.isEmpty()) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < hairs.size(); ++i) {
            ticketList.addObject(GameHair.hairs.get((int)i).weight, (Object)i);
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    public static int getRandomHairBasedOnGender(GameRandom random, HumanGender gender) {
        if (hairs == null || hairs.isEmpty()) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < hairs.size(); ++i) {
            HumanGender hairGender = GameHair.hairs.get((int)i).gender;
            if (hairGender != gender && hairGender != HumanGender.NEUTRAL) continue;
            ticketList.addObject(GameHair.hairs.get((int)i).weight, (Object)i);
        }
        if (ticketList.isEmpty()) {
            return random.nextInt();
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    public static int getRandomFacialFeature(GameRandom random) {
        if (facialFeatures == null || facialFeatures.isEmpty()) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < facialFeatures.size(); ++i) {
            ticketList.addObject(GameHair.facialFeatures.get((int)i).weight, (Object)i);
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    private void loadTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, String folderName, String hairPrefix) {
        this.loadFrontTextures(loader, cache, makeFinal, folderName, hairPrefix);
        this.loadBackTextures(loader, cache, makeFinal, folderName, hairPrefix);
        this.loadWigTextures(loader, cache, makeFinal, folderName, hairPrefix);
    }

    public void loadFrontTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, String folderName, String hairPrefix) {
        if (this.hairIndex == 0) {
            return;
        }
        GameTexture originalTexture = GameTexture.fromFile("player/" + folderName + "/" + hairPrefix + this.hairIndex, true);
        this.textures = new ArrayList();
        for (int i = 0; i < colors.getSize(); ++i) {
            int hash = originalTexture.hashCode() + GameRandom.prime(28) * colors.getColorHash(i);
            String cacheKey = (this.isFacialFeature ? "facial" : "") + "front" + this.hairIndex + "-" + i;
            AbstractGameTextureCache.Element element = cache.get(cacheKey);
            if (element != null && element.hash == hash) {
                try {
                    GameTexture cachedTexture = new GameTexture("cachedHair " + cacheKey, element.textureData);
                    if (makeFinal) {
                        cachedTexture.makeFinal();
                    }
                    loader.addToList(this.textures, i, cachedTexture);
                    continue;
                }
                catch (Exception e) {
                    GameLog.warn.println("Could not load hair cache for " + cacheKey);
                }
            } else if (printDebugs) {
                GameLog.debug.println("Detected invalid " + cacheKey);
            }
            loader.triggerFirstTimeSetup();
            if (printDebugs) {
                GameLog.debug.println("Generating new " + cacheKey);
            }
            GameTexture texture = new GameTexture(originalTexture);
            int finalI = i;
            loader.submitTaskAddToList(this.textures, i, null, () -> {
                colors.replaceColors(texture, finalI);
                texture.runPreAntialias(false);
                cache.set(cacheKey, hash, texture);
                return texture;
            }, makeFinal);
        }
    }

    public void loadBackTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, String folderName, String hairPrefix) {
        GameTexture originalTexture;
        if (this.hairIndex == 0) {
            return;
        }
        try {
            originalTexture = GameTexture.fromFileRaw("player/" + folderName + "/" + hairPrefix + this.hairIndex + "_back", true);
        }
        catch (FileNotFoundException e) {
            return;
        }
        this.backTextures = new ArrayList();
        for (int i = 0; i < colors.getSize(); ++i) {
            int hash = originalTexture.hashCode() + GameRandom.prime(32) * colors.getColorHash(i);
            String cacheKey = (this.isFacialFeature ? "facial" : "") + "back" + this.hairIndex + "-" + i;
            AbstractGameTextureCache.Element element = cache.get(cacheKey);
            if (element != null && element.hash == hash) {
                try {
                    GameTexture cachedTexture = new GameTexture("cachedHair " + cacheKey, element.textureData);
                    if (makeFinal) {
                        cachedTexture.makeFinal();
                    }
                    loader.addToList(this.backTextures, i, cachedTexture);
                    continue;
                }
                catch (Exception e) {
                    GameLog.warn.println("Could not load hair cache for " + cacheKey);
                }
            } else if (printDebugs) {
                GameLog.debug.println("Detected invalid " + cacheKey);
            }
            loader.triggerFirstTimeSetup();
            if (printDebugs) {
                GameLog.debug.println("Generating new " + cacheKey);
            }
            GameTexture texture = new GameTexture(originalTexture);
            int finalI = i;
            loader.submitTaskAddToList(this.backTextures, i, null, () -> {
                colors.replaceColors(texture, finalI);
                texture.runPreAntialias(false);
                cache.set(cacheKey, hash, texture);
                return texture;
            }, makeFinal);
        }
    }

    public void loadWigTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, String folderName, String hairPrefix) {
        this.wigTextures = new ArrayList();
        GameTexture wigsTexture = GameTexture.fromFile("player/" + folderName + "/wigs", true);
        GameTexture originalTexture = wigsTexture.getWidth() >= 32 * (this.hairIndex + 1) ? new GameTexture(wigsTexture, 32 * this.hairIndex, 0, 32, 32) : new GameTexture(GameResources.error);
        if (this.hairIndex == 0) {
            this.wigTextures.add(originalTexture);
            return;
        }
        this.wigTextures = new ArrayList();
        for (int i = 0; i < colors.getSize(); ++i) {
            int hash = originalTexture.hashCode() + GameRandom.prime(24) * colors.getColorHash(i);
            String cacheKey = (this.isFacialFeature ? "facial" : "") + "wig" + this.hairIndex + "-" + i;
            AbstractGameTextureCache.Element element = cache.get(cacheKey);
            if (element != null && element.hash == hash) {
                try {
                    GameTexture cachedTexture = new GameTexture("cachedHair " + cacheKey, element.textureData);
                    if (makeFinal) {
                        cachedTexture.makeFinal();
                    }
                    loader.addToList(this.wigTextures, i, cachedTexture);
                    continue;
                }
                catch (Exception e) {
                    GameLog.warn.println("Could not load hair cache for " + cacheKey);
                }
            } else if (printDebugs) {
                GameLog.debug.println("Detected invalid " + cacheKey);
            }
            loader.triggerFirstTimeSetup();
            if (printDebugs) {
                GameLog.debug.println("Generating new " + cacheKey);
            }
            GameTexture texture = new GameTexture(originalTexture);
            int finalI = i;
            loader.submitTaskAddToList(this.wigTextures, i, null, () -> {
                colors.replaceColors(texture, finalI);
                texture.runPreAntialias(false);
                cache.set(cacheKey, hash, texture);
                return texture;
            }, makeFinal);
        }
    }
}

