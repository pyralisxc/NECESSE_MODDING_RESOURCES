/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementBasicZoneConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.SettlementWorkZoneManager;

public abstract class SettlementWorkZone {
    public final IDData idData = new IDData();
    protected SettlementWorkZoneManager manager;
    protected final Zoning zoning = new Zoning();
    protected boolean removed;
    protected GameMessage name = this.getDefaultName(0);
    private int uniqueID;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public void init(SettlementWorkZoneManager manager) {
        this.manager = manager;
    }

    public void addSaveData(SaveData save) {
        save.addInt("uniqueID", this.uniqueID);
        save.addSaveData(this.name.getSaveData("name"));
        this.zoning.addZoneSaveData("areas", save);
    }

    public void applySaveData(LoadData save, Collection<SettlementWorkZone> currentZones, int tileXOffset, int tileYOffset) {
        this.uniqueID = save.getInt("uniqueID", this.uniqueID);
        LoadData nameSave = save.getFirstLoadDataByName("name");
        if (nameSave != null) {
            this.name = GameMessage.loadSave(nameSave);
        } else {
            this.generateDefaultName(currentZones);
        }
        try {
            this.zoning.applyZoneSaveData("areas", save, tileXOffset, tileYOffset);
        }
        catch (LoadDataException e) {
            GameLog.warn.println("Error loading settlement zone: " + e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writePacket(PacketWriter writer) {
        Zoning zoning = this.zoning;
        synchronized (zoning) {
            this.zoning.writeZonePacket(writer);
        }
        this.name.writePacket(writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyPacket(PacketReader reader) {
        Zoning zoning = this.zoning;
        synchronized (zoning) {
            this.zoning.readZonePacket(reader);
        }
        this.name = GameMessage.fromPacket(reader);
    }

    public int getUniqueID() {
        if (this.uniqueID == 0) {
            this.uniqueID = GameRandom.globalRandom.nextInt();
        }
        return this.uniqueID;
    }

    public void generateUniqueID(Predicate<Integer> isOccupied) {
        if (this.uniqueID != 0) {
            throw new IllegalStateException("Cannot change the uniqueID of a zone once already set");
        }
        this.uniqueID = 1;
        for (int i = 0; i < 1000; ++i) {
            this.uniqueID = GameRandom.globalRandom.nextInt();
            if (this.uniqueID != 0 && this.uniqueID != 1 && !isOccupied.test(this.uniqueID)) break;
        }
    }

    public void setUniqueID(int uniqueID) {
        if (this.uniqueID != 0) {
            throw new IllegalStateException("Cannot change the uniqueID of a zone once already set");
        }
        this.uniqueID = uniqueID;
    }

    public void tickSecond() {
    }

    public void tickJobs() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean fixOverlaps(BiPredicate<Integer, Integer> alreadyExists) {
        LinkedList<Point> removes = new LinkedList<Point>();
        for (Point tile : this.zoning.getTiles()) {
            if (!alreadyExists.test(tile.x, tile.y)) continue;
            removes.add(tile);
        }
        Zoning zoning = this.zoning;
        synchronized (zoning) {
            for (Point tile : removes) {
                this.zoning.removeTile(tile.x, tile.y);
            }
        }
        return !removes.isEmpty();
    }

    public boolean limitZoneToBounds(Rectangle tileRectangle, Point anchor) {
        if (this.zoning.limitZoneToRectangle(tileRectangle)) {
            if (anchor != null) {
                this.zoning.removeDisconnected(anchor.x, anchor.y);
            } else {
                this.zoning.removeDisconnected();
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean expandZone(Level level, Rectangle rectangle, Point anchor, BiPredicate<Integer, Integer> excluded) {
        boolean update = false;
        rectangle = Zoning.limitRectangle(rectangle, new Rectangle(level.tileWidth, level.tileHeight));
        for (int i = 0; i < rectangle.width; ++i) {
            int x = rectangle.x + i;
            for (int j = 0; j < rectangle.height; ++j) {
                int y = rectangle.y + j;
                if (excluded.test(x, y)) continue;
                Zoning zoning = this.zoning;
                synchronized (zoning) {
                    update = this.zoning.addTile(x, y) || update;
                    continue;
                }
            }
        }
        if (update) {
            Zoning zoning = this.zoning;
            synchronized (zoning) {
                if (anchor != null) {
                    this.zoning.removeDisconnected(anchor.x, anchor.y);
                } else {
                    this.zoning.removeDisconnected();
                }
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean shrinkZone(Level level, Rectangle rectangle) {
        boolean update = false;
        rectangle = Zoning.limitRectangle(rectangle, new Rectangle(level.tileWidth, level.tileHeight));
        for (int i = 0; i < rectangle.width; ++i) {
            int x = rectangle.x + i;
            for (int j = 0; j < rectangle.height; ++j) {
                int y = rectangle.y + j;
                Zoning zoning = this.zoning;
                synchronized (zoning) {
                    update = this.zoning.removeTile(x, y) || update;
                    continue;
                }
            }
        }
        if (update) {
            Zoning zoning = this.zoning;
            synchronized (zoning) {
                this.zoning.removeDisconnected();
            }
            return true;
        }
        return false;
    }

    public boolean containsTile(int x, int y) {
        return this.zoning.containsTile(x, y);
    }

    public boolean isEmpty() {
        return this.zoning.isEmpty();
    }

    public int size() {
        return this.zoning.size();
    }

    public Rectangle getTileBounds() {
        return this.zoning.getTileBounds();
    }

    public void remove() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    protected abstract GameMessage getDefaultName(int var1);

    public abstract GameMessage getAbstractName();

    public GameMessage getName() {
        return this.name;
    }

    public void setName(GameMessage name) {
        if (!this.name.translate().equals(name.translate())) {
            this.name = name;
            if (this.manager != null && !this.isRemoved()) {
                new SettlementWorkZoneNameEvent(this.manager.data, this).applyAndSendToClientsAt(this.manager.data.getLevel());
            }
        }
    }

    public void generateDefaultName(Collection<SettlementWorkZone> currentZones) {
        int startCount = (int)currentZones.stream().filter(z -> !z.isRemoved() && z.getID() == this.getID()).count();
        AtomicInteger number = new AtomicInteger(startCount + 1);
        while (currentZones.stream().anyMatch(z -> !z.isRemoved() && z.getName().translate().equals(this.getDefaultName(number.get()).translate()))) {
            number.addAndGet(1);
        }
        this.name = this.getDefaultName(number.get());
    }

    public HudDrawElement getHudDrawElement(int drawPriority, BooleanSupplier overrideShow) {
        return this.getHudDrawElement(drawPriority, overrideShow, new Color(0, 0, 255, 170), new Color(0, 0, 255, 100));
    }

    public HudDrawElement getHudDrawElement(final int drawPriority, final BooleanSupplier overrideShow, final Color edgeColor, final Color fillColor) {
        return new HudDrawElement(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (SettlementWorkZone.this.isHiddenSetting() && (overrideShow == null || !overrideShow.getAsBoolean())) {
                    return;
                }
                Zoning zoning = SettlementWorkZone.this.zoning;
                synchronized (zoning) {
                    final SharedTextureDrawOptions options = SettlementWorkZone.this.zoning.getDrawOptions(edgeColor, fillColor, camera);
                    if (options != null) {
                        list.add(new SortedDrawable(){

                            @Override
                            public int getPriority() {
                                return drawPriority;
                            }

                            @Override
                            public void draw(TickManager tickManager) {
                                options.draw();
                            }
                        });
                    }
                }
            }
        };
    }

    public boolean isHiddenSetting() {
        return false;
    }

    public boolean shouldRemove() {
        return this.isEmpty();
    }

    public void subscribeConfigEvents(SettlementContainer container, BooleanSupplier isActive) {
        container.subscribeEvent(SettlementWorkZoneNameEvent.class, e -> e.settlementUniqueID == container.getSettlementUniqueID() && e.zoneUniqueID == this.getUniqueID(), isActive);
    }

    public boolean canConfigure() {
        return true;
    }

    public void writeSettingsForm(PacketWriter writer) {
    }

    public WorkZoneConfigComponent getSettingsForm(SettlementAssignWorkForm<?> assignWork, Runnable backPressed, PacketReader reader) {
        return new SettlementBasicZoneConfigForm(assignWork, this, backPressed);
    }
}

