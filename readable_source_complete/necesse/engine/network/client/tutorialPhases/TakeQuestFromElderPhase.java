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
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.HaveKilledMobsSettlementQuest;
import necesse.engine.quest.KillMobsSettlementQuest;
import necesse.engine.quest.Quest;
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

public class TakeQuestFromElderPhase
extends TutorialPhase {
    private Mob elderMob;
    private long findElderCooldown;
    private boolean elderFocus;
    private boolean questCompleted;
    private LocalMessage walk;
    private HudDrawElement drawElement;

    public TakeQuestFromElderPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.elderMob = null;
        this.findElderCooldown = 0L;
        this.elderFocus = false;
        this.questCompleted = false;
        for (Quest quest : this.client.quests.getQuests()) {
            if (!(quest instanceof DeliverItemsSettlementQuest) && !(quest instanceof HaveKilledMobsSettlementQuest) && !(quest instanceof KillMobsSettlementQuest)) continue;
            this.questCompleted = true;
            break;
        }
        this.walk = new LocalMessage("tutorials", "elderwalk");
        this.drawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                Mob elderMob;
                if (!TakeQuestFromElderPhase.this.elderFocus && (elderMob = TakeQuestFromElderPhase.this.elderMob) != null) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MAX_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            FairTypeDrawOptions text = TakeQuestFromElderPhase.this.getTextDrawOptions(TakeQuestFromElderPhase.this.getTutorialText(TakeQuestFromElderPhase.this.walk.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE2)));
                            DrawOptions options = TakeQuestFromElderPhase.this.getLevelTextDrawOptions(text, elderMob.getX() + 1, elderMob.getY() - 76, camera, perspective, true);
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
        if (this.questCompleted) {
            this.over();
        } else {
            this.setObjective(mainGame, "takeelderquest");
        }
    }

    @Override
    public void tick() {
        if (!this.elderFocus) {
            if (this.elderMob != null && (this.elderMob.removed() || this.client.getLevel().entityManager.mobs.get(this.elderMob.getUniqueID(), false) != this.elderMob)) {
                this.elderMob = null;
            }
            this.findElder();
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
        this.elderMob = null;
        int elderID = MobRegistry.getMobID("elderhuman");
        this.elderMob = this.client.getLevel().entityManager.mobs.streamAreaTileRange(player.getX(), player.getY(), 200).filter(m -> m.getID() == elderID).findFirst().orElse(null);
        this.findElderCooldown = this.client.worldEntity.getLocalTime() + 2000L;
    }

    public void questAccepted() {
        this.questCompleted = true;
        if (GlobalData.getCurrentState() instanceof MainGame) {
            this.updateObjective((MainGame)GlobalData.getCurrentState());
        }
    }

    public void elderInteracted() {
        this.elderFocus = true;
    }
}

