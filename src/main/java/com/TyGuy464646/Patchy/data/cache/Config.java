package com.TyGuy464646.Patchy.data.cache;

/**
 * POJO object that stores config data for a guild.
 *
 * @author TyGuy464646
 */
public class Config {

    private long guild;

    public Config() {

    }

    public Config(long guild) {
        this.guild = guild;
    }

    // Getters and Setters
    public long getGuild() { return guild; }
    public void setGuild(long guild) { this.guild = guild; }
}
