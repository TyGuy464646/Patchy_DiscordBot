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
     * @param infoNPC The NPC to build the embed for.
     * @param isNewNPC Whether the NPC is new.
     * @return The built embed.
     */
    public static MessageEmbed createNPCInfo(NPC infoNPC, boolean isNewNPC) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setTitle(infoNPC.getFirstName() + " " + infoNPC.getLastName())
                .setDescription("```ㅤ```")
                .addField("Description", infoNPC.getDescription(), false)
                .addField("Gender", infoNPC.getGender(), true)
                .addField("Age", infoNPC.getAge() != -1 ? String.valueOf(infoNPC.getAge()) : "N/A", true)
                .addField("Alignment", infoNPC.getAlignment(), true)
                .addField("Faction", infoNPC.getFaction(), true)
                .addField("Attractiveness", infoNPC.getAttractiveness() != -1 ? String.valueOf(infoNPC.getAttractiveness()) + "/10" : "N/A", true)
                .addField("", "", true)
                .setImage(infoNPC.getMugShot());

        if (isNewNPC)
            embed.setAuthor("New NPC", "https://www.dndbeyond.com/classes", "https://cdn2.iconfinder.com/data/icons/material-set-2-4/48/145-512.png");

        return embed.build();
    }

    /**
     * Builds an embed for the NPC create menu.
     * @param step The step that the user is on.
     * @return The built embed.
     */
    public static List<MessageEmbed> createNPCCreate(int step) {
        StringBuilder todoStringBuilder = new StringBuilder();
        StringBuilder infoStringBuilder = new StringBuilder();

        EmbedBuilder todoEmbed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setTitle("NPC Creation");

        EmbedBuilder infoEmbed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color);

        switch (step) {
            case 0 -> {
                todoStringBuilder.append(CYCLONE + " **Step 0 | Welcome to the NPC editor!**\n")
                        .append(STAR + " Step 1: Create or Edit an NPC\n")
                        .append(STAR + " Step 2: Set the NPC's name\n")
                        .append(STAR + " Step 3: Set the NPC's description\n")
                        .append(STAR + " Step 4: Optional details\n");

                infoEmbed.setTitle("**Welcome to the NPC editor!**");
                infoStringBuilder.append("I will guide you through creating a new NPC. It is very easy to set up, and you can always edit the NPC later.\n\n")
                        .append("Here are some things to note:\n")
                        .append("• NPCs can have multiple characteristics (e.g. a name, a description, an age, etc.), but only ")
                        .append("the **name and description are required**.\n")
                        .append("• You can always **cancel** the creation process by pressing the `cancel` button.\n")
                        .append("• You can always **go back** to a previous step by pressing the `back` button.\n\n")
                        .append("Click the buttons below to navigate through the steps. Need help? Contact the bot owner or use the `help` command.\n\n");
            }
            case 1 -> {
                todoStringBuilder.append(CHECK + " Step 0: Welcome to the NPC editor!\n")
                        .append(CYCLONE + " **Step 1 | Create or Edit an NPC**\n")
                        .append(STAR + " Step 2: Set the NPC's name\n")
                        .append(STAR + " Step 3: Set the NPC's description\n")
                        .append(STAR + " Step 4: Optional details\n");

                infoEmbed.setTitle("**Create or Edit an NPC**");
            }
            case 2 -> {
                todoStringBuilder.append(CHECK + " Step 0: Welcome to the NPC editor!\n")
                        .append(CHECK + " Step 1: Create or Edit an NPC\n")
                        .append(CYCLONE + " **Step 2 | Set the NPC's name**\n")
                        .append(STAR + " Step 3: Set the NPC's description\n")
                        .append(STAR + " Step 4: Optional details\n");

                infoEmbed.setTitle("**Set the NPC's name**");
            }
            case 3 -> {
                todoStringBuilder.append(CHECK + " Step 0: Welcome to the NPC editor!\n")
                        .append(CHECK + " Step 1: Create or Edit an NPC\n")
                        .append(CHECK + " Step 2: Set the NPC's name\n")
                        .append(CYCLONE + " **Step 3 | Set the NPC's description**\n")
                        .append(STAR + " Step 4: Optional details\n");

                infoEmbed.setTitle("**Set the NPC's description**");
            }
            case 4 -> {
                todoStringBuilder.append(CHECK + " Step 0: Welcome to the NPC editor!\n")
                        .append(CHECK + " Step 1: Create or Edit an NPC\n")
                        .append(CHECK + " Step 2: Set the NPC's name\n")
                        .append(CHECK + " Step 3: Set the NPC's description\n")
                        .append(CYCLONE + " **Step 4 | Optional details**\n");

                infoEmbed.setTitle("**Optional details**");
            }
        }

        todoEmbed.setDescription(todoStringBuilder.toString());
        infoEmbed.setDescription(infoStringBuilder.toString());

        return new ArrayList<>(List.of(todoEmbed.build(), infoEmbed.build()));
    }
}
