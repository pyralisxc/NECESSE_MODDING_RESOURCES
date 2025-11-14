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
import necesse.engine.registries.ItemRegistry;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.Form;
import necesse.level.maps.hudManager.HudDrawElement;

public class CraftTorchPhase
extends TutorialPhase {
    private LocalMessage openInv;
    private LocalMessage craftTorch;
    private HudDrawElement drawElement;

    public CraftTorchPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.openInv = new LocalMessage("tutorials", "openinv");
        this.craftTorch = new LocalMessage("tutorials", "crafttorch");
        this.drawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                if (!perspective.isInventoryExtended()) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MAX_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            FairTypeDrawOptions text = CraftTorchPhase.this.getTextDrawOptions(CraftTorchPhase.this.getTutorialText(CraftTorchPhase.this.openInv.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.INVENTORY)));
                            DrawOptions options = CraftTorchPhase.this.getLevelTextDrawOptions(text, perspective.getX(), perspective.getY() - 50, camera, perspective, false);
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
        this.setObjective(mainGame, "treestorch");
    }

    @Override
    public void tick() {
        if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem("torch"), false, false, false, false, "tutorial") > 0) {
            this.over();
        }
    }

    @Override
    public void drawOverForm(PlayerMob perspective) {
        if (perspective.isInventoryExtended()) {
            Form form = ((MainGame)GlobalData.getCurrentState()).formManager.crafting;
            FairTypeDrawOptions text = this.getTextDrawOptions(this.getTutorialText(this.craftTorch.translate()));
            this.getTextDrawOptions(text, null, form.getX() + form.getWidth() / 2 - 40, form.getY(), true).draw();
        }
    }
}

