package net.mcbrawls.packmanager

import net.fabricmc.api.DedicatedServerModInitializer
import org.slf4j.LoggerFactory

object PackManager : DedicatedServerModInitializer {
    const val MOD_ID = "packmanager"
    const val MOD_NAME = "Pack Manager"

    private val logger = LoggerFactory.getLogger(MOD_NAME)

    override fun onInitializeServer() {
        logger.info("Initializing $MOD_NAME")
    }
}
