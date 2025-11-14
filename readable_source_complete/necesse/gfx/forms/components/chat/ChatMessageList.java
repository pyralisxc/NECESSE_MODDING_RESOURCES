/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.chat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Predicate;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.forms.components.chat.ChatMessageListener;

public class ChatMessageList
implements Iterable<ChatMessage> {
    public static final int maxSavedMessages = 250;
    private LinkedList<ChatMessage> messages = new LinkedList();
    private LinkedList<ChatMessageListener> listeners = new LinkedList();

    public ChatMessage addMessage(String message) {
        FairType type = new FairType();
        type.append(ChatMessage.fontOptions, message);
        return this.addMessage(type);
    }

    public ChatMessage addMessage(FairType fairType) {
        fairType.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions));
        System.out.println("Chat > " + fairType.getParseString());
        return this.addMessage(new ChatMessage(fairType));
    }

    public <T extends ChatMessage> T addMessage(T message) {
        this.messages.addLast(message);
        if (this.messages.size() > 250) {
            this.messages.removeFirst();
        }
        for (ChatMessageListener listener : this.listeners) {
            listener.onNewMessage(message);
        }
        return message;
    }

    public ChatMessage addOrModifyMessage(String identifier, String message) {
        FairType type = new FairType();
        type.append(ChatMessage.fontOptions, message);
        return this.addOrModifyMessage(identifier, type);
    }

    public ChatMessage addOrModifyMessage(String identifier, FairType type) {
        type.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions));
        ChatMessage last = this.messages.getLast();
        if (last.identifier != null && last.identifier.equals(identifier)) {
            this.messages.removeLast();
            for (ChatMessageListener listener : this.listeners) {
                listener.onRemoveMessage(last);
            }
        }
        return this.addMessage(new ChatMessage(identifier, type));
    }

    public void removeMessage(ChatMessage message) {
        if (this.messages.remove(message)) {
            for (ChatMessageListener listener : this.listeners) {
                listener.onRemoveMessage(message);
            }
        }
    }

    public boolean removeMessagesIf(Predicate<? super ChatMessage> filter) {
        LinkedList<ChatMessage> removes = new LinkedList<ChatMessage>();
        ListIterator li = this.messages.listIterator();
        while (li.hasNext()) {
            ChatMessage message = (ChatMessage)li.next();
            if (!filter.test(message)) continue;
            removes.addFirst(message);
            li.remove();
        }
        for (ChatMessage message : removes) {
            for (ChatMessageListener listener : this.listeners) {
                listener.onRemoveMessage(message);
            }
        }
        return !removes.isEmpty();
    }

    public void addListener(ChatMessageListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ChatMessageListener listener) {
        this.listeners.remove(listener);
    }

    public int getTotalListeners() {
        return this.listeners.size();
    }

    @Override
    public Iterator<ChatMessage> iterator() {
        return this.messages.iterator();
    }
}

