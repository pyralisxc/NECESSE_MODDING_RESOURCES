/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class MineOrePhase
extends TutorialPhase {
    private static final String[] oreItems = new String[]{"copperore", "ironore", "goldore"};
    private Point oreCoord;
    private long findOreCooldown;
    private boolean isUnderground;
    private LocalMessage mineOre;
    private LocalMessage findOre;
    private HudDrawElement drawElement;

    public MineOrePhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        this.oreCoord = null;
        this.findOreCooldown = 0L;
        this.isUnderground = false;
        this.mineOre = new LocalMessage("tutorials", "mineore");
        this.findOre = new LocalMessage("tutorials", "findorefloat");
        this.drawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                if (MineOrePhase.this.isUnderground) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MAX_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            DrawOptions options;
                            Point oreCoord = MineOrePhase.this.oreCoord;
                            if (oreCoord != null) {
                                FairTypeDrawOptions text = MineOrePhase.this.getTextDrawOptions(MineOrePhase.this.getTutorialText(MineOrePhase.this.mineOre.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE1)));
                                options = MineOrePhase.this.getLevelTextDrawOptions(text, oreCoord.x * 32 + 16, oreCoord.y * 32 + 8, camera, perspective, true);
                            } else {
                                FairTypeDrawOptions text = MineOrePhase.this.getTextDrawOptions(MineOrePhase.this.getTutorialText(MineOrePhase.this.findOre.translate()));
                                options = MineOrePhase.this.getLevelTextDrawOptions(text, perspective.getX(), perspective.getY() - 50, camera, perspective, false);
                            }
                            options.draw();
                        }
                    });
                }
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
        boolean bl = this.isUnderground = this.client.getLevel().getIdentifier().equals(LevelIdentifier.CAVE_IDENTIFIER) || this.client.getLevel().getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
        if (this.isUnderground) {
            this.setObjective(mainGame, "findore");
        } else {
            this.setObjective(mainGame, "gounder");
        }
    }

    @Override
    public void tick() {
        boolean isUnderground;
        boolean bl = isUnderground = this.client.getLevel().getIdentifier().equals(LevelIdentifier.CAVE_IDENTIFIER) || this.client.getLevel().getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
        if (isUnderground != this.isUnderground && GlobalData.getCurrentState() instanceof MainGame) {
            this.updateObjective((MainGame)GlobalData.getCurrentState());
        }
        if (isUnderground) {
            this.findOre();
            if (this.oreCoord != null && !this.isOre(this.oreCoord.x, this.oreCoord.y)) {
                this.over();
            }
            for (String ore : oreItems) {
                if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem(ore), false, false, false, false, "tutorial") <= 0) continue;
                this.over();
            }
        } else {
            this.oreCoord = null;
        }
    }

    private void findOre() {
        if (this.findOreCooldown > this.client.worldEntity.getLocalTime()) {
            return;
        }
        if (this.client.getLevel() == null) {
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            return;
        }
        this.oreCoord = null;
        final AtomicBoolean foundEmpty = new AtomicBoolean();
        new AreaFinder(player, 30){

            @Override
            public boolean checkPoint(int x, int y) {
                if (MineOrePhase.this.client.getLevel().getTileID(x, y) == TileRegistry.emptyID) {
                    foundEmpty.set(true);
                    return true;
                }
                if (MineOrePhase.this.isOre(x, y)) {
                    MineOrePhase.this.oreCoord = new Point(x, y);
                    return true;
                }
                return false;
            }
        }.runFinder();
        this.findOreCooldown = this.client.worldEntity.getLocalTime() + (long)(foundEmpty.get() ? 1000 : 5000);
    }

    private boolean isOre(int tileX, int tileY) {
        return this.client.getLevel().getObject((int)tileX, (int)tileY).isOre;
    }
}

