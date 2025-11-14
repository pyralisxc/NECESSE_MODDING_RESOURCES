/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;

public class CommandLog {
    private final Client client;
    private final ServerClient serverClient;
    private List<Runnable> logs;
    private boolean printed = false;

    public CommandLog(Client client, ServerClient serverClient) {
        this.client = client;
        this.serverClient = serverClient;
        this.logs = new ArrayList<Runnable>();
    }

    public void add(FairType fairType) {
        this.add(() -> CommandLog.print(this.client, this.serverClient, fairType));
    }

    public void add(GameMessage msg) {
        this.add(() -> CommandLog.print(this.client, this.serverClient, msg));
    }

    public void add(String msg) {
        this.add(new StaticMessage(msg));
    }

    public void addConsole(String msg) {
        this.addConsole(new StaticMessage(msg));
    }

    public void addConsole(GameMessage msg) {
        this.add(() -> System.out.println(GameColor.stripCodes(msg.translate())));
    }

    public void addClient(String msg, ServerClient serverClient) {
        this.addClient(new StaticMessage(msg), serverClient);
    }

    public void addClient(GameMessage msg, ServerClient serverClient) {
        this.add(() -> serverClient.sendChatMessage(msg));
    }

    protected synchronized void add(Runnable printLogic) {
        if (this.printed) {
            printLogic.run();
        } else {
            this.logs.add(printLogic);
        }
    }

    public synchronized void printLog() {
        this.logs.forEach(Runnable::run);
        this.printed = true;
    }

    public static void print(Client client, ServerClient serverClient, FairType fairType) {
        if (client != null) {
            client.chat.addMessage(fairType);
        } else if (serverClient != null) {
            Server server = serverClient.getServer();
            Client localClient = server.getLocalClient();
            if (localClient != null && server.getLocalServerClient() == serverClient) {
                localClient.chat.addMessage(fairType);
            } else {
                serverClient.sendChatMessage(fairType.getParseString());
            }
        } else {
            System.out.println(GameColor.stripCodes(fairType.getParseString()));
        }
    }

    public static void print(Client client, ServerClient serverClient, GameMessage msg) {
        if (client != null) {
            client.chat.addMessage(msg.translate());
        } else if (serverClient != null) {
            serverClient.sendChatMessage(msg);
        } else {
            System.out.println(GameColor.stripCodes(msg.translate()));
        }
    }

    public static void print(Client client, ServerClient serverClient, String msg) {
        CommandLog.print(client, serverClient, new StaticMessage(msg));
    }
}

