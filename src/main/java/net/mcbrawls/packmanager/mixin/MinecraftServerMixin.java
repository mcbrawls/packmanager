package net.mcbrawls.packmanager.mixin;

import net.mcbrawls.packmanager.ResourcePackEnvironment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MinecraftServer.ServerResourcePackProperties;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Transforms MinecraftServer into a resource pack environment.
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ResourcePackEnvironment {
    @Shadow private PlayerManager playerManager;

    /**
     * The active resource packs on the server.
     */
    @Unique
    private final Map<UUID, ServerResourcePackProperties> resourcePacks = new HashMap<>();

    @Unique
    @Override
    public void addResourcePack(ServerResourcePackProperties properties) {
        // try add resource pack
        if (resourcePacks.put(properties.id(), properties) == null) {
            // send pack to players if accepted
            if (this.playerManager != null) {
                var packet = ResourcePackEnvironment.createSendPacket(properties);
                this.playerManager.sendToAll(packet);
            }
        }
    }

    @Unique
    @Override
    public void removeResourcePack(UUID uuid) {
        // try remove resource pack
        if (resourcePacks.remove(uuid) != null) {
            // send pack removal to players if accepted
            if (this.playerManager != null) {
                var packet = ResourcePackEnvironment.createRemovePacket(uuid);
                this.playerManager.sendToAll(packet);
            }
        }
    }

    @Override
    public Set<ServerResourcePackProperties> getResourcePacks() {
        return new HashSet<>(resourcePacks.values());
    }
}
