package net.mcbrawls.packmanager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * An event invoked when the server receives a resource pack status packet.
 * This is only invoked during the play phase, not configuration.
 */
public interface ResourcePackStatusEvent {
    Event<ResourcePackStatusEvent> EVENT = EventFactory.createArrayBacked(
            ResourcePackStatusEvent.class,
            callbacks -> (player, id, status) -> {
                for (ResourcePackStatusEvent callback : callbacks) {
                    callback.onStatus(player, id, status);
                }
            }
    );

    /**
     * Called on resource pack status receipt from the client.
     * @param id the id of the resource pack
     * @param status the status of the client
     */
    void onStatus(ServerPlayerEntity player, UUID id, ResourcePackStatusC2SPacket.Status status);
}
