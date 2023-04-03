package com.TyGuy464646.Patchy.handlers;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.data.cache.NPC;
import com.TyGuy464646.Patchy.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Handles characters for a given guild.
 *
 * @author TyGuy464646
 */
public class CharacterHandler {

    private final Guild guild;
    private final Patchy bot;

    // NPC to be confirmed in /npc create
    private NPC confirmNPC;

    /**
     * Constructor
     * @param bot The bot
     * @param guild The specific guild
     */
    public CharacterHandler(Patchy bot, Guild guild) {
        this.guild = guild;
        this.bot = bot;
    }

    /**
     * Finds an NPC given the first and last name
     * @param firstName The first name of the NPC
     * @return {@link NPC}
     */
    public NPC findNPCByFirstLastName(String firstName, String lastName) {
        return bot.database.npc.find(
                and(
                        eq("guild", guild.getIdLong()),
                        eq("firstName", firstName),
                        eq("lastName", lastName)
                )).first();
    }

    /**
     * Removes an NPC from the list.
     * @param firstName First name of the NPC
     */
    public void remove(String firstName, String lastName) {
        bot.database.npc.deleteOne(
                and(
                    eq("firstName", firstName),
                    eq("lastName", lastName)
                )
        );
    }

    /**
     * Resets all NPC data locally and in MongoDB
     */
    public void reset() {
        bot.database.npc.deleteMany(eq("guild", guild.getIdLong()));
    }

    /**
     * Returns a list of {@link NPC} objects stored in the database.
     * @return the list of {@link NPC} objects.
     */
    public List<NPC> getNPCs() {
        List<NPC> npcs = new ArrayList<>();
        bot.database.npc.find(
                eq("guild", guild.getIdLong()))
                .into(npcs);
        
        return npcs;
    }

    public void setConfirmNPC(NPC npc) {
        confirmNPC = npc;
    }

    public void confirmNPC() {
        if (confirmNPC == null) return;

        bot.database.npc.insertOne(confirmNPC);
        resetConfirmNPC();
    }

    public void resetConfirmNPC() {
        confirmNPC = null;
    }
}
