package com.TyGuy464646.Patchy.commands.npc;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.data.cache.NPC;
import com.TyGuy464646.Patchy.handlers.CharacterHandler;
import com.TyGuy464646.Patchy.listeners.ButtonListener;
import com.TyGuy464646.Patchy.util.embeds.EmbedColor;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Command} that adds/removes NPC characters from a database
 *
 * @author TyGuy464646
 */
public class npcCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(npcCommand.class);

    public npcCommand(Patchy bot) {
        super(bot);

        this.name = "npc";
        this.description = "Add/Remove important NPCs.";
        this.category = Category.NPC;
        this.permission = Permission.MESSAGE_SEND;

        this.subCommands.add(new SubcommandData("create", "Create an NPC.")
                .addOptions(
                        new OptionData(OptionType.STRING, "first_name", "The first name of the NPC", true),
                        new OptionData(OptionType.STRING, "last_name", "The last name of the NPC", true),
                        new OptionData(OptionType.STRING, "description", "The description of the NPC", true),
                        new OptionData(OptionType.STRING, "gender", "The gender of the NPC"),
                        new OptionData(OptionType.INTEGER, "age", "The age of the NPC"),
                        new OptionData(OptionType.STRING, "alignment", "The alignment of the NPC"),
                        new OptionData(OptionType.STRING, "faction", "The faction the NPC belongs to"),
                        new OptionData(OptionType.INTEGER, "attractiveness", "The attractiveness of the NPC"),
                        new OptionData(OptionType.STRING, "mug_shot", "The mug shot of the NPC")
                ));
        this.subCommands.add(new SubcommandData("edit", "Edit an existing NPC's info")
                .addOptions(
                        new OptionData(OptionType.STRING, "name", "The name of the NPC you wish to edit.", true)
                ));
        this.subCommands.add(new SubcommandData("remove", "Remove an NPC from the list.")
                .addOptions(
                        new OptionData(OptionType.STRING, "name", "The name of the NPC you wish to remove.", true, true)
                ));
        this.subCommands.add(new SubcommandData("info", "Displays the full information of an NPC.")
                .addOptions(
                        new OptionData(OptionType.STRING, "name", "The name of the NPC you want info on.", true, true)
                ));
        this.subCommands.add(new SubcommandData("list", "List all NPCs."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        // TODO add channel option
        CharacterHandler characterHandler = GuildData.get(event.getGuild()).characterHandler;

        switch (event.getSubcommandName()) {
            case "create" -> {
                OptionMapping firstName = event.getOption("first_name");
                OptionMapping lastName = event.getOption("last_name");
                OptionMapping description = event.getOption("description");

                OptionMapping gender = event.getOption("gender");
                OptionMapping age = event.getOption("age");
                OptionMapping alignment = event.getOption("alignment");
                OptionMapping faction = event.getOption("faction");
                OptionMapping attractiveness = event.getOption("attractiveness");
                OptionMapping mugShot = event.getOption("mug_shot");

                // TODO: Send confirmation buttons if character wants to be added, THEN add to database

                NPC confirmNPC = new NPC(
                        event.getGuild().getIdLong(),
                        firstName.getAsString(),
                        lastName.getAsString(),
                        description.getAsString(),

                        gender != null ? gender.getAsString() : "No Gender Given.",
                        age != null ? age.getAsInt() : -1,
                        alignment != null ? alignment.getAsString() : "No Alignment Given.",
                        faction != null ? faction.getAsString() : "No Faction Given.",
                        attractiveness != null ? attractiveness.getAsInt() : -1,
                        mugShot != null ? mugShot.getAsString() : null
                );
                characterHandler.setConfirmNPC(confirmNPC);

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(EmbedColor.DEFAULT.color)
                        .setTitle(confirmNPC.getFirstName() + " " + confirmNPC.getLastName())
                        .setDescription("```ㅤ```")
                        .addField("Description", confirmNPC.getDescription(), false)
                        .addField("Gender", confirmNPC.getGender(), true);

                if (confirmNPC.getAge() != -1)
                    embedBuilder.addField("Age", String.valueOf(confirmNPC.getAge()), true);
                embedBuilder.addField("Alignment", confirmNPC.getAlignment(), true)
                        .addField("Faction", confirmNPC.getFaction(), true);
                if (confirmNPC.getAttractiveness() != -1)
                    embedBuilder.addField("Attractiveness", String.valueOf(confirmNPC.getAttractiveness() + "/10"), true);
                if (confirmNPC.getMugShot() != null)
                    embedBuilder.setImage(confirmNPC.getMugShot());
                if (confirmNPC.getAge() != -1 && confirmNPC.getAttractiveness() != -1) {
                    embedBuilder.addField("", "", true);
                }

                MessageEmbed confirmEmbed = EmbedUtils.createDefault("Are you sure you want to add this NPC to the database?");

                List<MessageEmbed> embedList = new ArrayList<>();
                embedList.add(embedBuilder.build());
                embedList.add(confirmEmbed);

                WebhookMessageCreateAction<Message> action = event.getHook().sendMessageEmbeds(embedList);
                ButtonListener.sendConfirmationMenu(event.getUser().getId(), "npc", action);
            }
            case "edit" -> {

            }
            case "remove" -> {
                OptionMapping name = event.getOption("name");

                String[] firstLastName = name.getAsString().split(":");
                characterHandler.remove(firstLastName[0], firstLastName[1]);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Removed " + firstLastName[0] + " " + firstLastName[1])).queue();
            }
            case "info" -> {
                OptionMapping name = event.getOption("name");

                String[] firstLastName = name.getAsString().split(":");
                NPC infoNPC = characterHandler.findNPCByFirstLastName(firstLastName[0], firstLastName[1]);

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(EmbedColor.DEFAULT.color)
                        .setTitle(infoNPC.getFirstName() + " " + infoNPC.getLastName())
                        .addField("Description", infoNPC.getDescription(), false)
                        .addField("Faction", infoNPC.getFaction(), false)
                        .setImage(infoNPC.getMugShot());

                event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }
            case "list" -> {
                // TODO: Add sorting option, by name and by faction
            }
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
        CharacterHandler characterHandler = GuildData.get(event.getGuild()).characterHandler;

        if (event.getName().equals("npc") && event.getSubcommandName().equals("remove")) {
            List<NPC> npcs = characterHandler.getNPCs();

            List<Choice> choices = new ArrayList<>();
            for (NPC npc : npcs) {
                choices.add(new Choice(npc.getFirstName() + " " + npc.getLastName(), npc.getFirstName() + ":" + npc.getLastName()));
            }

            event.replyChoices(choices).queue();
        }

        else if (event.getName().equals("npc") && event.getSubcommandName().equals("info")) {
            List<NPC> npcs = characterHandler.getNPCs();

            List<Choice> choices = new ArrayList<>();
            for (NPC npc : npcs) {
                choices.add(new Choice(npc.getFirstName() + " " + npc.getLastName(), npc.getFirstName() + ":" + npc.getLastName()));
            }

            event.replyChoices(choices).queue();
        }
    }
}
