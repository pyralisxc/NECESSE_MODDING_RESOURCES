/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle$GType
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.CollisionFilter
 *  necesse.level.maps.Level
 *  org.jetbrains.annotations.NotNull
 */
package aphorea.utils.area;

import aphorea.utils.area.AphAreaType;
import aphorea.utils.magichealing.AphMagicHealing;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;

public class AphArea {
    public float range;
    public float antRange;
    public float currentRange;
    public Color[] colors;
    public int position;
    public Set<AphAreaType> areaTypes = new HashSet<AphAreaType>();
    public int buffDuration = 1000;
    public int debuffDuration = 1000;
    public GameDamage areaDamage;
    public int areaHealing;
    public String[] buffs;
    public String[] debuffs;
    public boolean directExecuteHealing = false;
    public boolean onlyVision = true;
    public boolean ignoreLight = true;
    ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    public static int lateralBorderReduction = 10;

    public AphArea(float range, Color ... colors) {
        this.range = range;
        this.colors = colors;
    }

    public AphArea(float range, float alpha, Color ... colors) {
        this(range, AphArea.adjustAlpha(alpha, colors));
    }

    @NotNull
    private static Color[] adjustAlpha(float alpha, Color ... colors) {
        Color[] adjustedColors = new Color[colors.length];
        for (int i = 0; i < colors.length; ++i) {
            Color original = colors[i];
            adjustedColors[i] = new Color(original.getRed(), original.getGreen(), original.getBlue(), (int)(alpha * 255.0f));
        }
        return adjustedColors;
    }

    public AphArea setDamageArea(GameDamage damage) {
        this.areaTypes.add(AphAreaType.DAMAGE);
        this.areaDamage = damage;
        return this;
    }

    public AphArea setHealingArea(int healing) {
        this.areaTypes.add(AphAreaType.HEALING);
        this.areaHealing = healing;
        return this;
    }

    public AphArea setBuffArea(int duration, String ... buffs) {
        this.areaTypes.add(AphAreaType.BUFF);
        this.buffs = buffs;
        this.buffDuration = duration;
        return this;
    }

    public AphArea setDebuffArea(int duration, String ... debuffs) {
        this.areaTypes.add(AphAreaType.DEBUFF);
        this.debuffs = debuffs;
        this.debuffDuration = duration;
        return this;
    }

    public AphArea setDirectExecuteHealing(boolean directExecuteHealing) {
        this.directExecuteHealing = directExecuteHealing;
        return this;
    }

    public AphArea setOnlyVision(boolean onlyVision) {
        this.onlyVision = onlyVision;
        return this;
    }

    public AphArea setIgnoreLight(boolean ignoreLight) {
        this.ignoreLight = ignoreLight;
        return this;
    }

    public GameDamage getDamage() {
        return this.areaDamage;
    }

    public int getHealing() {
        return this.areaHealing;
    }

    public void executeServer(Mob attacker, @NotNull Mob target, float x, float y, float modRange, InventoryItem item, ToolItem toolItem) {
        float distance;
        if (this.position == 0 == AphArea.isCenter(attacker, target, distance = target.getDistance(x, y)) || this.inRange(distance, modRange) && AphArea.inVision(target, x, y)) {
            if (this.isDamageArea() && this.canDamageTarget(attacker, target)) {
                this.applyDamage(attacker, target);
            }
            if (this.isHealingArea() && this.canHealTarget(attacker, target)) {
                this.applyHealth(attacker, target, item, toolItem);
            }
            if (this.isBuffArea() && this.canBuffTarget(attacker, target)) {
                this.applyBuffs(attacker, target);
            }
            if (this.isDebuffArea() && this.canDebuffTarget(attacker, target)) {
                this.applyDebuffs(attacker, target);
            }
        }
    }

    public boolean isDamageArea() {
        return this.areaTypes.contains((Object)AphAreaType.DAMAGE);
    }

    public boolean canDamageTarget(Mob attacker, @NotNull Mob target) {
        return target != attacker && AphArea.canAreaAttack(attacker, target);
    }

    public void applyDamage(Mob attacker, @NotNull Mob target) {
        target.isServerHit(this.areaDamage, target.x - attacker.x, target.y - attacker.y, 0.0f, (Attacker)attacker);
    }

    public boolean isHealingArea() {
        return this.areaTypes.contains((Object)AphAreaType.HEALING);
    }

    public boolean canHealTarget(Mob attacker, @NotNull Mob target) {
        return target == attacker || AphMagicHealing.canHealMob(attacker, target);
    }

    public void applyHealth(Mob attacker, @NotNull Mob target, InventoryItem item, ToolItem toolItem) {
        if (this.directExecuteHealing) {
            AphMagicHealing.healMobExecute(attacker, target, this.areaHealing, item, toolItem);
        } else {
            AphMagicHealing.healMob(attacker, target, this.areaHealing, item, toolItem);
        }
    }

    public boolean isBuffArea() {
        return this.areaTypes.contains((Object)AphAreaType.BUFF);
    }

    public boolean canBuffTarget(Mob attacker, @NotNull Mob target) {
        return target == attacker || target.isSameTeam(attacker);
    }

    public void applyBuffs(Mob attacker, @NotNull Mob target) {
        Arrays.stream(this.buffs).forEach(buffID -> target.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)buffID), target, this.buffDuration, (Attacker)attacker), true));
    }

    public boolean isDebuffArea() {
        return this.areaTypes.contains((Object)AphAreaType.DEBUFF);
    }

    public boolean canDebuffTarget(Mob attacker, @NotNull Mob target) {
        return target != attacker && AphArea.canAreaAttack(attacker, target);
    }

    public void applyDebuffs(Mob attacker, @NotNull Mob target) {
        Arrays.stream(this.debuffs).forEach(debuffID -> target.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)debuffID), target, this.debuffDuration, (Attacker)attacker), true));
    }

    public static boolean isCenter(Mob attacker, Mob target, float distance) {
        return attacker == target && distance == 0.0f;
    }

    public boolean inRange(float distance, float modRange) {
        return distance <= this.range * modRange && distance > this.antRange * modRange;
    }

    public static boolean inVision(@NotNull Mob target, float x, float y) {
        return !target.getLevel().collides((Line2D)new Line2D.Float(x, y, target.x, target.y), new CollisionFilter().projectileCollision());
    }

    public static boolean canAreaAttack(Mob attacker, @NotNull Mob target) {
        return target.canBeTargeted(attacker, attacker.isPlayer ? ((PlayerMob)attacker).getNetworkClient() : null);
    }

    public void showParticles(Level level, float x, float y, Color[] forcedColors, float rangeModifier, float borderParticleModifier, float innerParticleModifier, int particleTime) {
        int range = Math.round(this.range * rangeModifier);
        int antRange = Math.round(this.antRange * rangeModifier);
        float[] rays = this.onlyVision ? AphArea.getRays(level, x, y, range, new CollisionFilter().projectileCollision()) : AphArea.getFullyRays(range);
        for (int i = 0; i < rays.length; ++i) {
            float dy;
            float dx;
            float rayDistance = rays[i];
            if (!(rayDistance > (float)antRange) || this.colors == null && forcedColors == null) continue;
            float trueRange = Math.min((float)range, rayDistance);
            float angle = (float)(Math.PI * 2 * (double)i / (double)rays.length);
            if (GameRandom.globalRandom.getChance(0.25f * borderParticleModifier)) {
                float dx2 = (float)Math.cos(angle) * trueRange;
                float dy2 = (float)Math.sin(angle) * trueRange;
                level.entityManager.addParticle(x + dx2, y + dy2, this.particleTypeSwitcher.next()).movesFriction((float)GameRandom.globalRandom.getIntBetween(-5, 5), (float)GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05f, 0.1f)).color(this.getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0.0f, 3.0f), GameRandom.globalRandom.getFloatBetween(5.0f, 10.0f)).ignoreLight(this.ignoreLight).lifeTime(particleTime);
            }
            float innerRange = trueRange;
            float neighbourRay = AphArea.getNeighbourRay(i, rays);
            float neighbourRange = Math.max((float)antRange, Math.min((float)range, neighbourRay));
            if (neighbourRange < trueRange) {
                int borderRange = (int)(trueRange - neighbourRange);
                innerRange -= (float)borderRange;
                for (int j = 0; j < borderRange / lateralBorderReduction; ++j) {
                    if (!GameRandom.globalRandom.getChance(borderParticleModifier)) continue;
                    dx = (float)Math.cos(angle) * (neighbourRange + (float)(j * lateralBorderReduction));
                    dy = (float)Math.sin(angle) * (neighbourRange + (float)(j * lateralBorderReduction));
                    level.entityManager.addParticle(x + dx, y + dy, this.particleTypeSwitcher.next()).movesFriction((float)GameRandom.globalRandom.getIntBetween(-5, 5), (float)GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05f, 0.1f)).color(this.getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0.0f, 3.0f), GameRandom.globalRandom.getFloatBetween(5.0f, 10.0f)).ignoreLight(this.ignoreLight).lifeTime(particleTime);
                }
            }
            if (!(innerRange > (float)antRange) || !GameRandom.globalRandom.getChance(innerParticleModifier * ((innerRange - (float)antRange) / 2000.0f)) || !(0.1f * innerRange + (float)antRange < innerRange * 0.9f)) continue;
            float r = GameRandom.globalRandom.getFloatBetween(0.0f, 1.0f);
            float d = (innerRange - (float)antRange) * AphArea.easeOutQuad(r) * 0.8f + 0.1f + (float)antRange;
            dx = (float)Math.cos(angle) * d;
            dy = (float)Math.sin(angle) * d;
            level.entityManager.addParticle(x + dx, y + dy, this.particleTypeSwitcher.next()).movesFriction((float)GameRandom.globalRandom.getIntBetween(-5, 5), (float)GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05f, 0.1f)).color(this.getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0.0f, 3.0f), GameRandom.globalRandom.getFloatBetween(5.0f, 10.0f)).ignoreLight(this.ignoreLight).lifeTime(particleTime);
        }
    }

    public static float easeOutQuad(float x) {
        return 1.0f - (1.0f - x) * (1.0f - x);
    }

    public static float[] getRays(Level level, float x, float y, float range, CollisionFilter filter) {
        int raysCount = (int)(Math.PI * 2 * (double)range);
        float[] rays = new float[raysCount];
        for (int i = 0; i < raysCount; ++i) {
            float angle = (float)(Math.PI * 2 * (double)i / (double)raysCount);
            rays[i] = (float)GameUtils.castRay((Level)level, (double)((double)x), (double)((double)y), (double)(Math.cos((double)((double)angle)) * (double)range), (double)(Math.sin((double)((double)angle)) * (double)range), (double)((double)range), (int)0, (CollisionFilter)filter).totalDist;
        }
        return rays;
    }

    public static float[] getFullyRays(float range) {
        int raysCount = (int)(Math.PI * 2 * (double)range);
        float[] rays = new float[raysCount];
        Arrays.fill(rays, range);
        return rays;
    }

    public static float getNeighbourRay(int ray, float[] rays) {
        float antRay = rays[ray == 0 ? rays.length - 1 : ray - 1];
        float nextRay = rays[ray == rays.length - 1 ? 0 : ray + 1];
        return Math.min(antRay, nextRay);
    }

    public Color getColor(Color[] forcedColors) {
        return (Color)GameRandom.globalRandom.getOneOf((Object[])(forcedColors != null ? forcedColors : this.colors));
    }
}

