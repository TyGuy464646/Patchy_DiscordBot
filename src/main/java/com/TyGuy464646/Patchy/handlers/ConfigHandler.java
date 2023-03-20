package com.TyGuy464646.Patchy.handlers;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.data.cache.Config;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.conversions.Bson;

/**
 * Handles config settings for a given guild.
 *
 * @author TyGuy464646
 */
public class ConfigHandler {

    private final Guild guild;
    private final Patchy bot;
    private final Bson filter;
    private Config config;

    public ConfigHandler(Patchy bot, Guild guild) {
        this.guild = guild;
        this.bot = bot;

        // Get POJO object from database
        this.filter = Filters.eq("guild", guild.getIdLong());
        this.config = bot.database.config.find(filter).first();
        if (this.config == null) {
            this.config = new Config(guild.getIdLong());
            bot.database.config.insertOne(config);
        }
    }

    public Config getConfig() { return config; }
}
