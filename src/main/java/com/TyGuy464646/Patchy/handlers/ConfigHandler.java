package com.TyGuy464646.Patchy.handlers;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.data.cache.Config;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;

/**
 * Handles config settings for a given guild.
 *
 * @author TyGuy464646
 */
public class ConfigHandler {

    private final Guild guild;
    private final Patchy bot;
    private Config config;

    /**
     * Constructor
     *
     * @param bot   The bot
     * @param guild The specific guild
     */
    public ConfigHandler(Patchy bot, Guild guild) {
        this.guild = guild;
        this.bot = bot;

        // Get POJO object from database
        this.config = bot.database.config.find(eq("guild", guild.getIdLong())).first();
        if (this.config == null) {
            this.config = new Config(guild.getIdLong());
            bot.database.config.insertOne(config);
        }
    }

    /**
     * Gets the NPC channel for the guild.
     *
     * @return The NPC channel ID
     */
    public Long getNpcChannel() {
        return bot.database.config.find(
                eq("guild", guild.getIdLong())
        ).first().getNpcChannel();
    }

    /**
     * Sets the NPC channel for the guild.
     *
     * @param channelID The channel ID
     */
    public void setNpcChannel(long channelID) {
        Bson update = Updates.set("npcChannel", channelID);
        bot.database.config.updateOne(eq("guild", guild.getIdLong()), update);
    }

    /**
     * Resets the NPC channel for the guild.
     */
    public void resetNpcChannel() {
        Bson update = Updates.unset("npcChannel");
        bot.database.config.updateOne(eq("guild", guild.getIdLong()), update);
    }

    /**
     * Checks if the NPC channel is set.
     *
     * @return True if set, false if not
     */
    public boolean isNpcChannelSet() {
        return bot.database.config.find(
                eq("guild", guild.getIdLong())
        ).first().getNpcChannel() != null;
    }
}
