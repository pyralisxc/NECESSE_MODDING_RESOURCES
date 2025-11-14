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
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class UseLadderPhase
extends TutorialPhase {
    private Point ladderCoord;
    private long findLadderCooldown;
    private LocalMessage craftLadder;
    private LocalMessage useLadder;
    private HudDrawElement drawElement;

    public UseLadderPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.findLadderCooldown = 0L;
        this.ladderCoord = null;
        this.craftLadder = new LocalMessage("tutorials", "craftladderfloat");
        this.useLadder = new LocalMessage("tutorials", "useladderfloat");
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
                        DrawOptions options;
                        Point ladderCoord = UseLadderPhase.this.ladderCoord;
                        if (ladderCoord != null) {
                            FairTypeDrawOptions text = UseLadderPhase.this.getTextDrawOptions(UseLadderPhase.this.getTutorialText(UseLadderPhase.this.useLadder.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE2)));
                            options = UseLadderPhase.this.getLevelTextDrawOptions(text, ladderCoord.x * 32 + 16, ladderCoord.y * 32 + 8, camera, perspective, true);
                        } else {
                            FairTypeDrawOptions text = UseLadderPhase.this.getTextDrawOptions(UseLadderPhase.this.getTutorialText(UseLadderPhase.this.craftLadder.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE1)));
                            options = UseLadderPhase.this.getLevelTextDrawOptions(text, perspective.getX(), perspective.getY() - 50, camera, perspective, false);
                        }
                        options.draw();
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
        this.findLadder();
        if (this.ladderCoord == null) {
            this.setObjective(mainGame, "craftladder");
        } else {
            this.setObjective(mainGame, "useladder");
        }
    }

    @Override
    public void tick() {
        if (this.client.getLevel().isCave) {
            this.over();
            return;
        }
        this.findLadder();
    }

    private void findLadder() {
        if (this.findLadderCooldown > this.client.worldEntity.getLocalTime()) {
            return;
        }
        if (this.client.getLevel() == null) {
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            return;
        }
        this.ladderCoord = null;
        final AtomicBoolean foundEmpty = new AtomicBoolean();
        new AreaFinder(player, 40){

            @Override
            public boolean checkPoint(int x, int y) {
                if (UseLadderPhase.this.client.getLevel().getTileID(x, y) == TileRegistry.emptyID) {
                    foundEmpty.set(true);
                    return true;
                }
                if (UseLadderPhase.this.isLadder(x, y)) {
                    UseLadderPhase.this.ladderCoord = new Point(x, y);
                    return true;
                }
                return false;
            }
        }.runFinder();
        this.findLadderCooldown = this.client.worldEntity.getLocalTime() + (long)(foundEmpty.get() ? 1000 : 10000);
    }

    private boolean isLadder(int tileX, int tileY) {
        return this.client.getLevel().getObject(tileX, tileY).getStringID().equals("ladderdown");
    }
}

