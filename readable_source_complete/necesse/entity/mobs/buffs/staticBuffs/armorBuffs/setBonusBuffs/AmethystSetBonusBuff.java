/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public class AmethystSetBonusBuff
extends SetBonusBuff {
    private GameTexture blueTracker;
    private GameTexture greenTracker;
    private GameTexture purpleTracker;
    private GameTexture orangeTracker;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.canCancel = false;
        this.isVisible = true;
        this.isPassive = true;
        if (buff.owner.isClient() && !buff.getGndData().getBoolean("tooltipInit")) {
            this.spawnSetAssembledParticles(buff);
        }
    }

    private void spawnSetAssembledParticles(ActiveBuff buff) {
        int particleCount = 25;
        Mob owner = buff.owner;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 360.0f / (float)particleCount;
        for (int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
            owner.getLevel().entityManager.addParticle(owner, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(184, 174, 255)).givesLight(247.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    public void updateModifiers(ActiveBuff buff) {
        ServerClient client;
        PlayerMob player;
        Mob owner = buff.owner;
        float updatedDamageBuff = this.getAddedDamageFromResilience(owner);
        buff.setModifier(BuffModifiers.ALL_DAMAGE, Float.valueOf(updatedDamageBuff));
        if (owner.isPlayer && owner.getMaxResilience() > 400 && (player = (PlayerMob)owner).isServerClient() && (client = player.getServerClient()).achievementsLoaded()) {
            client.achievements().CRYSTALLIZED.markCompleted(client);
        }
    }

    public float getAddedDamageFromResilience(Mob owner) {
        return (float)owner.getMaxResilience() / 10.0f / 100.0f;
    }

    @Override
    public GameTexture getDrawIcon(ActiveBuff buff) {
        Mob owner = buff.owner;
        if (owner.getMaxResilience() < 100) {
            return this.blueTracker;
        }
        if (owner.getMaxResilience() < 200) {
            return this.greenTracker;
        }
        if (owner.getMaxResilience() < 400) {
            return this.purpleTracker;
        }
        return this.orangeTracker;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        try {
            this.blueTracker = GameTexture.fromFileRaw("buffs/amethystbluedamagetracker");
            this.greenTracker = GameTexture.fromFileRaw("buffs/amethystgreendamagetracker");
            this.purpleTracker = GameTexture.fromFileRaw("buffs/amethystpurpledamagetracker");
            this.orangeTracker = GameTexture.fromFileRaw("buffs/amethystorangedamagetracker");
        }
        catch (FileNotFoundException e) {
            this.blueTracker = GameTexture.fromFile("buffs/unknown");
            this.greenTracker = GameTexture.fromFile("buffs/unknown");
            this.purpleTracker = GameTexture.fromFile("buffs/unknown");
            this.orangeTracker = GameTexture.fromFile("buffs/unknown");
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        if (blackboard.get(InventoryItem.class, "setItem") != null) {
            tooltips.add(Localization.translate("itemtooltip", "amethystset"), 400);
        } else {
            Mob owner = ab.owner;
            int damageIncrease = (int)Math.floor(this.getAddedDamageFromResilience(owner) * 100.0f);
            String tooltipText = owner.getMaxResilience() <= 100 ? Localization.translate("itemtooltip", "amethystbluetext", "damage", (Object)damageIncrease) : (owner.getMaxResilience() <= 200 ? Localization.translate("itemtooltip", "amethystgreentext", "damage", (Object)damageIncrease) : (owner.getMaxResilience() <= 400 ? Localization.translate("itemtooltip", "amethystpurpletext", "damage", (Object)damageIncrease) : Localization.translate("itemtooltip", "amethystorangetext", "damage", (Object)damageIncrease)));
            tooltips.add(tooltipText, 300);
        }
        return tooltips;
    }
}

