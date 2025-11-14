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

public class RubySetBonusBuff
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
            owner.getLevel().entityManager.addParticle(owner, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(255, 125, 175)).givesLight(247.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
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
        int additionalSummons = this.getAdditionalSummonsCount(owner);
        buff.setModifier(BuffModifiers.MAX_SUMMONS, additionalSummons);
        if (owner.isPlayer && additionalSummons >= 4 && (player = (PlayerMob)owner).isServerClient() && (client = player.getServerClient()).achievementsLoaded()) {
            client.achievements().CRYSTALLIZED.markCompleted(client);
        }
    }

    public int getAdditionalSummonsCount(Mob owner) {
        return (int)Math.floor((double)owner.getMaxResilience() / 100.0);
    }

    @Override
    public int getStacksDisplayCount(ActiveBuff buff) {
        return this.getAdditionalSummonsCount(buff.owner);
    }

    @Override
    public GameTexture getDrawIcon(ActiveBuff buff) {
        int additionalSummons = this.getAdditionalSummonsCount(buff.owner);
        if (additionalSummons <= 1) {
            return this.blueTracker;
        }
        if (additionalSummons == 2) {
            return this.greenTracker;
        }
        if (additionalSummons == 3) {
            return this.purpleTracker;
        }
        return this.orangeTracker;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        try {
            this.blueTracker = GameTexture.fromFileRaw("buffs/rubybluesummontracker");
            this.greenTracker = GameTexture.fromFileRaw("buffs/rubygreensummontracker");
            this.purpleTracker = GameTexture.fromFileRaw("buffs/rubypurplesummontracker");
            this.orangeTracker = GameTexture.fromFileRaw("buffs/rubyorangeummontracker");
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
            tooltips.add(Localization.translate("itemtooltip", "rubyset"), 400);
        } else {
            int additionalSummons = this.getAdditionalSummonsCount(ab.owner);
            String tooltipText = additionalSummons <= 1 ? Localization.translate("itemtooltip", "rubybluetext", "summoncount", (Object)additionalSummons) : (additionalSummons == 2 ? Localization.translate("itemtooltip", "rubygreentext", "summoncount", (Object)additionalSummons) : (additionalSummons == 3 ? Localization.translate("itemtooltip", "rubypurpletext", "summoncount", (Object)additionalSummons) : Localization.translate("itemtooltip", "rubyorangetext", "summoncount", (Object)additionalSummons)));
            tooltips.add(tooltipText, 300);
        }
        return tooltips;
    }
}

