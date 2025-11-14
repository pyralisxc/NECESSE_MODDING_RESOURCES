/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.fishingEvent;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.levelEvent.fishingEvent.FishingPhase;
import necesse.entity.levelEvent.fishingEvent.WaitFishingPhase;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.FishingHookProjectile;
import necesse.entity.trails.FishingTrail;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;

public class ReelFishingPhase
extends FishingPhase {
    private final ArrayList<ReelParticles> lines;
    private SoundPlayer reelSoundPlayer;

    public ReelFishingPhase(FishingEvent event, ArrayList<WaitFishingPhase.FishingLure> hooks) {
        super(event);
        ServerClient client;
        this.lines = new ArrayList(hooks.size());
        int totalCatches = 0;
        boolean isPlayer = event.getMob().isPlayer;
        for (WaitFishingPhase.FishingLure line : hooks) {
            JournalChallenge challenge;
            ServerClient serverClient;
            InventoryItem caught = line.getCatch();
            if (isPlayer && caught != null && event.level.isServer() && caught.item.getStringID().equals("icefish") && (serverClient = ((PlayerMob)event.getMob()).getServerClient()) != null && !(challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.CATCH_ICEFISH_ID)).isCompleted(serverClient) && challenge.isJournalEntryDiscovered(serverClient)) {
                challenge.markCompleted(serverClient);
                serverClient.forceCombineNewStats();
            }
            this.lines.add(new ReelParticles(line.hookPosition, caught));
            if (caught == null) continue;
            ++totalCatches;
        }
        if (event.level.isServer() && totalCatches >= 2 && isPlayer && (client = ((PlayerMob)event.getMob()).getServerClient()) != null && client.achievementsLoaded()) {
            client.achievements().DOUBLE_CATCH.markCompleted(client);
        }
    }

    @Override
    public void tickMovement(float delta) {
        boolean isOver = true;
        for (ReelParticles line : this.lines) {
            line.tickMovement();
            if (line.isOver) continue;
            isOver = false;
        }
        if (isOver) {
            this.event.over();
        }
    }

    @Override
    public void clientTick() {
        if (this.reelSoundPlayer == null || this.reelSoundPlayer.isDone()) {
            this.reelSoundPlayer = SoundManager.playSound(new SoundSettings(GameResources.fishingRodReel).volume(0.6f).basePitch(1.1f), this.event.getMob());
        }
        if (this.reelSoundPlayer != null) {
            this.reelSoundPlayer.refreshLooping(0.2f);
        }
    }

    @Override
    public void serverTick() {
    }

    @Override
    public void end() {
        this.lines.forEach(ReelParticles::remove);
    }

    @Override
    public void over() {
        if (!this.event.isClient()) {
            boolean giveBaitBack = true;
            for (ReelParticles line : this.lines) {
                if (!line.spawnedItem) {
                    line.spawnItem();
                }
                if (!line.spawnedItem) continue;
                giveBaitBack = false;
            }
            if (giveBaitBack) {
                this.event.giveBaitBack();
            }
        }
        this.event.getFishingMob().stopFishing();
        this.lines.forEach(ReelParticles::remove);
    }

    private class ReelParticles {
        public FishingTrail line;
        public FishingHookProjectile hook;
        public final InventoryItem caught;
        public boolean spawnedItem;
        public boolean isOver;

        public ReelParticles(Point hookPoint, InventoryItem caught) {
            this.caught = caught;
            this.hook = new FishingHookProjectile(ReelFishingPhase.this.event.level, ReelFishingPhase.this.event, ReelFishingPhase.this.event.getMob(), caught == null ? null : caught.item);
            this.hook.applyData(hookPoint.x, hookPoint.y, ReelFishingPhase.this.event.getMob().getX(), ReelFishingPhase.this.event.getMob().getY(), (float)ReelFishingPhase.this.event.fishingRod.hookSpeed * 1.25f, ReelFishingPhase.this.event.fishingRod.lineLength * 5, new GameDamage(0.0f), ReelFishingPhase.this.event.getMob());
            ReelFishingPhase.this.event.level.entityManager.projectiles.addHidden(this.hook);
            if (!ReelFishingPhase.this.event.level.isServer()) {
                this.line = new FishingTrail(ReelFishingPhase.this.event.getMob(), ReelFishingPhase.this.event.level, this.hook, ReelFishingPhase.this.event.fishingRod);
                ReelFishingPhase.this.event.level.entityManager.addTrail(this.line);
            }
        }

        public void tickMovement() {
            if (this.line != null) {
                this.line.update();
            }
            if (!this.isOver && this.hook.removed()) {
                if (!ReelFishingPhase.this.event.isClient() && !this.spawnedItem) {
                    this.spawnItem();
                }
                if (this.line != null && !this.line.isRemoved()) {
                    this.line.remove();
                }
                this.isOver = true;
            }
        }

        public void spawnItem() {
            if (this.caught != null) {
                ServerClient client;
                ReelFishingPhase.this.event.getFishingMob().giveCaughtItem(ReelFishingPhase.this.event, this.caught);
                if (ReelFishingPhase.this.event.getMob().isPlayer && (client = ((PlayerMob)ReelFishingPhase.this.event.getMob()).getServerClient()) != null) {
                    client.newStats.fish_caught.increment(1);
                }
                this.spawnedItem = true;
            }
        }

        public void remove() {
            if (this.line != null && !this.line.isRemoved()) {
                this.line.remove();
            }
            if (!this.hook.removed()) {
                this.hook.remove();
            }
        }
    }
}

