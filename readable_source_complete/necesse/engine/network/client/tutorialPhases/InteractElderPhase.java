/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.registries.MobRegistry;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class InteractElderPhase
extends TutorialPhase {
    private Mob elderMob;
    private long findElderCooldown;
    private boolean elderFocus;
    private LocalMessage walk;
    private HudDrawElement drawElement;

    public InteractElderPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.elderMob = null;
        this.findElderCooldown = 0L;
        this.elderFocus = false;
        this.walk = new LocalMessage("tutorials", "elderwalk");
        this.drawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                Mob elderMob;
                if (!InteractElderPhase.this.elderFocus && (elderMob = InteractElderPhase.this.elderMob) != null) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MAX_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            FairTypeDrawOptions text = InteractElderPhase.this.getTextDrawOptions(InteractElderPhase.this.getTutorialText(InteractElderPhase.this.walk.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE2)));
                            DrawOptions options = InteractElderPhase.this.getLevelTextDrawOptions(text, elderMob.getX(), elderMob.getY() - 32, camera, perspective, true);
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
        if (!this.elderFocus) {
            this.setObjective(mainGame, "talkelder");
        } else {
            this.setObjective(mainGame, new LocalMessage("tutorials", "talkelderstop", "key", "[input=" + Control.INVENTORY.id + "]"));
        }
    }

    @Override
    public void tick() {
        if (!this.elderFocus) {
            if (this.elderMob != null && (this.elderMob.removed() || this.client.getLevel().entityManager.mobs.get(this.elderMob.getUniqueID(), false) != this.elderMob)) {
                this.elderMob = null;
            }
            this.findElder();
        } else if (!this.client.hasFocusForm()) {
            this.over();
        }
    }

    public void findElder() {
        if (this.findElderCooldown > this.client.worldEntity.getLocalTime()) {
            return;
        }
        if (this.client.getLevel() == null) {
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            return;
        }
        int elderID = MobRegistry.getMobID("elderhuman");
        this.elderMob = this.client.getLevel().entityManager.mobs.streamAreaTileRange(player.getX(), player.getY(), 200).filter(m -> m.getID() == elderID).findFirst().orElse(null);
        this.findElderCooldown = this.client.worldEntity.getLocalTime() + 2000L;
    }

    public void elderInteracted() {
        this.elderFocus = true;
        if (GlobalData.getCurrentState() instanceof MainGame) {
            this.updateObjective((MainGame)GlobalData.getCurrentState());
        }
    }
}

