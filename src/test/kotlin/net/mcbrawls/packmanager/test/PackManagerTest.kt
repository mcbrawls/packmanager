package net.mcbrawls.packmanager.test

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.mcbrawls.packmanager.ResourcePackEnvironment
import net.minecraft.server.MinecraftServer.ServerResourcePackProperties
import net.minecraft.world.World
import java.util.UUID

object PackManagerTest : DedicatedServerModInitializer {
    private const val URL_1 = "https://cdn.mcbrawls.net/test-packs/demopack1.zip"
    private const val URL_2 = "https://cdn.mcbrawls.net/test-packs/demopack2.zip"
    private const val URL_3 = "https://cdn.mcbrawls.net/test-packs/demopack3.zip"

    override fun onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            val uuid1 = UUID.randomUUID()

            if (server is ResourcePackEnvironment) {
                server.addResourcePack(
                    ServerResourcePackProperties(
                        uuid1,
                        URL_1,
                        "",
                        true,
                        null
                    )
                )
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            val uuid2 = UUID.randomUUID()
            val uuid3 = UUID.randomUUID()

            val netherWorld = server.getWorld(World.NETHER)
            if (netherWorld is ResourcePackEnvironment) {
                netherWorld.addResourcePack(
                    ServerResourcePackProperties(
                        uuid2,
                        URL_2,
                        "",
                        true,
                        null
                    )
                )
            }

            val endWorld = server.getWorld(World.END)
            if (endWorld is ResourcePackEnvironment) {
                endWorld.addResourcePack(
                    ServerResourcePackProperties(
                        uuid3,
                        URL_3,
                        "",
                        true,
                        null
                    )
                )
            }
        }
    }
}
