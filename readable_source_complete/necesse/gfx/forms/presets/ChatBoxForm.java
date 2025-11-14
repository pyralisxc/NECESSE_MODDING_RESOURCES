/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.chat.FormChatInput;
import necesse.gfx.forms.components.chat.FormChatLog;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;

public class ChatBoxForm
extends Form {
    private final FormChatLog log;
    private final FormChatInput input;

    public ChatBoxForm(Client client, String name) {
        super(name, 510, 280);
        this.shouldLimitDrawArea = false;
        this.onWindowResized(WindowManager.getWindow());
        this.log = this.addComponent(new FormChatLog(0, 0, this.getWidth(), this.getHeight() - 20, client.chat){

            @Override
            public synchronized void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
            }

            @Override
            public boolean shouldDraw() {
                this.setActive(ChatBoxForm.this.input.isTyping() || ChatBoxForm.this.input.isControllerFocus() || ChatBoxForm.this.input.isControllerTyping());
                if (this.isActive()) {
                    return true;
                }
                return !Settings.hideChat;
            }
        });
        this.input = this.addComponent(new FormChatInput(0, this.getHeight() - 20, client, this.getWidth()), Integer.MAX_VALUE);
        this.input.onSubmit(e -> {
            String text = this.input.getText();
            if (text.length() != 0) {
                if (client != null) {
                    boolean send = true;
                    if (text.startsWith("/")) {
                        String fullCommand = text.substring(1);
                        send = !client.commandsManager.runClientCommand(new ParsedCommand(fullCommand));
                    } else {
                        PlayerMob player = client.getPlayer();
                        String playerName = player.getDisplayName() + ": ";
                        client.chat.addMessage(playerName + text);
                        Level level = player.getLevel();
                        if (level != null) {
                            level.hudManager.addElement(new ChatBubbleText(player, text));
                        }
                    }
                    if (send) {
                        client.network.sendPacket(new PacketChatMessage(client.getSlot(), text));
                    }
                }
                this.input.clearAndAddToLog();
            }
            this.input.setTyping(false);
        });
        this.drawBase = false;
    }

    public void onAutocompletePacket(List<AutoComplete> autoCompletes) {
        this.input.onAutocompletePacket(autoCompletes);
    }

    public void submitEscapeEvent(InputEvent escapeEvent) {
        if (this.isTyping()) {
            this.input.submitEscapeEvent(escapeEvent);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosition(10, window.getHudHeight() - this.getHeight() - 80);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return false;
    }

    public boolean isTyping() {
        return this.input.isTyping();
    }

    public void setTyping(boolean value) {
        this.input.setTyping(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refreshBoundingBoxes() {
        FormChatLog formChatLog = this.log;
        synchronized (formChatLog) {
            this.log.refreshBoundingBoxes();
        }
    }
}

