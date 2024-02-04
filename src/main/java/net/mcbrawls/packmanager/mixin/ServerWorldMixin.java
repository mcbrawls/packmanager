package net.mcbrawls.packmanager.mixin;

import net.mcbrawls.packmanager.ResourcePackEnvironment;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ResourcePackEnvironment {
}
