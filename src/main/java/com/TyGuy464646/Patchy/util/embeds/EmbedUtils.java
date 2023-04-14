package com.TyGuy464646.Patchy.util.embeds;

import com.TyGuy464646.Patchy.data.cache.NPC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class storing helpful methods for embeds.
 *
 * @author TyGuy464646
 */
public class EmbedUtils {

    // Emojis
    public static final String GREEN_TICK = ":white_check_mark:";
    public static final String RED_X = ":x:";
    public static final String CYCLONE = ":cyclone:";
    public static final String STAR = ":eight_pointed_black_star:";
    public static final String CHECK = ":white_check_mark:";

    /**
     * Quickly creates a simple error embed
     *
     * @param errorMessage message to be displayed
     * @return completed {@link MessageEmbed}
     */
    public static MessageEmbed createError(String errorMessage) {
        return new EmbedBuilder()
                .setColor(EmbedColor.ERROR.color)
                .setDescription(RED_X + " **Error** | `" + errorMessage + "`")
                .build();
    }

    /**
     * Quickly creates a simple default embed
     *
     * @param message message to be displayed
     * @return completed {@link MessageEmbed}
     */
    public static MessageEmbed createDefault(String message) {
        return new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setDescription("`" + message + "`")
                .build();
    }

    /**
     * Quickly creates a simple success embed
     *
     * @param message message to be displayed
     * @return completed {@link MessageEmbed}
     */
    public static MessageEmbed createSuccess(String message) {
        return new EmbedBuilder()
                .setColor(EmbedColor.SUCCESS.color)
                .setDescription(GREEN_TICK + " **Success** | `" + message + "`")
                .build();
    }

    /**
     * Builds an embed for the NPC info command.
     *
     * @param infoNPC  The NPC to build the embed for.
     * @param isNewNPC Whether the NPC is new.
     * @return The built embed.
     */
    public static MessageEmbed createNPCInfo(NPC infoNPC, boolean isNewNPC) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setTitle(infoNPC.getFirstName() + " " + infoNPC.getLastName())
                .setDescription("```ㅤ```")
                .addField("Description", "`" + infoNPC.getDescription() + "`", false)
                .addField("Gender", "`" + infoNPC.getGender() + "`", true)
                .addField("Age", infoNPC.getAge() != -1 ? "`" + String.valueOf(infoNPC.getAge()) + "`" : "`N/A`", true)
                .addField("Alignment", "`" + infoNPC.getAlignment() + "`", true)
                .addField("Faction", "`" + infoNPC.getFaction() + "`", true)
                .addField("Attractiveness", infoNPC.getAttractiveness() != -1 ? "`" + infoNPC.getAttractiveness() + "/10`" : "`N/A`", true)
                .addField("", "", true)
                .setImage(infoNPC.getMugShot());

        if (isNewNPC)
            embed.setAuthor("New NPC", "https://www.dndbeyond.com/classes", "https://cdn2.iconfinder.com/data/icons/material-set-2-4/48/145-512.png");

        return embed.build();
    }
}
