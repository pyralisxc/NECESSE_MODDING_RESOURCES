/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.AbstractGameTextureCache;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkin;
import necesse.gfx.GameSkinCache;
import necesse.gfx.GameSkinColors;
import necesse.gfx.GameSkinLoader;
import necesse.gfx.HumanGender;
import necesse.gfx.gameTexture.GameTexture;

public class GameEyes {
    public static boolean printDebugs = false;
    public static GameSkinColors colors;
    private static ArrayList<GameEyes> eyes;
    public final int eyeIndex;
    public final int weight;
    private final HumanGender gender;
    public ArrayList<GameTexture> openEyeColorTextures = new ArrayList();
    public ArrayList<GameTexture> openSkinColorTextures = new ArrayList();
    public ArrayList<GameTexture> closedEyeColorTextures = new ArrayList();
    public ArrayList<GameTexture> closedSkinColorTextures = new ArrayList();

    public static void loadEyeTypes() {
        eyes = new ArrayList();
        eyes.add(new GameEyes(1, 100, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(2, 100, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(3, 25, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(4, 50, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(5, 10, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(6, 0, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(7, 0, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(8, 0, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(9, 50, HumanGender.FEMALE));
        eyes.add(new GameEyes(10, 25, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(11, 25, HumanGender.NEUTRAL));
        eyes.add(new GameEyes(12, 10, HumanGender.NEUTRAL));
    }

    public static void loadEyeTextures(GameSkinLoader loader) {
        GameSkinCache cache = new GameSkinCache("version/eyes");
        cache.loadCache();
        GameEyes.loadEyeTextures(loader, cache, true);
        cache.saveCache();
    }

    public static void loadEyeTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal) {
        GameEyes.loadColors();
        GameTexture defaultClosedTexture = GameTexture.fromFile("player/eyes/eyes_closed", true);
        ArrayList<GameTexture> defaultClosedEyeColorTextures = new ArrayList<GameTexture>();
        ArrayList<GameTexture> defaultClosedSkinColorTextures = new ArrayList<GameTexture>();
        GameEyes.loadClosedColorTextures(loader, cache, makeFinal, -1, defaultClosedTexture, defaultClosedEyeColorTextures, defaultClosedSkinColorTextures);
        for (GameEyes eye : eyes) {
            eye.loadTextures(loader, cache, makeFinal, null, null, defaultClosedEyeColorTextures, defaultClosedSkinColorTextures);
        }
        loader.waitForCurrentTasks();
    }

    private static void loadColors() {
        colors = new GameSkinColors();
        String colorsPath = "player/eyes/eyecolors";
        try {
            GameTexture colorTexture = GameTexture.fromFileRaw(colorsPath, true);
            colors.addBaseColors(colorTexture, 0, 1, colorTexture.getWidth() - 1);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find eye colors texture file at " + colorsPath);
        }
    }

    private GameEyes(int eyeIndex, int weight, HumanGender gender) {
        this.eyeIndex = eyeIndex;
        this.weight = weight;
        this.gender = gender;
    }

    private void loadTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, ArrayList<GameTexture> defaultOpenEyeColorTextures, ArrayList<GameTexture> defaultOpenSkinColorTextures, ArrayList<GameTexture> defaultClosedEyeColorTextures, ArrayList<GameTexture> defaultClosedSkinColorTextures) {
        try {
            GameTexture openTexture = GameTexture.fromFileRaw("player/eyes/eyes" + this.eyeIndex, true);
            GameEyes.loadOpenColorTextures(loader, cache, makeFinal, this.eyeIndex, openTexture, this.openEyeColorTextures, this.openSkinColorTextures);
        }
        catch (FileNotFoundException e) {
            if (defaultOpenEyeColorTextures == null || defaultOpenSkinColorTextures == null) {
                GameEyes.loadOpenColorTextures(loader, cache, makeFinal, this.eyeIndex, GameResources.error, this.openEyeColorTextures, this.openSkinColorTextures);
            }
            this.openEyeColorTextures = defaultOpenEyeColorTextures;
            this.openSkinColorTextures = defaultOpenSkinColorTextures;
        }
        try {
            GameTexture closedTexture = GameTexture.fromFileRaw("player/eyes/eyes" + this.eyeIndex + "_closed", true);
            GameEyes.loadClosedColorTextures(loader, cache, makeFinal, this.eyeIndex, closedTexture, this.closedEyeColorTextures, this.closedSkinColorTextures);
        }
        catch (FileNotFoundException e) {
            if (defaultClosedEyeColorTextures == null || defaultClosedSkinColorTextures == null) {
                GameEyes.loadClosedColorTextures(loader, cache, makeFinal, this.eyeIndex, GameResources.error, this.closedEyeColorTextures, this.closedSkinColorTextures);
            }
            this.closedEyeColorTextures = defaultClosedEyeColorTextures;
            this.closedSkinColorTextures = defaultClosedSkinColorTextures;
        }
    }

    private static void loadOpenColorTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, int eyeIndex, GameTexture originalTexture, ArrayList<GameTexture> eyeColorTextures, ArrayList<GameTexture> skinColorTextures) {
        int i;
        for (i = 0; i < colors.getSize(); ++i) {
            GameEyes.loadTexture(loader, cache, makeFinal, eyeIndex, originalTexture, "eyesOpen", eyeColorTextures, i, colors, GameSkin.colors);
        }
        for (i = 0; i < GameSkin.colors.getSize(); ++i) {
            GameEyes.loadTexture(loader, cache, makeFinal, eyeIndex, originalTexture, "skinOpen", skinColorTextures, i, GameSkin.colors, colors);
        }
    }

    private static void loadClosedColorTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, int eyeIndex, GameTexture originalTexture, ArrayList<GameTexture> eyeColorTextures, ArrayList<GameTexture> skinColorTextures) {
        int i;
        for (i = 0; i < colors.getSize(); ++i) {
            GameEyes.loadTexture(loader, cache, makeFinal, eyeIndex, originalTexture, "eyesClosed", eyeColorTextures, i, colors, GameSkin.colors);
        }
        for (i = 0; i < GameSkin.colors.getSize(); ++i) {
            GameEyes.loadTexture(loader, cache, makeFinal, eyeIndex, originalTexture, "skinClosed", skinColorTextures, i, GameSkin.colors, colors);
        }
    }

    private static void loadTexture(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, int eyeIndex, GameTexture originalTexture, String cachePrefix, ArrayList<GameTexture> list, int index, GameSkinColors colors, GameSkinColors ... removeTones) {
        int removeTonesHash = Arrays.stream(removeTones).map(GameSkinColors::getTonesHash).collect(Collectors.toList()).hashCode();
        int hash = originalTexture.hashCode() + GameRandom.prime(15) * colors.getColorHash(index) + GameRandom.prime(42) * removeTonesHash;
        String cacheKey = cachePrefix + eyeIndex + "-" + index;
        AbstractGameTextureCache.Element element = cache.get(cacheKey);
        if (element != null && element.hash == hash) {
            try {
                GameTexture cachedTexture = new GameTexture("cachedEyes " + cacheKey, element.textureData);
                if (makeFinal) {
                    cachedTexture.makeFinal();
                }
                loader.addToList(list, index, cachedTexture);
                return;
            }
            catch (Exception e) {
                GameLog.warn.println("Could not load eyes cache for " + cacheKey);
            }
        } else if (printDebugs) {
            GameLog.debug.println("Detected invalid " + cacheKey);
        }
        loader.triggerFirstTimeSetup();
        if (printDebugs) {
            GameLog.debug.println("Generating new " + cacheKey);
        }
        GameTexture texture = new GameTexture(originalTexture);
        loader.submitTaskAddToList(list, index, null, () -> {
            HashSet<Color> excludes = new HashSet<Color>();
            colors.replaceColors(texture, index, excludes);
            colors.removeColors(texture, excludes, removeTones);
            texture.runPreAntialias(false);
            cache.set(cacheKey, hash, texture);
            return texture;
        }, makeFinal);
    }

    public <T> List<T> getOpenColorTextures(int eyeColor, int skinColor, boolean onlyHumanlike, Function<GameTexture, T> mapper) {
        GameTexture skinColorTexture = this.openSkinColorTextures.get(GameSkin.getSkinColorIndex(skinColor, onlyHumanlike));
        GameTexture eyeColorTexture = this.openEyeColorTextures.get(eyeColor % this.openEyeColorTextures.size());
        return Arrays.asList(mapper.apply(skinColorTexture), mapper.apply(eyeColorTexture));
    }

    public <T> List<T> getClosedColorTextures(int eyeColor, int skinColor, boolean onlyHumanlike, Function<GameTexture, T> mapper) {
        GameTexture skinColorTexture = this.closedSkinColorTextures.get(GameSkin.getSkinColorIndex(skinColor, onlyHumanlike));
        GameTexture eyeColorTexture = this.closedEyeColorTextures.get(eyeColor % this.closedEyeColorTextures.size());
        return Arrays.asList(mapper.apply(skinColorTexture), mapper.apply(eyeColorTexture));
    }

    public List<GameTexture> getOpenColorTextures(int eyeColor, int skinColor, boolean onlyHumanlike) {
        return this.getOpenColorTextures(eyeColor, skinColor, onlyHumanlike, t -> t);
    }

    public List<GameTexture> getClosedColorTextures(int eyeColor, int skinColor, boolean onlyHumanlike) {
        return this.getClosedColorTextures(eyeColor, skinColor, onlyHumanlike, t -> t);
    }

    public static int getTotalEyeTypes() {
        return eyes.size();
    }

    public static int getTotalColors() {
        return colors.getSize();
    }

    public static GameEyes getEyes(int eyeType) {
        return eyes.get(eyeType % eyes.size());
    }

    public static int getRandomEyeColor(GameRandom random) {
        if (colors == null || colors.getSize() <= 0) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < colors.getSize(); ++i) {
            ticketList.addObject(colors.getWeight(i), (Object)i);
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    public static int getRandomEyesBasedOnGender(GameRandom random, HumanGender gender) {
        if (eyes == null || eyes.isEmpty()) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < eyes.size(); ++i) {
            HumanGender eyeGender = GameEyes.eyes.get((int)i).gender;
            if (eyeGender != gender && eyeGender != HumanGender.NEUTRAL) continue;
            ticketList.addObject(GameEyes.eyes.get((int)i).weight, (Object)i);
        }
        if (ticketList.isEmpty()) {
            return random.nextInt();
        }
        return (Integer)ticketList.getRandomObject(random);
    }
}

