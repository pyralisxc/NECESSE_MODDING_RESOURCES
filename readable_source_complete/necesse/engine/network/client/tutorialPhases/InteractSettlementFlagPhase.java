/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class InteractSettlementFlagPhase
extends TutorialPhase {
    private boolean settlementActive;
    private Point chestCoord;
    private long findFlagCooldown;
    private HudDrawElement drawElement;

    public InteractSettlementFlagPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.chestCoord = null;
        this.findFlagCooldown = 0L;
        this.drawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        Point chestCoord = InteractSettlementFlagPhase.this.chestCoord;
                        DrawOptions options = null;
                        if (InteractSettlementFlagPhase.this.settlementActive) {
                            FairTypeDrawOptions text = InteractSettlementFlagPhase.this.getTextDrawOptions(InteractSettlementFlagPhase.this.getTutorialText(Localization.translate("tutorials", "useflag")).replaceAll("<key>", new FairControlKeyGlyph(Control.OPEN_SETTLEMENT)));
                            options = InteractSettlementFlagPhase.this.getLevelTextDrawOptions(text, perspective.getX(), perspective.getY() - 50, camera, perspective, false);
                        } else if (chestCoord != null) {
                            FairTypeDrawOptions text = InteractSettlementFlagPhase.this.getTextDrawOptions(InteractSettlementFlagPhase.this.getTutorialText(Localization.translate("tutorials", "takeflag")));
                            options = InteractSettlementFlagPhase.this.getLevelTextDrawOptions(text, chestCoord.x * 32 + 16, chestCoord.y * 32 + 8, camera, perspective, true);
                        }
                        if (options != null) {
                            options.draw();
                        }
                    }
                });
            }
        };
        this.client.getLevel().hudManager.addElement(this.drawElement);
    }

    @Override
    public void end() {
        super.end();
        if (this.drawElement != null) {
            this.drawElement.remove();
        }
        this.drawElement = null;
    }

    @Override
    public void updateObjective(MainGame mainGame) {
        this.setObjective(mainGame, new LocalMessage("tutorials", "settlement", "key", "[input=" + Control.OPEN_SETTLEMENT.id + "]"));
    }

    @Override
    public void tick() {
        Container container;
        PlayerMob player = this.client.getPlayer();
        Level level = this.client.getLevel();
        if (player != null && level != null) {
            NetworkSettlementData settlement = SettlementsWorldData.getSettlementsData(this.client).getNetworkDataAtTile(level.getIdentifier(), player.getTileX(), player.getTileY());
            boolean bl = this.settlementActive = settlement != null && settlement.hasFlag() && !settlement.isDisbanding();
            if (!this.settlementActive) {
                this.findFlag();
            }
        }
        if ((container = this.client.getContainer()) instanceof SettlementContainer) {
            this.over();
        }
    }

    public void findFlag() {
        if (this.findFlagCooldown > this.client.worldEntity.getLocalTime()) {
            return;
        }
        if (this.client.getLevel() == null) {
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            return;
        }
        this.chestCoord = null;
        final AtomicBoolean foundEmpty = new AtomicBoolean();
        new AreaFinder(player, 30){

            @Override
            public boolean checkPoint(int x, int y) {
                if (InteractSettlementFlagPhase.this.client.getLevel().getTileID(x, y) == TileRegistry.emptyID) {
                    foundEmpty.set(true);
                    return true;
                }
                if (InteractSettlementFlagPhase.this.containsSettlementFlag(x, y)) {
                    InteractSettlementFlagPhase.this.chestCoord = new Point(x, y);
                    return true;
                }
                return false;
            }
        }.runFinder();
        this.findFlagCooldown = this.client.worldEntity.getLocalTime() + (long)(foundEmpty.get() ? 1000 : 5000);
    }

    public boolean containsSettlementFlag(int x, int y) {
        Inventory inventory;
        ObjectEntity oe = this.client.getLevel().entityManager.getObjectEntity(x, y);
        if (oe instanceof OEInventory && (inventory = ((OEInventory)((Object)oe)).getInventory()) != null) {
            for (int i = 0; i < inventory.getSize(); ++i) {
                InventoryItem item = inventory.getItem(i);
                if (item == null || !item.item.getStringID().equals("settlementflag")) continue;
                return true;
            }
        }
        return false;
    }
}

