/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkClient
 *  necesse.engine.registries.ContainerRegistry
 */
package aphorea.registry;

import aphorea.containers.book.BookContainer;
import aphorea.containers.book.BookContainerForm;
import aphorea.containers.initialrune.InitialRuneContainer;
import aphorea.containers.initialrune.InitialRuneContainerForm;
import aphorea.containers.runesinjector.RunesInjectorContainer;
import aphorea.containers.runesinjector.RunesInjectorContainerForm;
import necesse.engine.network.NetworkClient;
import necesse.engine.registries.ContainerRegistry;

public class AphContainers {
    public static int RUNES_INJECTOR_CONTAINER;
    public static int INITIAL_RUNE_CONTAINER;
    public static int BOOK_CONTAINER;

    public static void registerCore() {
        RUNES_INJECTOR_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new RunesInjectorContainerForm<RunesInjectorContainer>(client, new RunesInjectorContainer((NetworkClient)client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new RunesInjectorContainer((NetworkClient)client, uniqueSeed, packet));
        INITIAL_RUNE_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new InitialRuneContainerForm<InitialRuneContainer>(client, new InitialRuneContainer((NetworkClient)client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new InitialRuneContainer((NetworkClient)client, uniqueSeed, packet));
        BOOK_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new BookContainerForm<BookContainer>(client, new BookContainer((NetworkClient)client.getClient(), uniqueSeed)), (client, uniqueSeed, packet, serverObject) -> new BookContainer((NetworkClient)client, uniqueSeed));
    }
}

