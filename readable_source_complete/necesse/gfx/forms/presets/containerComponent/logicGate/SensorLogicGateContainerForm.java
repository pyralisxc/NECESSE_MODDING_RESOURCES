/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.logicGate;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GameEventListener;
import necesse.engine.GameTileRange;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.logicGate.LogicGateContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.SensorLogicGateContainer;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;
import necesse.level.maps.hudManager.HudDrawElement;

public class SensorLogicGateContainerForm<T extends SensorLogicGateContainer>
extends LogicGateContainerForm<T> {
    protected HudDrawElement rangeElement;
    protected FormHorizontalScroll<Integer> rangeSelector;
    protected final GameEventListener<LogicGateEntity.ApplyPacketEvent> applyListener;

    public SensorLogicGateContainerForm(Client client, final T container) {
        super(client, 400, 160, container);
        this.addComponent(new FormLocalLabel(((SensorLogicGateContainer)container).entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
        this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireoutputs"), ((SensorLogicGateContainer)container).entity, e -> e.wireOutputs, ((SensorLogicGateContainer)container).setOutputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 150, 45, 100, false));
        this.addComponent(new FormLocalLabel(new LocalMessage("ui", "sensorlabel"), new FontOptions(20), -1, 160, 40));
        final FormCheckBox players = this.addComponent(new FormLocalCheckBox("ui", "sensorplayers", 160, 65, ((SensorLogicGateContainer)container).entity.players)).onClicked(e -> container.setPlayers.runAndSend(((FormCheckBox)e.from).checked));
        final FormCheckBox hostileMobs = this.addComponent(new FormLocalCheckBox("ui", "sensorhostile", 160, 85, ((SensorLogicGateContainer)container).entity.hostileMobs)).onClicked(e -> container.setHostileMobs.runAndSend(((FormCheckBox)e.from).checked));
        final FormCheckBox passiveMobs = this.addComponent(new FormLocalCheckBox("ui", "sensorpassive", 160, 105, ((SensorLogicGateContainer)container).entity.passiveMobs)).onClicked(e -> container.setPassiveMobs.runAndSend(((FormCheckBox)e.from).checked));
        this.rangeSelector = this.addComponent(new FormHorizontalIntScroll(160, 125, 100, FormHorizontalScroll.DrawOption.valueOnHover, new LocalMessage("ui", "sensorrange"), ((SensorLogicGateContainer)container).entity.range, 1, SensorLogicGateEntity.MAX_RANGE)).onChanged(e -> container.setRange.runAndSend((Integer)((FormHorizontalScroll)e.from).getValue()));
        this.applyListener = ((SensorLogicGateContainer)container).entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>(){

            @Override
            public void onEvent(LogicGateEntity.ApplyPacketEvent event) {
                players.checked = container.entity.players;
                hostileMobs.checked = container.entity.hostileMobs;
                passiveMobs.checked = container.entity.passiveMobs;
                SensorLogicGateContainerForm.this.rangeSelector.setValue(container.entity.range);
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
        this.rangeElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (!SensorLogicGateContainerForm.this.rangeSelector.isHovering()) {
                    return;
                }
                GameTileRange tileRange = SensorLogicGateEntity.getTileRange(((SensorLogicGateContainer)((SensorLogicGateContainerForm)SensorLogicGateContainerForm.this).container).entity.range);
                final SharedTextureDrawOptions options = tileRange.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), ((SensorLogicGateContainer)((SensorLogicGateContainerForm)SensorLogicGateContainerForm.this).container).entity.tileX, ((SensorLogicGateContainer)((SensorLogicGateContainerForm)SensorLogicGateContainerForm.this).container).entity.tileY, camera);
                if (options != null) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    });
                }
            }
        };
        this.client.getLevel().hudManager.addElement(this.rangeElement);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.rangeSelector.isHovering()) {
            Renderer.hudManager.fadeHUD();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.applyListener.dispose();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
    }
}

