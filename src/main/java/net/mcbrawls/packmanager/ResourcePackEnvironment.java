package net.mcbrawls.packmanager;

import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.MinecraftServer.ServerResourcePackProperties;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a context which can manage resource packs.
 */
public interface ResourcePackEnvironment {
    /**
     * Adds a resource pack to this environment.
     * @param properties the resource pack properties
     */
    default void addResourcePack(ServerResourcePackProperties properties) {
        throw new AssertionError("Not implemented in mixin");
    }

    /**
     * Removes a resource pack from this environment.
     * @param uuid the uuid of the resource pack
     */
    default void removeResourcePack(UUID uuid) {
        throw new AssertionError("Not implemented in mixin");
    }

    /**
     * Returns a set of all resource packs within this environment.
     * @return a set of resource pack properties
     */
    default Set<ServerResourcePackProperties> getResourcePacks() {
        throw new AssertionError("Not implemented in mixin");
    }

    /**
     * Creates a packet which enables a resource pack for the given properties.
     * @return a resource pack send packet
     */
    static ResourcePackSendS2CPacket createSendPacket(ServerResourcePackProperties properties) {
        return new ResourcePackSendS2CPacket(
                properties.id(),
                properties.url(),
                properties.hash(),
                properties.isRequired(),
                properties.prompt()
        );
    }

    /**
     * Creates a packet which removes a resource pack for the given properties.
     * @param uuid the uuid of the resource pack to be removed
     * @return a resource pack remove packet
     */
    static ResourcePackRemoveS2CPacket createRemovePacket(UUID uuid) {
        return new ResourcePackRemoveS2CPacket(Optional.of(uuid));
    }
}
