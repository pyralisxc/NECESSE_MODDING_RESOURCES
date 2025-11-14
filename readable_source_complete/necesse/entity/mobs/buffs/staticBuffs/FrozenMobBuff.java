/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import necesse.engine.CameraShakeValues;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ObjectValue;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FrozenEnemyShatterLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MobExtraDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.light.GameLight;

public class FrozenMobBuff
extends Buff
implements MobExtraDrawBuff {
    private static final Dimension[] TEXTURE_DIMENSIONS = new Dimension[]{new Dimension(32, 32), new Dimension(32, 64), new Dimension(64, 64), new Dimension(64, 96), new Dimension(96, 96)};
    public static int DELAY_TIME_PER_TILE = 150;
    public static int THAW_TIME_MIN = 3000;
    public static int THAW_TIME_MAX = 6000;
    public static float[] CRACK_SOUND_AT_PROGRESS = new float[]{0.2f, 0.4f, 0.6f, 0.8f};
    public static CameraShakeValues CRACK_SHAKE = new CameraShakeValues(200, 50, 1.5f, 1.5f, true);
    private static GameTexture[][] FROZEN_TEXTURES;
    private final GameRandom drawRandom;

    public FrozenMobBuff() {
        this.isVisible = true;
        this.isImportant = true;
        this.canCancel = false;
        this.drawRandom = new GameRandom();
    }

    @Override
    public void onOverridden(ActiveBuff buff, ActiveBuff other) {
        super.onOverridden(buff, other);
        other.getGndData().copyKeysToTarget(buff.getGndData(), "lastCrackSound", "shakeStartTime");
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.PARALYZED, true);
        buff.setModifier(BuffModifiers.GROUNDED, true);
        buff.getGndData().setLong("thawStartTime", buff.getStackTimes().getLast().startTime);
        buff.getGndData().setLong("thawTime", buff.getDuration() - 100);
    }

    @Override
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/frozensolid");
        }
        catch (FileNotFoundException e) {
            this.iconTexture = GameTexture.fromFile("buffs/unknown");
        }
        GameTexture overlay = GameTexture.fromFile("objects/breakobjectoverlay", true);
        FROZEN_TEXTURES = new GameTexture[TEXTURE_DIMENSIONS.length][0];
        for (int i = 0; i < TEXTURE_DIMENSIONS.length; ++i) {
            Dimension dimension = TEXTURE_DIMENSIONS[i];
            GameTexture texture = GameTexture.fromFile("particles/frozentomb" + dimension.width + "x" + dimension.height);
            FrozenMobBuff.FROZEN_TEXTURES[i] = ObjectDamagedTextureArray.applyOverlay(texture, overlay);
        }
    }

    public static int getAppropriateSpriteIndex(int width, int height) {
        Dimension best = null;
        int bestIndex = -1;
        for (int i = 0; i < TEXTURE_DIMENSIONS.length; ++i) {
            int nextSize;
            int bestSize;
            Dimension dimension = TEXTURE_DIMENSIONS[i];
            if (dimension.width < width || dimension.height < height || best != null && (bestSize = best.width * best.height) < (nextSize = dimension.width * dimension.height)) continue;
            best = dimension;
            bestIndex = i;
        }
        return bestIndex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private GameSprite getAppropriateSprite(int width, int height, int seed, float breakPercent) {
        int index = FrozenMobBuff.getAppropriateSpriteIndex(width, height);
        if (index != -1) {
            int sprite;
            Dimension dimension = TEXTURE_DIMENSIONS[index];
            GameTexture[] textures = FROZEN_TEXTURES[index];
            int variation = Math.min((int)(breakPercent * (float)textures.length), textures.length - 1);
            GameTexture texture = textures[variation];
            int sprites = Math.max(texture.getWidth() / (dimension.width + 32), 1);
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                sprite = this.drawRandom.seeded(seed).nextInt(sprites);
            }
            return new GameSprite(texture, sprite, 0, dimension.width + 32, dimension.height + 32, dimension.width + 32, dimension.height + 32);
        }
        return null;
    }

    public static GameTextureSection getAppropriateDebrisSprites(int width, int height) {
        int index = FrozenMobBuff.getAppropriateSpriteIndex(width, height);
        if (index != -1) {
            Dimension dimension = TEXTURE_DIMENSIONS[index];
            GameTexture texture = FROZEN_TEXTURES[index][0];
            int spriteStart = dimension.height + 32;
            int spriteHeight = spriteStart - texture.getHeight();
            return new GameTextureSection(texture).section(0, spriteHeight * 5, spriteStart, texture.getHeight());
        }
        return null;
    }

    public static GameTextureSection getAppropriateRandomDebrisSprite(int width, int height) {
        GameTextureSection sprites = FrozenMobBuff.getAppropriateDebrisSprites(width, height);
        if (sprites == null) {
            return null;
        }
        int spriteRes = sprites.getHeight();
        int sprite = GameRandom.globalRandom.nextInt(Math.max(sprites.getWidth() / spriteRes, 1));
        return sprites.sprite(sprite, 0, spriteRes);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        long startTime = buff.getGndData().getLong("thawStartTime");
        if (startTime != 0L) {
            long thawTime = startTime + (long)buff.getGndData().getInt("thawTime");
            if (buff.owner.getTime() >= thawTime) {
                this.onThawFinished(buff);
            }
        }
    }

    protected void onThawFinished(ActiveBuff activeBuff) {
        activeBuff.owner.getLevel().entityManager.addLevelEvent(new FrozenEnemyShatterLevelEvent(activeBuff.owner));
        activeBuff.owner.buffManager.removeBuff(activeBuff.buff, true);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.tickPlayCrack(buff);
        long startTime = buff.getGndData().getLong("thawStartTime");
        if (startTime != 0L && startTime <= buff.owner.getTime()) {
            buff.owner.getLevel().lightManager.refreshParticleLightFloat(buff.owner.x, buff.owner.y, 210.0f, 1.0f, 50);
        }
    }

    public void tickPlayCrack(ActiveBuff buff) {
        float lastCrackSound = buff.getGndData().getFloat("lastCrackSound", -1.0f);
        float nextCrackSound = -1.0f;
        for (float crackSoundAtProgress : CRACK_SOUND_AT_PROGRESS) {
            if (!(crackSoundAtProgress > lastCrackSound)) continue;
            nextCrackSound = crackSoundAtProgress;
            break;
        }
        if (nextCrackSound != -1.0f) {
            float breakPercent = 0.0f;
            long startTime = buff.getGndData().getLong("thawStartTime");
            if (startTime != 0L) {
                long thawProgressTime = buff.owner.getTime() - startTime;
                breakPercent = GameMath.limit((float)thawProgressTime / (float)buff.getGndData().getInt("thawTime"), 0.0f, 1.0f);
            }
            if (breakPercent > nextCrackSound) {
                GameSound sound = GameRandom.globalRandom.getOneOf(GameResources.iceCrack1, GameResources.iceCrack2, GameResources.iceCrack3);
                SoundManager.playSound(sound, (SoundEffect)SoundEffect.effect(buff.owner).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
                buff.getGndData().setFloat("lastCrackSound", breakPercent);
                buff.getGndData().setLong("shakeStartTime", buff.owner.getLocalTime());
            }
        }
    }

    protected void startThawMob(Mob mob, int delayTime, int nextThawTime) {
        ActiveBuff ab = mob.buffManager.getBuff(this);
        if (ab != null) {
            long thawStartTime = ab.getGndData().getLong("thawStartTime");
            if (thawStartTime == 0L) {
                if (nextThawTime == -1) {
                    nextThawTime = GameRandom.globalRandom.getIntBetween(THAW_TIME_MIN, THAW_TIME_MAX);
                }
                ab.getGndData().setLong("thawStartTime", (long)delayTime + mob.getTime());
                ab.getGndData().setLong("thawTime", nextThawTime);
            } else {
                long thawTime = ab.getGndData().getLong("thawTime");
                ab.getGndData().setLong("thawTime", thawTime - 1000L);
            }
            BuffManager.sendUpdatePacket(ab);
        }
    }

    protected void startThawMobAndFindNext(Mob baseMob, Mob targetMob, int maxTargets) {
        if (targetMob.isServer()) {
            this.startThawMob(targetMob, 0, -1);
            this.findAndThawNextTargets(baseMob, maxTargets);
        }
    }

    protected void findAndThawNextTargets(Mob from, int maxTargets) {
        from.getLevel().entityManager.mobs.streamInRegionsInRange(from.x, from.y, 384).filter(m -> !m.removed() && m != from).filter(m -> {
            ActiveBuff buff = m.buffManager.getBuff(this);
            return buff != null && buff.getGndData().getLong("thawStartTime") == 0L;
        }).sorted(Comparator.comparingDouble(m -> GameMath.diagonalMoveDistance(from.getX(), from.getY(), m.getX(), m.getY()))).limit(maxTargets).forEach(m -> this.startThawMobAndFindNext(from, (Mob)m, 1));
    }

    protected boolean findAndThawAll(Mob from) {
        LinkedList list = from.getLevel().entityManager.mobs.streamInRegionsInRange(from.x, from.y, 384).filter(m -> !m.removed()).filter(m -> {
            ActiveBuff buff = m.buffManager.getBuff(this);
            return buff != null && buff.getGndData().getLong("thawStartTime") == 0L;
        }).map(m -> new ObjectValue<Mob, Double>((Mob)m, m == from ? 0.0 : GameMath.diagonalMoveDistance(from.getX(), from.getY(), m.getX(), m.getY()))).sorted(Comparator.comparingDouble(o -> (Double)o.value)).collect(Collectors.toCollection(LinkedList::new));
        if (!list.isEmpty()) {
            boolean out = false;
            double first = (Double)((ObjectValue)list.getFirst()).value;
            double last = (Double)((ObjectValue)list.getLast()).value;
            for (ObjectValue o2 : list) {
                if (o2.object == from) {
                    out = true;
                }
                int delayTime = (int)((double)DELAY_TIME_PER_TILE * (Double)o2.value / 32.0);
                double distancePercent = first == last ? 0.0 : GameMath.clamp((Double)o2.value, first, last);
                this.startThawMob((Mob)o2.object, delayTime, (int)GameMath.lerp(distancePercent, (long)THAW_TIME_MIN, (long)THAW_TIME_MAX));
            }
            return out;
        }
        return false;
    }

    @Override
    public void addBackDrawOptions(ActiveBuff buff, LinkedList<DrawOptions> list, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public void addFrontDrawOptions(ActiveBuff buff, LinkedList<DrawOptions> list, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameSprite sprite;
        GameLight light = buff.owner.getLevel().getLightLevel(buff.owner);
        Rectangle selectBox = buff.owner.getMountUnionSelectBox(x, y);
        float breakPercent = 0.0f;
        long startTime = buff.getGndData().getLong("thawStartTime");
        if (startTime != 0L) {
            long thawProgressTime = buff.owner.getTime() - startTime;
            breakPercent = GameMath.limit((float)thawProgressTime / (float)buff.getGndData().getInt("thawTime"), 0.0f, 1.0f);
        }
        Point2D.Float shake = new Point2D.Float();
        long shakeStartTime = buff.getGndData().getLong("shakeStartTime");
        if (shakeStartTime != 0L) {
            shake = CRACK_SHAKE.getCurrentShake(shakeStartTime, buff.owner.getLocalTime());
        }
        if ((sprite = this.getAppropriateSprite(selectBox.width, selectBox.height, buff.owner.getUniqueID(), breakPercent)) != null) {
            int deltaWidth = sprite.width - selectBox.width;
            int deltaHeight = sprite.height - selectBox.height;
            int drawX = camera.getDrawX((float)selectBox.x + shake.x) - deltaWidth / 2;
            int drawY = camera.getDrawY((float)selectBox.y + shake.y) - deltaHeight + 18;
            TextureDrawOptionsEnd drawOptions = sprite.initDraw().light(light).pos(drawX, drawY);
            list.add(drawOptions);
        } else {
            int drawX = camera.getDrawX((float)selectBox.x + shake.x) - 5;
            int drawY = camera.getDrawY((float)selectBox.y + shake.y) - 10;
            TextureDrawOptionsEnd quadOptions = Renderer.initQuadDraw(selectBox.width + 10, selectBox.height + 10).colorLight(new Color(150, 150, 255, 150), light).pos(drawX, drawY);
            list.add(quadOptions);
        }
    }

    public static boolean isBuffValidForTarget(Mob target) {
        Rectangle selectBox = target.getSelectBox();
        return FrozenMobBuff.getAppropriateSpriteIndex(selectBox.width, selectBox.height) != -1;
    }
}

