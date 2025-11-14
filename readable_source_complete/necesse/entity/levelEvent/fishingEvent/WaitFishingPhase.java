/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.fishingEvent;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.input.Control;
import necesse.engine.network.packet.PacketFishingStatus;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.levelEvent.fishingEvent.FishingPhase;
import necesse.entity.levelEvent.fishingEvent.ReelFishingPhase;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FishingHookParticle;
import necesse.entity.particle.Particle;
import necesse.entity.trails.FishingTrail;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.FishItemInterface;
import necesse.level.maps.LevelTile;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;

public class WaitFishingPhase
extends FishingPhase {
    private final ArrayList<FishingLure> lures;
    private int tickCounter;
    private int lastSplashTick = -1;

    public WaitFishingPhase(FishingEvent event, Point[] hookPositions) {
        super(event);
        this.lures = new ArrayList(event.getLines());
        for (int i = 0; i < event.getLines(); ++i) {
            this.lures.add(new FishingLure(i, hookPositions[i]));
        }
    }

    @Override
    public void tickMovement(float delta) {
        if (this.event.isMine() && Control.MOUSE1.isPressed()) {
            this.event.level.getClient().network.sendPacket(PacketFishingStatus.getReelPacket(this.event));
            this.event.reel();
        }
        this.lures.forEach(FishingLure::updateLine);
    }

    @Override
    public void clientTick() {
        if (this.event.isReeled) {
            this.reel();
            return;
        }
        this.event.getFishingMob().showFishingWaitAnimation(this.event.fishingRod, this.event.getTarget().x, this.event.getTarget().y);
        ++this.tickCounter;
        this.lures.forEach(FishingLure::clientTick);
        if (this.tickCounter > 4800) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        if (this.event.isReeled) {
            this.reel();
            return;
        }
        this.event.getFishingMob().showFishingWaitAnimation(this.event.fishingRod, this.event.getTarget().x, this.event.getTarget().y);
        ++this.tickCounter;
        this.lures.forEach(FishingLure::serverTick);
        if (this.event.checkOutsideRange()) {
            return;
        }
        if (this.tickCounter > 4800) {
            this.over();
        }
    }

    @Override
    public void end() {
        this.lures.forEach(FishingLure::remove);
    }

    @Override
    public void over() {
        this.event.getFishingMob().stopFishing();
        this.lures.forEach(FishingLure::remove);
    }

    private int getNewCatchTick() {
        int rodPower = this.event.fishingRod.fishingPower;
        int baitPower = this.event.bait == null ? 0 : this.event.bait.fishingPower;
        int buffPower = this.event.getMob().buffManager.getModifier(BuffModifiers.FISHING_POWER);
        int totalPower = GameMath.limit(rodPower + baitPower + buffPower, 0, 100);
        float perc = Math.abs((float)totalPower / 100.0f - 1.0f);
        int maxTicks = (int)(500.0f * perc);
        return this.tickCounter + (maxTicks <= 0 ? 0 : GameRandom.globalRandom.nextInt(maxTicks)) + 10;
    }

    @Override
    public void addNewCatch(int lineIndex, int inTicks, InventoryItem item) {
        if (lineIndex >= 0 && lineIndex < this.lures.size()) {
            this.lures.get(lineIndex).addCatch(item, inTicks);
        } else {
            GameLog.warn.println("Received invalid new fishing catch index " + lineIndex + " out of " + this.lures.size());
        }
    }

    @Override
    public void reel() {
        this.event.setPhase(new ReelFishingPhase(this.event, this.lures));
    }

    @Override
    public int getTicksToNextCatch() {
        return this.lures.stream().mapToInt(FishingLure::getTicksToNextCatch).min().orElse(500);
    }

    public class FishingLure {
        public final int lineIndex;
        public FishingTrail line;
        public FishingHookParticle hookParticle;
        public final Point hookPosition;
        private InventoryItem caught;
        public int catchNum;
        public int catchSent;
        public int catchTick;

        public FishingLure(int lineIndex, Point hookPosition) {
            this.lineIndex = lineIndex;
            this.hookPosition = hookPosition;
            this.catchTick = !WaitFishingPhase.this.event.isClient() ? WaitFishingPhase.this.getNewCatchTick() : -1;
            ++this.catchNum;
            if (!WaitFishingPhase.this.event.level.isServer()) {
                this.hookParticle = new FishingHookParticle(WaitFishingPhase.this.event.level, (float)hookPosition.x, (float)hookPosition.y, WaitFishingPhase.this.event.fishingRod);
                WaitFishingPhase.this.event.level.entityManager.particles.add(this.hookParticle);
                this.line = new FishingTrail(WaitFishingPhase.this.event.getMob(), WaitFishingPhase.this.event.level, this.hookParticle, WaitFishingPhase.this.event.fishingRod);
                WaitFishingPhase.this.event.level.entityManager.addTrail(this.line);
            }
        }

        public void updateLine() {
            if (this.line != null) {
                this.line.update();
            }
        }

        public void clientTick() {
            if (this.hookParticle != null) {
                this.hookParticle.refreshSpawnTime();
            }
            if (this.caught != null) {
                if (WaitFishingPhase.this.tickCounter == this.catchTick) {
                    if (WaitFishingPhase.this.lastSplashTick != WaitFishingPhase.this.tickCounter) {
                        SoundManager.playSound(GameResources.splash, (SoundEffect)SoundEffect.effect(this.hookPosition.x, this.hookPosition.y));
                        WaitFishingPhase.this.lastSplashTick = WaitFishingPhase.this.tickCounter;
                    }
                    if (this.hookParticle != null) {
                        this.hookParticle.blob();
                        for (int i = 0; i < 5; ++i) {
                            WaitFishingPhase.this.event.level.entityManager.addParticle(this.hookParticle.x, this.hookParticle.y, Particle.GType.IMPORTANT_COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 6.0), -GameRandom.globalRandom.nextFloat() * 4.0f).color(new Color(89, 139, 224));
                        }
                    }
                }
                if (WaitFishingPhase.this.tickCounter - this.catchTick > WaitFishingPhase.this.event.fishingRod.reelWindow) {
                    this.caught = null;
                }
            }
        }

        public void serverTick() {
            int catchInTicks = this.catchTick - WaitFishingPhase.this.tickCounter;
            if (this.caught == null && catchInTicks <= 20 && this.catchSent < this.catchNum) {
                this.catchSent = this.catchNum++;
                this.caught = this.getNewCatch();
                if (this.caught != null) {
                    if (WaitFishingPhase.this.event.level.isServer()) {
                        WaitFishingPhase.this.event.level.getServer().network.sendToClientsWithEntity(PacketFishingStatus.getUpcomingCatchPacket(WaitFishingPhase.this.event, this.lineIndex, catchInTicks, this.caught), WaitFishingPhase.this.event);
                    }
                } else {
                    this.catchTick = catchInTicks + WaitFishingPhase.this.getNewCatchTick();
                }
            } else if (this.caught != null && catchInTicks < -WaitFishingPhase.this.event.fishingRod.reelWindow) {
                this.catchTick = WaitFishingPhase.this.getNewCatchTick();
                this.caught = null;
            }
        }

        public InventoryItem getCatch() {
            if (this.catchTick - WaitFishingPhase.this.tickCounter < 0) {
                return this.caught;
            }
            return null;
        }

        public int getTicksToNextCatch() {
            int d = this.catchTick - WaitFishingPhase.this.tickCounter;
            if (d < 0) {
                if (this.caught != null) {
                    return d;
                }
                return 500;
            }
            return d;
        }

        public void addCatch(InventoryItem item, int inTicks) {
            FishItemInterface fishItem;
            Particle particle;
            if (item.item instanceof FishItemInterface && (particle = (fishItem = (FishItemInterface)((Object)item.item)).getParticle(WaitFishingPhase.this.event.level, this.hookPosition.x, this.hookPosition.y, inTicks * 50)) != null) {
                WaitFishingPhase.this.event.level.entityManager.addParticle(particle, Particle.GType.CRITICAL);
            }
            this.caught = item;
            this.catchTick = WaitFishingPhase.this.tickCounter + inTicks;
        }

        private InventoryItem getNewCatch() {
            FishingSpot spot = new FishingSpot(new LevelTile(WaitFishingPhase.this.event.level, GameMath.getTileCoordinate(this.hookPosition.x), GameMath.getTileCoordinate(this.hookPosition.y)), WaitFishingPhase.this.event.fishingRod, WaitFishingPhase.this.event.bait);
            FishingLootTable lootTable = WaitFishingPhase.this.event.getFishingMob().getFishingLootTable(spot);
            return lootTable.getRandomItem(spot, GameRandom.globalRandom);
        }

        public void remove() {
            if (this.line != null && !this.line.isRemoved()) {
                this.line.remove();
            }
            if (this.hookParticle != null && !this.hookParticle.removed()) {
                this.hookParticle.remove();
            }
        }
    }
}

