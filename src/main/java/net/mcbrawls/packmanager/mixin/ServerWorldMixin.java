package net.mcbrawls.packmanager.mixin;

import net.mcbrawls.packmanager.ResourcePackEnvironment;
import net.minecraft.server.MinecraftServer.ServerResourcePackProperties;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Transforms ServerWorld into a resource pack environment.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ResourcePackEnvironment {
    @Shadow @Final
    List<ServerPlayerEntity> players;

    /**
     * The active resource packs on the world.
     */
    @Unique
    private final Map<UUID, ServerResourcePackProperties> resourcePacks = new HashMap<>();

    @Unique
    @Override
    public void addResourcePack(ServerResourcePackProperties properties) {
        // try add resource pack
        if (resourcePacks.put(properties.id(), properties) == null) {
            // send pack to players if accepted
            var packet = ResourcePackEnvironment.createSendPacket(properties);
            this.players.forEach(player -> player.networkHandler.sendPacket(packet));
        }
    }

    @Unique
    @Override
    public void removeResourcePack(UUID uuid) {
        // try remove resource pack
        if (resourcePacks.remove(uuid) != null) {
            // send pack removal to players if accepted
            var packet = ResourcePackEnvironment.createRemovePacket(uuid);
            this.players.forEach(player -> player.networkHandler.sendPacket(packet));
        }
    }

    @Override
    public Set<ServerResourcePackProperties> getResourcePacks() {
        return new HashSet<>(resourcePacks.values());
    }
}
