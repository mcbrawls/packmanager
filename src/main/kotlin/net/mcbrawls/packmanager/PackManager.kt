package net.mcbrawls.packmanager

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.SendResourcePackTask
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import org.slf4j.LoggerFactory

object PackManager : DedicatedServerModInitializer {
    const val MOD_ID = "packmanager"
    const val MOD_NAME = "Pack Manager"

    private val logger = LoggerFactory.getLogger(MOD_NAME)

    override fun onInitializeServer() {
        logger.info("Initializing $MOD_NAME")

        registerEvents()
    }

    private fun registerEvents() {
        // register configuration event
        ServerConfigurationConnectionEvents.CONFIGURE.register { handler, server ->
            if (server is ResourcePackEnvironment) {
                // send all server pack tasks
                val packs = server.resourcePacks
                packs.forEach { properties ->
                    val task = SendResourcePackTask(properties)
                    handler.addTask(task)
                }
            }
        }

        // register join event
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val player = handler.player

            // compile pack set
            val packs = buildSet {
                val world = player.world
                if (world is ResourcePackEnvironment) {
                    addAll(world.resourcePacks)
                }

                // server resource packs applied at configuration phase
                if (server is ResourcePackEnvironment) {
                    subtract(server.resourcePacks)
                }
            }

            // send all packs
            packs.forEach { properties ->
                val packet = ResourcePackEnvironment.createSendPacket(properties)
                handler.sendPacket(packet)
            }
        }

        // register change world event
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register { player, origin, destination ->
            onWorldChange(player, origin, destination)
        }

        // register respawn event
        ServerPlayerEvents.AFTER_RESPAWN.register { oldPlayer, newPlayer, _ ->
            val origin = oldPlayer.serverWorld
            val destination = newPlayer.serverWorld
            if (origin != destination) {
                onWorldChange(newPlayer, origin, destination)
            }
        }
    }

    /**
     * A procedure called on any world change.
     */
    private fun onWorldChange(player: ServerPlayerEntity, origin: ServerWorld, destination: ServerWorld) {
        // always true, just check for compiler
        if (origin is ResourcePackEnvironment && destination is ResourcePackEnvironment) {
            val originPacks = origin.resourcePacks
            val destinationPacks = destination.resourcePacks
            val server = player.server

            // remove old packs
            val packsToRemove = buildSet {
                addAll(originPacks)
                subtract(destinationPacks)

                // server resource packs do not need to be modified here
                if (server is ResourcePackEnvironment) {
                    subtract(server.resourcePacks)
                }
            }

            packsToRemove.forEach { properties ->
                val packet = ResourcePackEnvironment.createRemovePacket(properties.id)
                player.networkHandler.sendPacket(packet)
            }

            // add new packs
            val packsToAdd = buildSet {
                addAll(destinationPacks)
                subtract(originPacks)

                // server resource packs do not need to be modified here
                if (server is ResourcePackEnvironment) {
                    subtract(server.resourcePacks)
                }
            }

            packsToAdd.forEach { properties ->
                val packet = ResourcePackEnvironment.createSendPacket(properties)
                player.networkHandler.sendPacket(packet)
            }
        }
    }
}
