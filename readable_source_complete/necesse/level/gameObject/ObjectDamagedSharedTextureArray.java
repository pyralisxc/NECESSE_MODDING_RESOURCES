/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.util.HashMap;
import necesse.entity.DamagedObjectEntity;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.multiTile.MultiTile;

public class ObjectDamagedSharedTextureArray {
    protected static HashMap<String, ObjectDamagedSharedTextureArray> loadedTextures = new HashMap();
    protected GameTextureSection[] textures;

    public ObjectDamagedSharedTextureArray(GameTextureSection[] textures) {
        this.textures = textures;
    }

    public ObjectDamagedSharedTextureArray(SharedGameTexture sharedTexture, ObjectDamagedTextureArray array) {
        ObjectDamagedSharedTextureArray cache;
        if (array.hash != null && (cache = loadedTextures.get(array.hash)) != null) {
            this.textures = cache.textures;
            return;
        }
        this.textures = new GameTextureSection[array.textures.length];
        for (int i = 0; i < this.textures.length; ++i) {
            this.textures[i] = sharedTexture.addTexture(array.textures[i]);
        }
        if (array.hash != null) {
            loadedTextures.put(array.hash, this);
        }
    }

    public final GameTextureSection getDamagedTexture(GameObject object, Level level, int tileX, int tileY) {
        return this.getDamagedTexture(object, level, 0, tileX, tileY);
    }

    public GameTextureSection getDamagedTexture(GameObject object, Level level, int layerID, int tileX, int tileY) {
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

    public GameTextureSection getDamagedTexture(float damagePercent) {
        if (damagePercent <= 0.0f) {
            return this.textures[0];
        }
        int sprite = Math.min((int)(damagePercent * (float)this.textures.length), this.textures.length - 1);
        return this.textures[sprite];
    }
}

