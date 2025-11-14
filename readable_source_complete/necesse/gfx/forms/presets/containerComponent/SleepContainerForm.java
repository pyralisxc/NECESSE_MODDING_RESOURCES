/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.TreeSet;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameRandom;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.BedContainer;
import necesse.inventory.container.events.SpawnUpdateContainerEvent;

public class SleepContainerForm
extends ContainerFormSwitcher<BedContainer> {
    public final FormComponentList components;
    public final FormLabel label;
    public final FormLabel clockLabel;
    public final FormLabel timeLabel;
    public final Form wakeUpButtonForm;
    public final Form spawnButtonForm;
    public final FormLocalTextButton spawnButton;
    private final TreeSet<SleepingCharacter> chars = new TreeSet<SleepingCharacter>(Comparator.comparingLong(c -> ((SleepingCharacter)c).startTime - (long)((SleepingCharacter)c).lifeTime));

    public SleepContainerForm(Client client, BedContainer container) {
        super(client, container);
        this.components = this.addComponent(new FormComponentList());
        this.makeCurrent(this.components);
        this.wakeUpButtonForm = this.components.addComponent(new Form(200, 32));
        FormLocalTextButton wakeUpButton = this.wakeUpButtonForm.addComponent(new FormLocalTextButton("ui", "wakeupbutton", 0, 0, this.wakeUpButtonForm.getWidth(), FormInputSize.SIZE_32, ButtonColor.BASE));
        wakeUpButton.onClicked(e -> client.closeContainer(true));
        this.spawnButtonForm = this.components.addComponent(new Form(300, 32));
        this.spawnButton = this.spawnButtonForm.addComponent(new FormLocalTextButton("ui", container.isCurrentSpawn ? "clearspawnbutton" : "setspawnbutton", 0, 0, this.spawnButtonForm.getWidth(), FormInputSize.SIZE_32, ButtonColor.BASE));
        this.spawnButton.onClicked(e -> {
            if (!container.isCurrentSpawn) {
                container.setSpawn.runAndSend();
            } else {
                container.clearSpawn.runAndSend();
            }
            this.spawnButton.startCooldown(250);
        });
        container.onEvent(SpawnUpdateContainerEvent.class, event -> this.spawnButton.setLocalization(new LocalMessage("ui", event.isCurrentSpawn ? "clearspawnbutton" : "setspawnbutton")));
        this.label = this.components.addComponent(new FormLabel("", new FontOptions(20).color(Color.WHITE).outline(), 0, 0, 0));
        this.clockLabel = this.components.addComponent(new FormLabel("", new FontOptions(20).color(Color.WHITE).outline(), 0, 0, 0));
        this.timeLabel = this.components.addComponent(new FormLabel("", new FontOptions(20).color(Color.WHITE).outline(), 0, 0, 0));
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        WorldEntity worldEntity;
        if ((long)((BedContainer)this.container).sleepingPlayers < this.client.streamClients().count()) {
            this.label.setText(Localization.translate("ui", "waitingplayers", "count", ((BedContainer)this.container).sleepingPlayers, "total", this.client.streamClients().count()));
        } else {
            if (tickManager.isGameTick() && GameRandom.globalRandom.getEveryXthChance(20)) {
                this.chars.add(new SleepingCharacter((float)WindowManager.getWindow().getHudWidth() / 2.0f + (float)GameRandom.globalRandom.getIntBetween(-10, 10), (float)WindowManager.getWindow().getHudHeight() / 2.0f - 20.0f + (float)GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-10, 10), GameRandom.globalRandom.getIntBetween(-20, -50), GameRandom.globalRandom.getIntBetween(5, 25) * GameRandom.globalRandom.getOneOf(1, -1), GameRandom.globalRandom.getIntBetween(14, 20), GameRandom.globalRandom.getIntBetween(1000, 3000)));
            }
            while (!this.chars.isEmpty() && this.chars.first().shouldRemove()) {
                this.chars.pollFirst();
            }
            this.label.setText("");
        }
        if (perspective != null && (worldEntity = perspective.getWorldEntity()) != null) {
            this.clockLabel.setText(worldEntity.getDayTimeReadable());
            this.timeLabel.setText(worldEntity.getTimeOfDay().displayName);
        }
        for (SleepingCharacter aChar : this.chars) {
            aChar.draw();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.wakeUpButtonForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2 + 50);
        this.spawnButtonForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2 + 50 + this.wakeUpButtonForm.getHeight() + this.getInterfaceStyle().formSpacing);
        this.label.setPosition(window.getHudWidth() / 2, window.getHudHeight() / 2 - 140);
        this.clockLabel.setPosition(window.getHudWidth() / 2, window.getHudHeight() / 2 - 120);
        this.timeLabel.setPosition(window.getHudWidth() / 2, window.getHudHeight() / 2 - 100);
    }

    private static class SleepingCharacter {
        private final float x;
        private final float y;
        private final float dx;
        private final float dy;
        private final float sway;
        private final int fontSize;
        private final long startTime;
        private final int lifeTime;

        public SleepingCharacter(float x, float y, float dx, float dy, float sway, int fontSize, int lifeTime) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.sway = sway;
            this.fontSize = fontSize;
            this.lifeTime = lifeTime;
            this.startTime = System.currentTimeMillis();
        }

        public void draw() {
            double percFontSize;
            if (this.shouldRemove()) {
                return;
            }
            long currentLife = System.currentTimeMillis() - this.startTime;
            int fontSize = this.fontSize;
            if (currentLife < 500L) {
                percFontSize = (double)currentLife / 500.0;
                fontSize = (int)(percFontSize * (double)fontSize);
            } else if (currentLife > (long)(this.lifeTime - 100)) {
                percFontSize = Math.abs((double)(currentLife + 100L - (long)this.lifeTime) / 100.0 - 1.0);
                fontSize = (int)(percFontSize * (double)fontSize);
            }
            double percLife = (double)currentLife / (double)this.lifeTime;
            double x = (double)this.x + Math.sin((double)currentLife / 500.0) * (double)this.sway + (double)this.dx * percLife;
            double y = (double)this.y + (double)this.dy * percLife;
            FontOptions fontOptions = new FontOptions(fontSize).outline();
            float width = FontManager.bit.getWidth('Z', fontOptions);
            float height = FontManager.bit.getHeight('Z', fontOptions);
            FontManager.bit.drawChar((float)x - width / 2.0f, (float)y - height / 2.0f, 'Z', fontOptions);
        }

        public boolean shouldRemove() {
            return this.startTime + (long)this.lifeTime < System.currentTimeMillis();
        }
    }
}

