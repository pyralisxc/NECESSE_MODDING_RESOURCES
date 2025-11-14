/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.HashMap;
import necesse.entity.DamagedObjectEntity;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.multiTile.MultiTile;

public class ObjectDamagedTextureArray {
    protected static HashMap<String, ObjectDamagedTextureArray> loadedTextures = new HashMap();
    protected GameTexture[] textures;
    protected String hash;

    public ObjectDamagedTextureArray(GameTexture[] textures, String hash) {
        this.textures = textures;
        this.hash = hash;
    }

    public static GameTexture[] applyOverlay(GameTexture texture, GameTexture overlay) {
        int overlayFrames = overlay.getWidth() / 32;
        GameTexture[] textures = new GameTexture[overlayFrames + 1];
        textures[0] = new GameTexture(texture).makeFinal();
        for (int i = 0; i < overlayFrames; ++i) {
            int currentWidth;
            GameTexture damagedTexture = new GameTexture(texture);
            int spriteX = 0;
            while ((currentWidth = Math.min(damagedTexture.getWidth() - 32 * spriteX, 32)) > 0) {
                int currentHeight;
                int spriteY = 0;
                while ((currentHeight = Math.min(damagedTexture.getHeight() - 32 * spriteY, 32)) > 0) {
                    damagedTexture.merge(overlay, spriteX * 32, spriteY * 32, i * 32, 0, currentWidth, currentHeight, new MergeFunction(){

                        @Override
                        public Color merge(Color currentColor, Color mergeColor) {
                            if (currentColor.getAlpha() == 0) {
                                return currentColor;
                            }
                            if (mergeColor.getAlpha() == 0) {
                                return currentColor;
                            }
                            Color merge = MergeFunction.NORMAL.merge(currentColor, mergeColor);
                            return new Color(merge.getRed(), merge.getGreen(), merge.getBlue(), currentColor.getAlpha());
                        }
                    });
                    ++spriteY;
                }
                ++spriteX;
            }
            damagedTexture.makeFinal();
            textures[i + 1] = damagedTexture;
        }
        texture.makeFinal();
        return textures;
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlay(GameObject object, GameTexture texture, GameTexture overlay, String hash) {
        ObjectDamagedTextureArray cached;
        if (!object.shouldGenerateDamageOverlayTextures()) {
            texture.makeFinal();
            return new SingleTexture(texture, hash);
        }
        if (hash != null && (cached = loadedTextures.get(hash)) != null) {
            return cached;
        }
        ObjectDamagedTextureArray out = new ObjectDamagedTextureArray(ObjectDamagedTextureArray.applyOverlay(texture, overlay), hash);
        if (hash != null) {
            loadedTextures.put(hash, out);
        }
        return out;
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlay(GameObject object, GameTexture texture, GameTexture overlay) {
        return ObjectDamagedTextureArray.loadAndApplyOverlay(object, texture, overlay, null);
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlay(GameObject object, GameTexture texture) {
        return ObjectDamagedTextureArray.loadAndApplyOverlay(object, texture, GameTexture.fromFile("objects/breakobjectoverlay", true));
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlay(GameObject object, String texturePath, String overlayPath) {
        String hash = ObjectDamagedTextureArray.getHash(texturePath, overlayPath);
        return ObjectDamagedTextureArray.loadAndApplyOverlay(object, GameTexture.fromFile(texturePath, true), GameTexture.fromFile(overlayPath, true), hash);
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlay(GameObject object, String texturePath) {
        return ObjectDamagedTextureArray.loadAndApplyOverlay(object, texturePath, "objects/breakobjectoverlay");
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlayRaw(GameObject object, String texturePath, String overlayPath) throws FileNotFoundException {
        String hash = ObjectDamagedTextureArray.getHash(texturePath, overlayPath);
        return ObjectDamagedTextureArray.loadAndApplyOverlay(object, GameTexture.fromFileRaw(texturePath, true), GameTexture.fromFile(overlayPath, true), hash);
    }

    public static ObjectDamagedTextureArray loadAndApplyOverlayRaw(GameObject object, String texturePath) throws FileNotFoundException {
        return ObjectDamagedTextureArray.loadAndApplyOverlayRaw(object, texturePath, "objects/breakobjectoverlay");
    }

    public static String getHash(String texturePath, String overlayPath) {
        return texturePath + "." + overlayPath;
    }

    public static String getHash(String texturePath) {
        return ObjectDamagedTextureArray.getHash(texturePath, "objects/breakobjectoverlay");
    }

    public final GameTexture getDamagedTexture(GameObject object, Level level, int tileX, int tileY) {
        return this.getDamagedTexture(object, level, 0, tileX, tileY);
    }

    public GameTexture getDamagedTexture(GameObject object, Level level, int layerID, int tileX, int tileY) {
        MultiTile multiTile = object.getMultiTile(level, layerID, tileX, tileY);
        LevelObject master = multiTile.getMasterLevelObject(level, layerID, tileX, tileY).orElse(null);
        DamagedObjectEntity damagedObjectEntity = master != null ? level.entityManager.getDamagedObjectEntity(master.tileX, master.tileY) : level.entityManager.getDamagedObjectEntity(tileX, tileY);
        int damage = level.getAppearedObjectDamage(object, layerID, tileX, tileY);
        if (damagedObjectEntity != null) {
            damage = Math.max(damagedObjectEntity.getObjectDamage(layerID), damage);
        }
        if (damage > 0) {
            float damagePercent = (float)damage / (float)object.objectHealth;
            return this.getDamagedTexture(damagePercent);
        }
        return this.getDamagedTexture(0.0f);
    }

    public GameTexture getDamagedTexture(float damagePercent) {
        if (damagePercent <= 0.0f) {
            return this.textures[0];
        }
        int sprite = Math.min((int)(damagePercent * (float)this.textures.length), this.textures.length - 1);
        return this.textures[sprite];
    }

    private static class SingleTexture
    extends ObjectDamagedTextureArray {
        protected GameTexture texture;

        public SingleTexture(GameTexture texture, String hash) {
            super(new GameTexture[]{texture}, hash);
            this.texture = texture;
        }

        @Override
        public GameTexture getDamagedTexture(GameObject object, Level level, int layerID, int tileX, int tileY) {
            return this.texture;
        }

        @Override
        public GameTexture getDamagedTexture(float damagePercent) {
            return this.texture;
        }
    }
}

