/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.chat;

import necesse.gfx.forms.components.chat.ChatMessage;

public interface ChatMessageListener {
    public void onNewMessage(ChatMessage var1);

    public void onRemoveMessage(ChatMessage var1);
}

