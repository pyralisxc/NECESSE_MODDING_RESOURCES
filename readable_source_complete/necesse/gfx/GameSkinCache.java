/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import necesse.engine.GameCache;
import necesse.gfx.AbstractGameTextureCache;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.res.ResourceEncoder;

public class GameSkinCache
extends AbstractGameTextureCache {
    public final String path;
    protected HashSet<String> queriedKeys = new HashSet();
    protected HashMap<String, AbstractGameTextureCache.Element> loaded = new HashMap();
    protected Map<String, AbstractGameTextureCache.Element> synchronizedMap = new HashMap<String, AbstractGameTextureCache.Element>();
    protected boolean isDirty = false;

    public GameSkinCache(String path) {
        this.path = path;
    }

    public void loadCache() {
        File outsideFile = GameCache.getCacheFile(this.path);
        if (outsideFile.exists()) {
            try {
                this.loaded = GameCache.getObject(this.path, HashMap.class);
            }
            catch (Exception e) {
                this.loaded = null;
            }
        }
        if (this.loaded == null) {
            try {
                InputStream inputStream = ResourceEncoder.getResourceInputStream(this.path + ".data");
                this.loaded = GameCache.getObjectFromStream(inputStream, HashMap.class);
                if (this.loaded == null) {
                    this.loaded = new HashMap();
                }
            }
            catch (Exception e) {
                this.loaded = new HashMap();
            }
        }
        this.synchronizedMap = Collections.synchronizedMap(this.loaded);
    }

    public void saveCache() {
        HashMap<String, AbstractGameTextureCache.Element> newMap = new HashMap<String, AbstractGameTextureCache.Element>();
        for (String key : this.queriedKeys) {
            newMap.put(key, this.loaded.remove(key));
        }
        if (!this.loaded.isEmpty() || this.isDirty) {
            GameCache.cacheObject(newMap, this.path);
        }
        this.loaded = newMap;
        this.synchronizedMap = Collections.synchronizedMap(this.loaded);
        this.isDirty = false;
    }

    @Override
    public void set(String key, int hash, GameTexture texture) {
        this.queriedKeys.add(key);
        this.isDirty = true;
        this.synchronizedMap.put(key, new AbstractGameTextureCache.Element(hash, texture.getData()));
    }

    @Override
    public AbstractGameTextureCache.Element get(String key) {
        this.queriedKeys.add(key);
        return this.synchronizedMap.get(key);
    }
}

