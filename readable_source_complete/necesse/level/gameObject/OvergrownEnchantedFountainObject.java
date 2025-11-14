/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.CavelingOasisFountainObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.gameObject.FountainObject;
import necesse.level.maps.Level;

public class OvergrownEnchantedFountainObject
extends FountainObject {
    protected OvergrownEnchantedFountainObject(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(texturePath, multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision);
        this.lightLevel = 100;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        GameRandom globalRandom = GameRandom.globalRandom;
        if (globalRandom.getChance(0.025f)) {
            int startHeight = 16 + globalRandom.nextInt(16);
            level.entityManager.addParticle(tileX * 32 + globalRandom.getIntBetween(4, 24), tileY * 32 + 8 + globalRandom.getIntBetween(0, 8), Particle.GType.COSMETIC).sprite(GameResources.magicSparkParticles.sprite(2, 0, 22)).heightMoves(startHeight, startHeight + 42).sizeFades(14, 14).movesConstant(globalRandom.getIntBetween(-3, 3), 0.0f).color(new Color(229, 245, 240)).lifeTime(3000);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (this.isMultiTileMaster()) {
            return new CavelingOasisFountainObjectEntity(level, x, y);
        }
        return null;
    }

    public static int[] registerFountain(String texturePath, boolean isObtainable) {
        int[] ids = new int[8];
        Rectangle collision = new Rectangle(6, 0, 116, 60);
        ids[0] = ObjectRegistry.registerObject(texturePath, new OvergrownEnchantedFountainObject(texturePath, 0, 0, 4, 2, ids, collision), 0.0f, isObtainable);
        ids[1] = ObjectRegistry.registerObject(texturePath + "2", new OvergrownEnchantedFountainObject(texturePath, 1, 0, 4, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject(texturePath + "3", new OvergrownEnchantedFountainObject(texturePath, 2, 0, 4, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject(texturePath + "4", new OvergrownEnchantedFountainObject(texturePath, 3, 0, 4, 2, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject(texturePath + "5", new OvergrownEnchantedFountainObject(texturePath, 0, 1, 4, 2, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject(texturePath + "6", new OvergrownEnchantedFountainObject(texturePath, 1, 1, 4, 2, ids, collision), 0.0f, false);
        ids[6] = ObjectRegistry.registerObject(texturePath + "7", new OvergrownEnchantedFountainObject(texturePath, 2, 1, 4, 2, ids, collision), 0.0f, false);
        ids[7] = ObjectRegistry.registerObject(texturePath + "8", new OvergrownEnchantedFountainObject(texturePath, 3, 1, 4, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

