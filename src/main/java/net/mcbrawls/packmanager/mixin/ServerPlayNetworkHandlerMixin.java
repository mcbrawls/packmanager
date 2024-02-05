package net.mcbrawls.packmanager.mixin;

import net.mcbrawls.packmanager.ResourcePackStatusEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    private ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Override
    public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
        super.onResourcePackStatus(packet);

        // invoke event if not disconnected
        if (!connection.disconnected) {
            ResourcePackStatusEvent.EVENT.invoker().onStatus(this.player, packet.id(), packet.status());
        }
    }
}
