/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.objectEntity.AnyLogFueledInventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEVicinityBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CampfireObjectEntity
extends AnyLogFueledInventoryObjectEntity
implements OEVicinityBuff {
    public final boolean allowSettlementStorage;
    protected SoundPlayer sizzleSoundPlayer;
    protected static final int sizzleSoundCooldownMin = 4000;
    protected static final int sizzleSoundCooldownMax = 10000;
    protected int sizzleSoundCooldown;
    protected int sizzleSoundTimer;

    public CampfireObjectEntity(Level level, String type, int x, int y, boolean alwaysOn, boolean allowSettlementStorage) {
        super(level, type, x, y, alwaysOn);
        this.allowSettlementStorage = allowSettlementStorage;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isFueled()) {
            this.tickVicinityBuff(this);
            this.sizzleSoundTimer += 50;
            if (this.sizzleSoundTimer >= this.sizzleSoundCooldown && this.getLevel().weatherLayer.isRaining()) {
                this.sizzleSoundPlayer = SoundManager.playSound(new SoundSettings(GameResources.dropSizzle).volume(0.2f).basePitch(0.9f).pitchVariance(0.05f).fallOffDistance(256), this);
                this.sizzleSoundTimer = 0;
                this.sizzleSoundCooldown = GameRandom.globalRandom.getIntBetween(4000, 10000);
                int spriteRes = 20;
                Color rainColor = new Color(60, 101, 236, 169);
                this.getLevel().entityManager.addParticle(ParticleOption.base((float)(this.tileX * 32) + (float)spriteRes * 0.5f + (float)GameRandom.globalRandom.getIntBetween(2, 11), (float)(this.tileY * 32) + (float)spriteRes * 0.5f + (float)GameRandom.globalRandom.getIntBetween(-4, 5)), Particle.GType.COSMETIC).lifeTime(600).sprite((options, lifeTime, timeAlive, lifePercent) -> {
                    int frames = GameResources.rainBlobParticle.getWidth() / spriteRes;
                    return options.add(GameResources.rainBlobParticle.sprite(Math.min((int)(lifePercent * (float)frames), frames - 1), 0, spriteRes));
                }).color(rainColor);
            }
        }
    }

    @Override
    protected void onRanOutOfFuel() {
        super.onRanOutOfFuel();
        SoundManager.playSound(new SoundSettings(GameResources.campfireSizzle).volume(0.8f), this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isFueled()) {
            this.tickVicinityBuff(this);
        }
    }

    @Override
    public Buff[] getBuffs() {
        return new Buff[]{BuffRegistry.CAMPFIRE};
    }

    @Override
    public int getBuffRange() {
        return 160;
    }

    @Override
    public boolean shouldBuffPlayers() {
        return true;
    }

    @Override
    public boolean shouldBuffMobs() {
        return false;
    }

    @Override
    public InventoryRange getSettlementStorage() {
        Inventory inventory;
        if (this.allowSettlementStorage && (inventory = this.getInventory()) != null) {
            return new InventoryRange(inventory);
        }
        return null;
    }

    @Override
    public boolean isSettlementStorageItemDisabled(Item item) {
        return !item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
    }
}

