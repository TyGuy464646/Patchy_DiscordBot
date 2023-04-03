package com.TyGuy464646.Patchy.data.cache;

/**
 * POJO object that stores config data for a guild.
 *
 * @author TyGuy464646
 */
public class Config {

    // ID of guild it belongs to
    private long guild;

    // ID of channel that NPC's are sent to
    private Long npcChannel;

    // For POJO
    public Config() {
    }

    // Constructor
    public Config(long guild) {
        this.guild = guild;
        this.npcChannel = null;
    }

    // Getters and Setters
    public long getGuild() {
        return guild;
    }

    public void setGuild(long guild) {
        this.guild = guild;
    }

    public Long getNpcChannel() {
        return npcChannel;
    }

    public void setNpcChannel(Long npcChannel) {
        this.npcChannel = npcChannel;
    }
}
