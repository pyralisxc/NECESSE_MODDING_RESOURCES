/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import java.util.ArrayList;
import java.util.UUID;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.ThreadFreezeMonitor;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.loading.ClientLoadingClient;
import necesse.engine.network.client.loading.ClientLoadingConnecting;
import necesse.engine.network.client.loading.ClientLoadingLevelData;
import necesse.engine.network.client.loading.ClientLoadingLevelPreload;
import necesse.engine.network.client.loading.ClientLoadingPhase;
import necesse.engine.network.client.loading.ClientLoadingPlayerStats;
import necesse.engine.network.client.loading.ClientLoadingPlayers;
import necesse.engine.network.client.loading.ClientLoadingQuests;
import necesse.engine.network.client.loading.ClientLoadingSelectCharacter;
import necesse.engine.network.client.loading.ClientLoadingWorld;
import necesse.engine.network.client.loading.ClientObjectEntityLoading;
import necesse.engine.network.packet.PacketClientStats;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.MainMenuFormManager;
import necesse.level.maps.Level;

public class ClientLoading {
    public final Client client;
    private final ArrayList<ClientLoadingPhase> phases = new ArrayList();
    private int currentPhase;
    private boolean currentPhaseStarted;
    private boolean firstLoad = true;
    private long loadStartTime;
    private FormResizeWrapper unusedFormWrapper;
    public final ClientLoadingConnecting connectingPhase;
    public final ClientLoadingWorld worldPhase;
    public final ClientLoadingPlayerStats statsPhase;
    public final ClientLoadingLevelData levelDataPhase;
    public final ClientLoadingSelectCharacter createCharPhase;
    public final ClientLoadingPlayers playersPhase;
    public final ClientLoadingClient clientPhase;
    public final ClientLoadingQuests questsPhase;
    public final ClientLoadingLevelPreload levelPreloadPhase;
    public final ClientObjectEntityLoading objectEntities;

    public ClientLoading(Client client) {
        this.client = client;
        this.connectingPhase = new ClientLoadingConnecting(this);
        this.phases.add(this.connectingPhase);
        this.worldPhase = new ClientLoadingWorld(this);
        this.phases.add(this.worldPhase);
        this.levelDataPhase = new ClientLoadingLevelData(this);
        this.phases.add(this.levelDataPhase);
        this.createCharPhase = new ClientLoadingSelectCharacter(this);
        this.phases.add(this.createCharPhase);
        this.statsPhase = new ClientLoadingPlayerStats(this);
        this.phases.add(this.statsPhase);
        this.playersPhase = new ClientLoadingPlayers(this);
        this.phases.add(this.playersPhase);
        this.clientPhase = new ClientLoadingClient(this);
        this.phases.add(this.clientPhase);
        this.questsPhase = new ClientLoadingQuests(this);
        this.phases.add(this.questsPhase);
        this.levelPreloadPhase = new ClientLoadingLevelPreload(this);
        this.phases.add(this.levelPreloadPhase);
        this.currentPhase = 0;
        this.objectEntities = new ClientObjectEntityLoading(this);
    }

    public void init() {
        this.startCurrentPhase();
    }

    private void startCurrentPhase() {
        ClientLoadingPhase currentPhase = this.phases.get(this.currentPhase);
        this.currentPhaseStarted = false;
        if (currentPhase.isDone()) {
            return;
        }
        FormResizeWrapper start = currentPhase.start();
        this.currentPhaseStarted = true;
        if (currentPhase.isDone()) {
            return;
        }
        FormManager formManager = GlobalData.getCurrentState().getFormManager();
        if (formManager instanceof MainMenuFormManager) {
            ((MainMenuFormManager)formManager).setConnectingComponent(start);
        } else {
            this.unusedFormWrapper = start;
        }
    }

    public FormResizeWrapper getUnusedFormWrapper() {
        FormResizeWrapper temp = this.unusedFormWrapper;
        this.unusedFormWrapper = null;
        return temp;
    }

    public GameMessage getLoadingMessage() {
        if (this.isDone()) {
            return null;
        }
        return this.phases.get(this.currentPhase).getLoadingMessage();
    }

    public void tick() {
        if (!this.isDone()) {
            long timeSinceStart;
            block8: {
                ThreadFreezeMonitor.setLoading();
                do {
                    if (this.currentPhaseStarted) {
                        this.phases.get(this.currentPhase).tick();
                    }
                    if (!this.nextPhase()) break block8;
                } while (!this.isDone());
                this.finishUp();
            }
            if (!this.isDone() && this.loadStartTime != 0L && (timeSinceStart = System.currentTimeMillis() - this.loadStartTime) >= 10000L) {
                if (GlobalData.getCurrentState() instanceof MainGame) {
                    GlobalData.setCurrentState(new MainMenu(this.client));
                }
                this.loadStartTime = 0L;
            }
        } else {
            if (this.client.levelManager.loading().isLoadingDone()) {
                Level level = this.client.getLevel();
                if (level.debugLoadingPerformance != null && level.debugLoadingPrintCooldown == 0) {
                    System.out.println("PERFORMANCE RESULTS POST IN-LEVEL LOADING:");
                    PerformanceTimerUtils.printPerformanceTimer(level.debugLoadingPerformance.getCurrentRootPerformanceTimer());
                    level.debugLoadingPerformance = null;
                }
                this.client.recordLoadingPerformance = false;
            }
            this.objectEntities.tick();
        }
    }

    private boolean nextPhase() {
        if (this.isDone()) {
            return false;
        }
        boolean out = false;
        while (this.phases.get(this.currentPhase).isDone()) {
            out = true;
            if (this.currentPhaseStarted) {
                this.phases.get(this.currentPhase).end();
            }
            ++this.currentPhase;
            if (this.isDone()) break;
            this.startCurrentPhase();
        }
        return out;
    }

    private void finishUp() {
        Level level;
        this.client.resetPositionPointUpdate();
        this.client.tutorial.start();
        if (Settings.alwaysSkipTutorial || this.client.worldSettings.creativeMode) {
            this.client.tutorial.endTutorial();
        }
        if (this.firstLoad) {
            this.client.network.sendPacket(new PacketClientStats(GlobalData.stats(), GlobalData.achievements()));
        }
        if ((level = this.client.getLevel()) != null) {
            if (level.debugLoadingPerformance != null) {
                System.out.println("PERFORMANCE RESULTS PRE IN-LEVEL LOADING:");
                PerformanceTimerUtils.printPerformanceTimer(level.debugLoadingPerformance.getCurrentRootPerformanceTimer());
            }
            if (this.client.recordLoadingPerformance) {
                level.debugLoadingPerformance = new PerformanceTimerManager();
            }
        }
        this.firstLoad = false;
    }

    public void reset() {
        this.loadStartTime = System.currentTimeMillis();
        this.currentPhase = 0;
        this.phases.stream().filter(p -> p.resetOnLevelChange).forEach(ClientLoadingPhase::reset);
        this.startCurrentPhase();
        this.objectEntities.reset();
    }

    public void submitApprovedPacket(PacketConnectApproved packet) {
        this.connectingPhase.submitApprovedPacket(packet);
        this.createCharPhase.submitConnectAccepted(packet);
    }

    public boolean isDone() {
        return this.currentPhase >= this.phases.size();
    }

    public boolean hasLocalServer() {
        return this.client.getLocalServer() != null;
    }

    public String getClientCachePath(long worldUniqueID, String uniqueIdentifier) {
        byte[] bytes = (worldUniqueID + uniqueIdentifier).getBytes();
        return "/client/" + UUID.nameUUIDFromBytes(bytes);
    }

    public String getClientCachePath(String uniqueIdentifier) {
        return this.getClientCachePath(this.client.getWorldUniqueID(), uniqueIdentifier);
    }
}

