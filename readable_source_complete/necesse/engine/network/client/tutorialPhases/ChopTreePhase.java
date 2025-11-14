/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class ChopTreePhase
extends TutorialPhase {
    public static String[] treeItems = new String[]{"oaklog", "sprucelog", "pinelog", "palmlog", "willowlog", "oaksapling", "sprucesapling", "pinesapling", "palmsapling", "willowsapling", "cactussapling"};
    private Point chopTreeCoord;
    private long chopTreeFindCooldown;
    private LocalMessage hitTree;
    private LocalMessage findTree;
    private HudDrawElement drawElement;

    public ChopTreePhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.hitTree = new LocalMessage("tutorials", "hittree");
        this.findTree = new LocalMessage("tutorials", "findtree");
        this.chopTreeFindCooldown = 0L;
        this.chopTreeCoord = null;
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
                        DrawOptionsList drawOptions = new DrawOptionsList();
                        Point chopTreeCoord = ChopTreePhase.this.chopTreeCoord;
                        if (chopTreeCoord == null) {
                            FairTypeDrawOptions text = ChopTreePhase.this.getTextDrawOptions(ChopTreePhase.this.getTutorialText(ChopTreePhase.this.findTree.translate()));
                            drawOptions.add(ChopTreePhase.this.getLevelTextDrawOptions(text, perspective.getX(), perspective.getY() - 50, camera, perspective, false));
                        } else {
                            FairTypeDrawOptions text = ChopTreePhase.this.getTextDrawOptions(ChopTreePhase.this.getTutorialText(ChopTreePhase.this.hitTree.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE1)));
                            drawOptions.add(ChopTreePhase.this.getLevelTextDrawOptions(text, chopTreeCoord.x * 32 + 16, chopTreeCoord.y * 32 + 8, camera, perspective, true));
                            drawOptions.add(Settings.UI.select_outline.initDraw().sprite(0, 0, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(camera.getTileDrawX(chopTreeCoord.x), camera.getTileDrawY(chopTreeCoord.y)));
                            drawOptions.add(Settings.UI.select_outline.initDraw().sprite(1, 0, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(camera.getTileDrawX(chopTreeCoord.x) + 16, camera.getTileDrawY(chopTreeCoord.y)));
                            drawOptions.add(Settings.UI.select_outline.initDraw().sprite(0, 1, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(camera.getTileDrawX(chopTreeCoord.x), camera.getTileDrawY(chopTreeCoord.y) + 16));
                            drawOptions.add(Settings.UI.select_outline.initDraw().sprite(1, 1, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(camera.getTileDrawX(chopTreeCoord.x) + 16, camera.getTileDrawY(chopTreeCoord.y) + 16));
                        }
                        drawOptions.draw();
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
        this.setObjective(mainGame, "treestorch");
    }

    @Override
    public void tick() {
        this.findNewTree();
        if (this.chopTreeCoord != null && !this.checkForTree(this.chopTreeCoord.x, this.chopTreeCoord.y)) {
            this.over();
        }
        for (String itemID : treeItems) {
            if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem(itemID), false, false, false, false, "tutorial") <= 0) continue;
            this.over();
        }
    }

    private void findNewTree() {
        if (this.chopTreeFindCooldown > this.client.worldEntity.getLocalTime()) {
            return;
        }
        if (this.client.getLevel() == null) {
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            return;
        }
        this.chopTreeCoord = null;
        final AtomicBoolean foundEmpty = new AtomicBoolean();
        new AreaFinder(player, 20){

            @Override
            public boolean checkPoint(int x, int y) {
                if (ChopTreePhase.this.client.getLevel().getTileID(x, y) == TileRegistry.emptyID) {
                    foundEmpty.set(true);
                    return true;
                }
                if (ChopTreePhase.this.checkForTree(x, y)) {
                    ChopTreePhase.this.chopTreeCoord = new Point(x, y);
                    return true;
                }
                return false;
            }
        }.runFinder();
        this.chopTreeFindCooldown = this.client.worldEntity.getLocalTime() + (long)(foundEmpty.get() ? 1000 : 4000);
    }

    private boolean checkForTree(int tileX, int tileY) {
        return this.client.getLevel().getObject((int)tileX, (int)tileY).isTree;
    }
}

