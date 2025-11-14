/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.actions.LevelEventAction;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class SecondLifePerkLevelEvent
extends LevelEvent {
    public int extraLives;
    protected HashMap<Long, Integer> playerRemainingLives = new HashMap();
    protected final PlayerLifeUsedAction lifeUsedAction;

    public SecondLifePerkLevelEvent() {
        super(true);
        this.shouldSave = true;
        this.lifeUsedAction = this.registerAction(new PlayerLifeUsedAction());
    }

    public SecondLifePerkLevelEvent(int extraLives) {
        this();
        this.extraLives = extraLives;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("extraLives", this.extraLives);
        if (!this.playerRemainingLives.isEmpty()) {
            SaveData remainingLivesSave = new SaveData("playerRemainingLives");
            for (Map.Entry<Long, Integer> entry : this.playerRemainingLives.entrySet()) {
                remainingLivesSave.addInt(Long.toString(entry.getKey()), entry.getValue());
            }
            save.addSaveData(remainingLivesSave);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.extraLives = save.getInt("extraLives", this.extraLives, false);
        LoadData remainingLivesSave = save.getFirstLoadDataByName("playerRemainingLives");
        if (remainingLivesSave != null) {
            for (LoadData loadDatum : remainingLivesSave.getLoadData()) {
                if (!loadDatum.isData()) continue;
                try {
                    long key = Long.parseLong(loadDatum.getName());
                    int lives = Integer.parseInt(loadDatum.getData());
                    this.playerRemainingLives.put(key, lives);
                }
                catch (NumberFormatException e) {
                    GameLog.warn.println("Failed to parse player remaining lives key or value: " + loadDatum.getName() + " = " + loadDatum.getData());
                }
            }
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.extraLives);
        writer.putNextShortUnsigned(this.playerRemainingLives.size());
        for (Map.Entry<Long, Integer> entry : this.playerRemainingLives.entrySet()) {
            writer.putNextLong(entry.getKey());
            writer.putNextInt(entry.getValue());
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.extraLives = reader.getNextShortUnsigned();
        this.playerRemainingLives.clear();
        int count = reader.getNextShortUnsigned();
        for (int i = 0; i < count; ++i) {
            long key = reader.getNextLong();
            int lives = reader.getNextInt();
            this.playerRemainingLives.put(key, lives);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickBuffs();
        this.tickSingleLevelEvent();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickBuffs();
        this.tickSingleLevelEvent();
    }

    protected void tickSingleLevelEvent() {
        LevelEvent event;
        int eventUniqueID = this.getLevel().gndData.getInt("secondLifePerkEventUniqueID");
        if (eventUniqueID != 0 && (event = this.getLevel().entityManager.events.get(eventUniqueID, false)) instanceof SecondLifePerkLevelEvent) {
            SecondLifePerkLevelEvent secondLifeEvent = (SecondLifePerkLevelEvent)event;
            if (secondLifeEvent.extraLives > this.extraLives) {
                this.over();
                return;
            }
        }
        this.getLevel().gndData.setInt("secondLifePerkEventUniqueID", this.getUniqueID());
    }

    public void tickBuffs() {
        GameUtils.streamNetworkClients(this.getLevel()).filter(c -> !c.isDead()).filter(c -> c.playerMob != null && !c.playerMob.removed()).forEach(c -> this.tickBuff(c.authentication, c.playerMob));
    }

    public void tickBuff(long auth, PlayerMob player) {
        int remainingLives = this.playerRemainingLives.getOrDefault(auth, this.extraLives);
        if (remainingLives > 0) {
            ActiveBuff existingBuff = player.buffManager.getBuff(BuffRegistry.SECOND_LIFE_PERK);
            if (existingBuff == null) {
                existingBuff = new ActiveBuff(BuffRegistry.SECOND_LIFE_PERK, (Mob)player, 0.5f, null);
                player.buffManager.addBuff(existingBuff, false);
            }
            existingBuff.getGndData().setInt("eventUniqueID", this.getUniqueID());
            while (existingBuff.getStacks() < remainingLives) {
                existingBuff.addStack(1000, null);
            }
            while (existingBuff.getStacks() > remainingLives) {
                existingBuff.removeStack(false);
            }
            existingBuff.setDurationLeftSeconds(0.5f);
        }
    }

    public boolean onPlayerUsedLife(Mob mob) {
        if (!mob.isPlayer) {
            return false;
        }
        PlayerMob player = (PlayerMob)mob;
        ServerClient serverClient = player.getServerClient();
        if (serverClient == null) {
            return false;
        }
        int remainingLives = this.playerRemainingLives.getOrDefault(serverClient.authentication, this.extraLives) - 1;
        this.lifeUsedAction.runAndSend(serverClient.authentication, remainingLives);
        return remainingLives >= 0;
    }

    protected class PlayerLifeUsedAction
    extends LevelEventAction {
        protected PlayerLifeUsedAction() {
        }

        public void runAndSend(long auth, int remainingLives) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextLong(auth);
            writer.putNextInt(remainingLives);
            this.runAndSendAction(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            ClientClient client;
            long auth = reader.getNextLong();
            int remainingLives = reader.getNextInt();
            SecondLifePerkLevelEvent.this.playerRemainingLives.put(auth, remainingLives);
            if (SecondLifePerkLevelEvent.this.isClient() && (client = SecondLifePerkLevelEvent.this.getClient().getClientByAuth(auth)) != null && client.playerMob != null && client.isSamePlace(SecondLifePerkLevelEvent.this.getLevel())) {
                PlayerMob player = client.playerMob;
                if (player.getLevel() == null) {
                    return;
                }
                SecondLifePerkLevelEvent.this.tickBuff(auth, player);
                if (client.slot == SecondLifePerkLevelEvent.this.getClient().getSlot()) {
                    SoundManager.playSound(GameResources.teleportfail, (SoundEffect)SoundEffect.effect(player).pitch(0.7f));
                }
                for (int i = 0; i < 10; ++i) {
                    player.getLevel().entityManager.addParticle(player.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), player.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesFriction((float)(GameRandom.globalRandom.nextGaussian() * 6.0), (float)(GameRandom.globalRandom.nextGaussian() * 6.0), 0.5f).color(new Color(225, 37, 37)).lifeTime(2000).heightMoves(16.0f, 48.0f);
                }
            }
        }
    }
}

