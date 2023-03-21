package com.TyGuy464646.Patchy.data.cache;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO object that represents an NPC.
 *
 * @author TyGuy464646
 */
public class NPC {

    // ID of guild it belongs to
    private long guild;

    // first name
    private String firstName;

    // last name
    private String lastName;

    // description
    private String description;

    // faction
    private String faction;

    // TODO: Add attractiveness scale from 1/10

    // mug shot
    private String mugShot;

    /**
     * For POJO
     */
    public NPC() {}

    /**
     * Constructor
     * @param guild Guild the NPC belongs to
     * @param firstName First name of the NPC
     * @param lastName Last name of the NPC
     * @param description Description of the NPC
     * @param faction Faction of the NPC
     * @param mugShot Mug shot of the NPC
     */
    public NPC(long guild, String firstName, String lastName, String description, String faction, String mugShot) {
        this.guild = guild;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.faction = faction;
        this.mugShot = mugShot;
    }

    // Getters and Setters
    public long getGuild() {
        return guild;
    }

    public void setGuild(long guild) {
        this.guild = guild;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getMugShot() {
        return mugShot;
    }

    public void setMugShot(String mugShot) {
        this.mugShot = mugShot;
    }
}
