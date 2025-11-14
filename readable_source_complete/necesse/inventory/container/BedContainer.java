/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import java.awt.Point;
import java.util.function.Predicate;
import necesse.engine.GlobalData;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.events.SleepUpdateContainerEvent;
import necesse.inventory.container.events.SpawnUpdateContainerEvent;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RespawnObject;

public class BedContainer
extends Container {
    public static int WAIT_TIME = 4000;
    public final int tileX;
    public final int tileY;
    public final ObjectEntity objectEntity;
    public final OEUsers oeUsers;
    public int sleepTimer;
    public int sleepingPlayers;
    public int darknessTimer;
    public int updateTimer;
    public long nextWakeUpTime;
    public boolean isCurrentSpawn;
    public EmptyCustomAction setSpawn;
    public EmptyCustomAction clearSpawn;

    public BedContainer(final NetworkClient client, int uniqueSeed, final ObjectEntity objectEntity, Packet content) {
        super(client, uniqueSeed);
        this.tileX = objectEntity.tileX;
        this.tileY = objectEntity.tileY;
        this.objectEntity = objectEntity;
        this.oeUsers = objectEntity instanceof OEUsers ? (OEUsers)((Object)objectEntity) : null;
        if (client.isServer() & this.oeUsers != null) {
            this.oeUsers.startUser(client.playerMob);
        }
        PacketReader reader = new PacketReader(content);
        this.isCurrentSpawn = reader.getNextBoolean();
        this.sleepingPlayers = reader.getNextInt();
        this.subscribeEvent(SleepUpdateContainerEvent.class, e -> true, () -> true);
        this.onEvent(SleepUpdateContainerEvent.class, (T event) -> {
            this.sleepingPlayers = event.totalSleeping;
        });
        this.subscribeEvent(SpawnUpdateContainerEvent.class, e -> e.tileX == this.tileX && e.tileY == this.tileY, () -> true);
        this.onEvent(SpawnUpdateContainerEvent.class, (T event) -> {
            this.isCurrentSpawn = event.isCurrentSpawn;
        });
        this.updateTimer = 60000;
        this.setSpawn = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (!client.isServer()) {
                    return;
                }
                GameObject object = objectEntity.getObject();
                if (object instanceof RespawnObject) {
                    ServerClient serverClient = client.getServerClient();
                    boolean isCurrentSpawn = ((RespawnObject)((Object)object)).isCurrentSpawn(objectEntity.getLevel(), BedContainer.this.tileX, BedContainer.this.tileY, serverClient);
                    ((RespawnObject)((Object)object)).setSpawn(objectEntity.getLevel(), BedContainer.this.tileX, BedContainer.this.tileY, serverClient, !isCurrentSpawn);
                    new SpawnUpdateContainerEvent(BedContainer.this.tileX, BedContainer.this.tileY, true).applyAndSendToClient(serverClient);
                }
            }
        });
        this.clearSpawn = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (!client.isServer()) {
                    return;
                }
                GameObject object = objectEntity.getObject();
                if (object instanceof RespawnObject) {
                    ServerClient serverClient = client.getServerClient();
                    ((RespawnObject)((Object)object)).removeSpawn(objectEntity.getLevel(), BedContainer.this.tileX, BedContainer.this.tileY, serverClient, true);
                    new SpawnUpdateContainerEvent(BedContainer.this.tileX, BedContainer.this.tileY, false).applyAndSendToClient(serverClient);
                }
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.isServer() && this.objectEntity.removed()) {
            this.close();
            return;
        }
        if (this.client.isServer() && (this.oeUsers == null || !this.oeUsers.isMobUsing(this.client.playerMob))) {
            this.close();
            return;
        }
        PlayerMob playerMob = this.getClient().playerMob;
        if (this.client.isServer() && (playerMob.getLevel().isSleepPrevented() || playerMob.getWorldEntity().isSleepPrevented())) {
            this.close();
            return;
        }
        if (this.nextWakeUpTime > 0L && this.nextWakeUpTime <= playerMob.getWorldEntity().getWorldTime()) {
            this.close();
            return;
        }
        this.updateTimer += 50;
        if (this.updateTimer >= 5000) {
            this.sendUpdate(null);
            this.updateTimer = 0;
        }
        this.sleepTimer += 50;
        if (this.client.isClient()) {
            ClientClient cClient = this.client.getClientClient();
            if ((long)this.sleepingPlayers < cClient.getClient().streamClients().count()) {
                if (this.darknessTimer != 0) {
                    float currentDarkness = this.getCurrentDarkness();
                    PostProcessingEffects.setSceneDarknessFade(currentDarkness, 0.0f, 0, 1000, (int)(500.0f * currentDarkness), () -> {
                        GameCamera camera = GlobalData.getCurrentState().getCamera();
                        return new Point(camera.getDrawX(cClient.playerMob.getX()), camera.getDrawY(cClient.playerMob.getY()) - 16);
                    }, () -> !this.client.getClientClient().getClient().isDisconnected() && this.client.hasSpawned());
                }
                this.darknessTimer = 0;
            } else {
                this.darknessTimer += 50;
                GameCamera camera = GlobalData.getCurrentState().getCamera();
                int midX = camera.getDrawX(cClient.playerMob.getX());
                int midY = camera.getDrawY(cClient.playerMob.getY()) - 16;
                PostProcessingEffects.setSceneDarkness(this.getCurrentDarkness(), 0, 1000, midX, midY);
            }
        }
        if (this.sleepTimer > WAIT_TIME && this.getClient().isServer()) {
            Server server = this.getClient().getServerClient().getServer();
            boolean allSleeping = server.streamClients().allMatch(c -> {
                Container container = c.getContainer();
                if (container instanceof BedContainer) {
                    BedContainer sleepContainer = (BedContainer)container;
                    return sleepContainer.sleepTimer > WAIT_TIME;
                }
                return false;
            });
            if (this.nextWakeUpTime == 0L && allSleeping) {
                float hourToTimeInDay = server.world.worldEntity.hourToDayTime(7.0f);
                long timeToNextDay = server.world.getTimeToNextTimeOfDay((int)hourToTimeInDay);
                if (timeToNextDay < 30000L) {
                    timeToNextDay += (long)server.world.worldEntity.getDayTimeMax() * 1000L;
                }
                this.nextWakeUpTime = server.world.getWorldTime() + timeToNextDay;
            } else if (!allSleeping) {
                this.nextWakeUpTime = 0L;
            }
            if (allSleeping) {
                server.world.worldEntity.keepSleeping();
            }
        }
    }

    public float getCurrentDarkness() {
        return Math.min((float)this.darknessTimer / (float)WAIT_TIME, 1.0f);
    }

    public void sendUpdate(Predicate<ServerClient> filter) {
        if (!this.client.isServer()) {
            return;
        }
        Server server = this.client.getServerClient().getServer();
        SleepUpdateContainerEvent event = new SleepUpdateContainerEvent(server, filter);
        event.applyAndSendToAllClients(server);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.client.isServer() & this.oeUsers != null) {
            this.oeUsers.stopUser(this.client.playerMob);
        }
        this.sendUpdate(c -> c != this.getClient());
        if (this.client.isClient()) {
            float currentDarkness = this.getCurrentDarkness();
            PostProcessingEffects.setSceneDarknessFade(currentDarkness, 0.0f, 0, 1000, (int)(500.0f * currentDarkness), () -> {
                ClientClient cClient = this.client.getClientClient();
                GameCamera camera = GlobalData.getCurrentState().getCamera();
                return new Point(camera.getDrawX(cClient.playerMob.getX()), camera.getDrawY(cClient.playerMob.getY()) - 16);
            }, () -> !this.client.getClientClient().getClient().isDisconnected() && this.client.hasSpawned());
        }
    }

    public static Packet getContainerContent(ServerClient client, boolean isCurrentSpawn) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(isCurrentSpawn);
        int total = (int)client.getServer().streamClients().filter(c -> c == client || c.getContainer() instanceof BedContainer).count();
        writer.putNextInt(total);
        return content;
    }
}

