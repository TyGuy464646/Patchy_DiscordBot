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

    private static final int EMBEDS_PER_PAGE = 4;

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
                        new OptionData(OptionType.STRING, "gender", "The gender of the NPC")
                                .addChoice("Male", "Male")
                                .addChoice("Female", "Female"),
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

                        gender != null ? gender.getAsString() : "N/A",
                        age != null ? age.getAsInt() : -1,
                        alignment != null ? alignment.getAsString() : "N/A",
                        faction != null ? faction.getAsString() : "N/A",
                        attractiveness != null ? attractiveness.getAsInt() : -1,
                        mugShot != null ? mugShot.getAsString() : null
                );
                characterHandler.setConfirmNPC(confirmNPC);

                WebhookMessageCreateAction<Message> action = event.getHook().sendMessageEmbeds(
                        characterHandler.buildListEmbed(confirmNPC),
                        EmbedUtils.createDefault("Are you sure you want to add this NPC to the database?")
                );
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

                event.getHook().sendMessageEmbeds(characterHandler.buildListEmbed(infoNPC)).queue();
            }
            case "list" -> {
                List<List<MessageEmbed>> pages = buildNPCMenu(characterHandler.getNPCs());

                if (pages.isEmpty()) {
                    // No NPCs
                    EmbedBuilder embed = new EmbedBuilder()
                            .setColor(EmbedColor.DEFAULT.color)
                            .setTitle("NPCs")
                            .setDescription("Coming soon...");
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                    return;
                }

                // Send paginated help menu
                WebhookMessageCreateAction<Message> action = event.getHook().sendMessageEmbeds(pages.get(0));
                if (pages.size() > 1) {
                    ButtonListener.sendEmbedPaginatedMenu(event.getUser().getId(), action, pages);
                    return;
                }
                action.queue();
            }
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
        CharacterHandler characterHandler = GuildData.get(event.getGuild()).characterHandler;

        if (event.getName().equals("npc")) {
            if (event.getSubcommandName().equals("remove") || event.getSubcommandName().equals("info")) {
                List<NPC> npcs = characterHandler.getNPCs();

                List<Choice> choices = new ArrayList<>();
                for (NPC npc : npcs) {
                    choices.add(new Choice(npc.getFirstName() + " " + npc.getLastName(), npc.getFirstName() + ":" + npc.getLastName()));
                }

                event.replyChoices(choices).queue();
            }
        }
    }

    /**
     * Builds a list of pages for the NPC list.
     *
     * @param npcs The list of NPCs to build the pages from.
     * @return A list of pages.
     */
    private List<List<MessageEmbed>> buildNPCMenu(List<NPC> npcs) {
        List<List<MessageEmbed>> pages = new ArrayList<>();

        List<MessageEmbed> page = new ArrayList<>();
        for (int i = 0; i < npcs.size(); i++) {
            NPC npc = npcs.get(i);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(EmbedColor.DEFAULT.color)
                    .setAuthor(npc.getFirstName() + " " + npc.getLastName())
                    .addField("Gender", npc.getGender(), true)
                    .addField("Age", npc.getAge() != -1 ? String.valueOf(npc.getAge()) : "N/A", true)
                    .addField("Attractiveness", npc.getAttractiveness() != -1 ? String.valueOf(npc.getAttractiveness()) + "/10" : "N/A", true)
                    .setThumbnail(npc.getMugShot());

            page.add(embedBuilder.build());

            if (page.size() == EMBEDS_PER_PAGE || i == npcs.size() - 1) {
                pages.add(page);
                page = new ArrayList<>();
            }
        }

        return pages;
    }
}
