/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.AbstractGameTextureCache;
import necesse.gfx.GameSkinCache;
import necesse.gfx.GameSkinColors;
import necesse.gfx.GameSkinLoader;
import necesse.gfx.gameTexture.GameTexture;

public class GameSkin {
    public static boolean printDebugs = false;
    public static GameSkinColors colors;
    private static final ArrayList<GameSkin> skins;
    private static final int humanLikeSkinColors = 4;
    public final int colorIndex;
    public final boolean isHumanlike;
    public GameTexture head;
    public GameTexture body;
    public GameTexture leftArms;
    public GameTexture rightArms;
    public GameTexture feet;

    public static void loadSkinTextures(GameSkinLoader loader) {
        GameSkinCache cache = new GameSkinCache("version/skins");
        cache.loadCache();
        GameSkin.loadSkinTextures(loader, cache, true);
        cache.saveCache();
    }

    public static void loadSkinTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal) {
        GameSkin.loadColors();
        GameSkin.loadTextures(loader, cache, makeFinal);
        loader.waitForCurrentTasks();
    }

    private static void loadColors() {
        colors = new GameSkinColors();
        String colorsPath = "player/skin/skincolors";
        try {
            GameTexture colorTexture = GameTexture.fromFileRaw(colorsPath, true);
            colors.addBaseColors(colorTexture, 0, 1, colorTexture.getWidth() - 1);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find skin colors texture file at " + colorsPath);
        }
    }

    private static void loadTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal) {
        for (int i = 0; i < colors.getSize(); ++i) {
            GameSkin skin = new GameSkin(i, i < 4);
            skins.add(skin);
            skin.loadTextures(loader, cache, makeFinal, i);
        }
    }

    private GameSkin(int colorIndex, boolean isHumanlike) {
        this.colorIndex = colorIndex;
        this.isHumanlike = isHumanlike;
    }

    private void loadTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, int id) {
        this.loadRegularTextures(loader, cache, makeFinal, id, "head", texture -> {
            this.head = texture;
        });
        this.loadRegularTextures(loader, cache, makeFinal, id, "body", texture -> {
            this.body = texture;
        });
        this.loadRegularTextures(loader, cache, makeFinal, id, "arms_left", texture -> {
            this.leftArms = texture;
        });
        this.loadRegularTextures(loader, cache, makeFinal, id, "arms_right", texture -> {
            this.rightArms = texture;
        });
        this.loadRegularTextures(loader, cache, makeFinal, id, "feet", texture -> {
            this.feet = texture;
        });
    }

    public void loadRegularTextures(GameSkinLoader loader, AbstractGameTextureCache cache, boolean makeFinal, int id, String fileName, Consumer<GameTexture> whenDone) {
        GameTexture originalTexture = GameTexture.fromFile("player/skin/" + fileName, true);
        int hash = originalTexture.hashCode() + GameRandom.prime(10) * colors.getColorHash(this.colorIndex);
        String cacheKey = fileName + id;
        AbstractGameTextureCache.Element element = cache.get(cacheKey);
        if (element != null && element.hash == hash) {
            try {
                GameTexture cachedTexture = new GameTexture("cachedSkin " + cacheKey, element.textureData);
                if (makeFinal) {
                    cachedTexture.makeFinal();
                }
                whenDone.accept(cachedTexture);
                return;
            }
            catch (Exception e) {
                GameLog.warn.println("Could not load skin cache for " + cacheKey);
            }
        } else if (printDebugs) {
            GameLog.debug.println("Detected invalid " + cacheKey);
        }
        loader.triggerFirstTimeSetup();
        if (printDebugs) {
            GameLog.debug.println("Generating new " + cacheKey);
        }
        GameTexture texture = new GameTexture(originalTexture);
        loader.submitTask(null, () -> {
            colors.replaceColors(texture, this.colorIndex);
            texture.runPreAntialias(false);
            cache.set(cacheKey, hash, texture);
            whenDone.accept(texture);
            return texture;
        }, makeFinal);
    }

    public GameTexture getHeadTexture() {
        return this.head;
    }

    public GameTexture getBodyTexture() {
        return this.body;
    }

    public GameTexture getLeftArmsTexture() {
        return this.leftArms;
    }

    public GameTexture getRightArmsTexture() {
        return this.rightArms;
    }

    public GameTexture getFeetTexture() {
        return this.feet;
    }

    public static int getTotalSkins() {
        return skins.size();
    }

    public static GameSkin getSkin(int id, boolean onlyHumanlike) {
        return skins.get(GameSkin.getSkinColorIndex(id, onlyHumanlike));
    }

    public static int getSkinColorIndex(int id, boolean onlyHumanlike) {
        if (onlyHumanlike) {
            return id % Math.min(4, skins.size());
        }
        return id % skins.size();
    }

    public static int getRandomSkinColor(GameRandom random, boolean onlyHumanLike) {
        if (colors == null || colors.getSize() <= 0) {
            return random.nextInt();
        }
        TicketSystemList ticketList = new TicketSystemList();
        for (int i = 0; i < colors.getSize(); ++i) {
            if (i >= 4 && onlyHumanLike) continue;
            ticketList.addObject(colors.getWeight(i), (Object)i);
        }
        return (Integer)ticketList.getRandomObject(random);
    }

    static {
        skins = new ArrayList();
    }
}

