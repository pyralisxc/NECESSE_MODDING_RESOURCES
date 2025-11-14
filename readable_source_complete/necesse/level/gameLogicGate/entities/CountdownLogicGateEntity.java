/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.CountdownRelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.presets.PresetRotation;

public class CountdownLogicGateEntity
extends LogicGateEntity {
    public boolean[] startInputs = new boolean[4];
    public boolean[] resetInputs = new boolean[4];
    public boolean[] wireOutputs = new boolean[4];
    public int totalCountdownTime = 200;
    public boolean[] relayDirections = new boolean[4];
    public boolean reverseRelayActive;
    private boolean currentlyActive;
    private int currentCountdownTime;
    private ArrayList<ArrayList<CountdownRelayLogicGateEntity>> currentRelays;

    public CountdownLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public CountdownLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("startInputs", this.startInputs);
        save.addSmallBooleanArray("resetInputs", this.resetInputs);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addInt("totalCountdownTime", this.totalCountdownTime);
        save.addSmallBooleanArray("relayDirections", this.relayDirections);
        save.addBoolean("reverseRelayActive", this.reverseRelayActive);
        save.addBoolean("currentlyActive", this.currentlyActive);
        save.addInt("currentCountdownTime", this.currentCountdownTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.startInputs = save.getSmallBooleanArray("startInputs", this.startInputs);
        this.resetInputs = save.getSmallBooleanArray("resetInputs", this.resetInputs);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.totalCountdownTime = save.getInt("totalCountdownTime", this.totalCountdownTime);
        this.relayDirections = save.getSmallBooleanArray("relayDirections", this.relayDirections);
        this.reverseRelayActive = save.getBoolean("reverseRelayActive", this.reverseRelayActive);
        this.currentlyActive = save.getBoolean("currentlyActive", this.currentlyActive);
        this.currentCountdownTime = save.getInt("currentCountdownTime", this.currentCountdownTime);
        this.updateOutputs(true);
    }

    @Override
    public void applyPresetLoadData(LoadData save, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
        super.applyPresetLoadData(save, mirrorX, mirrorY, rotation);
        CountdownLogicGateEntity.applyPresetRotationToDirectionArray(this.relayDirections, mirrorX, mirrorY, rotation);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.startInputs[i]);
            writer.putNextBoolean(this.resetInputs[i]);
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        writer.putNextInt(this.totalCountdownTime);
        for (boolean relayDirection : this.relayDirections) {
            writer.putNextBoolean(relayDirection);
        }
        writer.putNextBoolean(this.reverseRelayActive);
        writer.putNextBoolean(this.currentlyActive);
        writer.putNextInt(this.currentCountdownTime);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        int i;
        super.applyPacket(reader);
        for (i = 0; i < 4; ++i) {
            this.startInputs[i] = reader.getNextBoolean();
            this.resetInputs[i] = reader.getNextBoolean();
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        this.totalCountdownTime = reader.getNextInt();
        for (i = 0; i < this.relayDirections.length; ++i) {
            this.relayDirections[i] = reader.getNextBoolean();
        }
        this.reverseRelayActive = reader.getNextBoolean();
        this.currentlyActive = reader.getNextBoolean();
        this.currentCountdownTime = reader.getNextInt();
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isServer()) {
            return;
        }
        if (this.currentlyActive) {
            boolean anyInputWireActive = false;
            for (int i = 0; i < 4; ++i) {
                if (!this.startInputs[i] || !this.isWireActive(i)) continue;
                anyInputWireActive = true;
                break;
            }
            if (!anyInputWireActive) {
                ++this.currentCountdownTime;
                if (this.currentRelays == null) {
                    this.currentRelays = this.getRelayList();
                }
                this.updateRelays(anyInputWireActive);
                if (this.currentCountdownTime >= this.totalCountdownTime - 1) {
                    if (this.currentCountdownTime >= this.totalCountdownTime) {
                        this.currentlyActive = false;
                        this.currentCountdownTime = 0;
                    }
                    this.updateOutputs(false);
                }
            }
        }
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        if (!this.isServer()) {
            return;
        }
        boolean anyInputWireActive = false;
        for (int i = 0; i < 4; ++i) {
            if (!this.startInputs[i] || !this.isWireActive(i)) continue;
            anyInputWireActive = true;
            break;
        }
        if (anyInputWireActive) {
            this.currentCountdownTime = 0;
            this.currentlyActive = true;
            this.currentRelays = this.getRelayList();
            this.updateRelays(anyInputWireActive);
            this.updateOutputs(false);
        } else if (this.resetInputs[wireID] && active && this.currentlyActive) {
            this.currentCountdownTime = 0;
            this.currentlyActive = false;
            this.currentRelays = this.getRelayList();
            this.updateRelays(anyInputWireActive);
            this.updateOutputs(false);
        }
    }

    public void updateOutputs(boolean forceUpdate) {
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs[i] && this.currentCountdownTime == this.totalCountdownTime - 1;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    protected void updateRelays(boolean anyInputWireActive) {
        if (anyInputWireActive) {
            for (ArrayList<CountdownRelayLogicGateEntity> relays : this.currentRelays) {
                for (CountdownRelayLogicGateEntity relayEntity : relays) {
                    relayEntity.setActive(!this.reverseRelayActive);
                }
            }
        } else {
            float percentPerRelay = 1.0f / (float)this.currentRelays.size();
            float percent = (float)this.currentCountdownTime / (float)this.totalCountdownTime;
            for (int i = 0; i < this.currentRelays.size(); ++i) {
                ArrayList<CountdownRelayLogicGateEntity> relayEntities = this.currentRelays.get(i);
                if (this.currentlyActive) {
                    boolean shouldBeActive;
                    float relayPercent = (float)i / (float)this.currentRelays.size();
                    boolean bl = shouldBeActive = percent <= relayPercent + percentPerRelay / 2.0f;
                    if (this.reverseRelayActive) {
                        shouldBeActive = !shouldBeActive;
                    }
                    for (CountdownRelayLogicGateEntity relayEntity : relayEntities) {
                        relayEntity.setActive(shouldBeActive);
                    }
                    continue;
                }
                for (CountdownRelayLogicGateEntity relayEntity : relayEntities) {
                    relayEntity.setActive(this.reverseRelayActive);
                }
            }
        }
    }

    protected ArrayList<ArrayList<CountdownRelayLogicGateEntity>> getRelayList() {
        ArrayList<ArrayList<CountdownRelayLogicGateEntity>> relays = new ArrayList<ArrayList<CountdownRelayLogicGateEntity>>();
        HashSet<Point> alreadyAdded = new HashSet<Point>();
        ArrayList<CountdownRelayLogicGateEntity> firstRelays = new ArrayList<CountdownRelayLogicGateEntity>();
        for (int i = 0; i < this.relayDirections.length; ++i) {
            if (!this.relayDirections[i]) continue;
            CountdownLogicGateEntity.addNextRelay(this.level, firstRelays, alreadyAdded, this.tileX, this.tileY, i);
        }
        if (!firstRelays.isEmpty()) {
            relays.add(firstRelays);
            while (true) {
                ArrayList<CountdownRelayLogicGateEntity> lastRelays = relays.get(relays.size() - 1);
                ArrayList<CountdownRelayLogicGateEntity> next = new ArrayList<CountdownRelayLogicGateEntity>();
                for (CountdownRelayLogicGateEntity relay : lastRelays) {
                    for (int i = 0; i < relay.relayDirections.length; ++i) {
                        if (!relay.relayDirections[i]) continue;
                        CountdownLogicGateEntity.addNextRelay(relay.level, next, alreadyAdded, relay.tileX, relay.tileY, i);
                    }
                }
                if (next.isEmpty()) break;
                relays.add(next);
            }
        }
        return relays;
    }

    public static void addNextRelay(Level level, ArrayList<CountdownRelayLogicGateEntity> relays, HashSet<Point> alreadyAdded, int tileX, int tileY, int dir) {
        Function<Integer, Point> dirToTile;
        switch (dir) {
            case 0: {
                dirToTile = i -> new Point(tileX, tileY - i);
                break;
            }
            case 1: {
                dirToTile = i -> new Point(tileX + i, tileY);
                break;
            }
            case 2: {
                dirToTile = i -> new Point(tileX, tileY + i);
                break;
            }
            case 3: {
                dirToTile = i -> new Point(tileX - i, tileY);
                break;
            }
            default: {
                return;
            }
        }
        for (int i2 = 1; i2 < 20; ++i2) {
            LogicGateEntity currentEntity;
            Point p = dirToTile.apply(i2);
            if (alreadyAdded.contains(p) || !((currentEntity = level.logicLayer.getEntity(p.x, p.y)) instanceof CountdownRelayLogicGateEntity)) continue;
            CountdownRelayLogicGateEntity relayEntity = (CountdownRelayLogicGateEntity)currentEntity;
            relays.add(relayEntity);
            alreadyAdded.add(p);
            return;
        }
    }

    public static GameMessage getRelayDirName(int relayDir) {
        switch (relayDir) {
            case 0: {
                return new LocalMessage("ui", "dirnorth");
            }
            case 1: {
                return new LocalMessage("ui", "direast");
            }
            case 2: {
                return new LocalMessage("ui", "dirsouth");
            }
            case 3: {
                return new LocalMessage("ui", "dirwest");
            }
        }
        return new LocalMessage("ui", "countdownrelaynone");
    }

    public static void applyPresetRotationToDirectionArray(boolean[] dirs, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
        if (mirrorX) {
            boolean relayWest;
            boolean relayEast = dirs[1];
            dirs[1] = relayWest = dirs[3];
            dirs[3] = relayEast;
        }
        if (mirrorY) {
            boolean relaySouth;
            boolean relayNorth = dirs[0];
            dirs[0] = relaySouth = dirs[2];
            dirs[2] = relayNorth;
        }
        if (rotation != null) {
            for (int i = 0; i < rotation.dirOffset; ++i) {
                boolean relayWest;
                boolean relayNorth = dirs[0];
                boolean relayEast = dirs[1];
                boolean relaySouth = dirs[2];
                dirs[0] = relayWest = dirs[3];
                dirs[1] = relayNorth;
                dirs[2] = relayEast;
                dirs[3] = relaySouth;
            }
        }
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        tooltips.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.startInputs)));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.COUNTDOWN_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

