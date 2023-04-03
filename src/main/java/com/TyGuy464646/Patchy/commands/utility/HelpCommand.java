package com.TyGuy464646.Patchy.commands.utility;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.commands.CommandRegistry;
import com.TyGuy464646.Patchy.listeners.ButtonListener;
import com.TyGuy464646.Patchy.util.embeds.EmbedColor;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.TyGuy464646.Patchy.commands.CommandRegistry.commands;

/**
 * {@link Command} that displays a list of all available commands.
 *
 * @author TyGuy464646
 */
public class HelpCommand extends Command {

    private static final int COMMANDS_PER_PAGE = 6;

    public HelpCommand(Patchy bot) {
        super(bot);

        this.name = "help";
        this.description = "Display a list of all commands and categories.";
        this.category = Category.UTILITY;

        OptionData data = new OptionData(OptionType.STRING, "category", "See commands under a specific category.");
        for (Category c : Category.values()) {
            String name = c.name.toLowerCase();
            data.addChoice(name, name);
        }

        this.args.add(data);
        this.args.add(new OptionData(OptionType.STRING, "command", "See details for a specific command"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Create a sorted hashmap that groups commands by category
        HashMap<Category, List<Command>> categories = new LinkedHashMap<>();
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(EmbedColor.DEFAULT.color);
        for (Category category : Category.values()) {
            categories.put(category, new ArrayList<>());
        }
        for (Command command : commands) {
            categories.get(command.category).add(command);
        }

        OptionMapping categoryOption = event.getOption("category");
        OptionMapping commandOption = event.getOption("command");
        if (categoryOption != null && commandOption != null) {
            event.replyEmbeds(EmbedUtils.createError("Please only give one optional argument and try again.")).queue();
        } else if (categoryOption != null) {
            // Display category command menu
            Category category = Category.valueOf(categoryOption.getAsString().toUpperCase());
            List<MessageEmbed> embeds = buildCategoryMenu(category, categories.get(category));

            if (embeds.isEmpty()) {
                // No commands for this category
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(category.emoji + " **%s Commands**".formatted(category.name))
                        .setDescription("Coming soon...")
                        .setColor(EmbedColor.DEFAULT.color);
                event.replyEmbeds(embed.build()).queue();
                return;
            }

            // Send paginated help menu
            ReplyCallbackAction action = event.replyEmbeds(embeds.get(0));
            if (embeds.size() > 1) {
                ButtonListener.sendPaginatedMenu(event.getUser().getId(), action, embeds);
                return;
            }
            action.queue();
        } else if (commandOption != null) {
            // Display command details menu
            Command cmd = CommandRegistry.commandsMap.get(commandOption.getAsString().toLowerCase());

            if (cmd != null) {
                embedBuilder.setTitle("Command: " + cmd.name);
                embedBuilder.setDescription(cmd.description);
                StringBuilder usages = new StringBuilder();
                if (cmd.subCommands.isEmpty())
                    usages.append("`").append(getUsage(cmd)).append("`");
                else {
                    for (SubcommandData sub : cmd.subCommands)
                        usages.append("`").append(getUsage(sub, cmd.name)).append("`\n");
                }

                embedBuilder.addField("Usage:", usages.toString(), false);
                embedBuilder.addField("Permission: ", getPermission(cmd), false);
                event.replyEmbeds(embedBuilder.build()).queue();
            } else
                // Command specified doesn't exist
                event.replyEmbeds(
                                EmbedUtils.createError("No command called \"" + commandOption.getAsString() + "\" found."))
                        .queue();
        } else {
            // Display default menu
            embedBuilder.setTitle("Patchy Commands");
            categories.forEach((category, commands) -> {
                String categoryName = category.name().toLowerCase();
                String value = "`/help " + categoryName + "`";
                embedBuilder.addField(category.emoji + " " + category.name, value, true);
            });
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
    }

    /**
     * Builds a menu with all the commands in a specified category.
     *
     * @param category The category to build a menu for
     * @param commands A list of the commands in the category
     * @return A list of {@link MessageEmbed} objects for pagination
     */
    private List<MessageEmbed> buildCategoryMenu(Category category, List<Command> commands) {
        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(category.emoji + " **%s Commands**".formatted(category.name));
        embedBuilder.setColor(EmbedColor.DEFAULT.color);

        int counter = 0;
        for (Command cmd : commands) {
            if (cmd.subCommands.isEmpty()) {
                embedBuilder.appendDescription("`" + getUsage(cmd) + "`\n" + cmd.description + "\n\n");
                counter++;

                if (counter % COMMANDS_PER_PAGE == 0) {
                    embeds.add(embedBuilder.build());
                    embedBuilder.setDescription("");
                    counter = 0;
                }
            } else {
                for (SubcommandData sub : cmd.subCommands) {
                    embedBuilder.appendDescription("`" + getUsage(sub, cmd.name) + "`\n" + sub.getDescription() + "\n\n");
                    counter++;

                    if (counter % COMMANDS_PER_PAGE == 0) {
                        embeds.add(embedBuilder.build());
                        embedBuilder.setDescription("");
                        counter = 0;
                    }
                }
            }
        }

        if (counter != 0) embeds.add(embedBuilder.build());
        return embeds;
    }

    /**
     * Creates a string of {@link Command} usage.
     *
     * @param cmd Command to build usage for.
     * @return String with name and args stitiched together.
     */
    private String getUsage(Command cmd) {
        StringBuilder usage = new StringBuilder("/" + cmd.name);

        if (cmd.args.isEmpty()) return usage.toString();
        for (int i = 0; i < cmd.args.size(); i++) {
            boolean isRequired = cmd.args.get(i).isRequired();
            if (isRequired) usage.append(" <");
            else usage.append(" [");

            usage.append(cmd.args.get(i).getName());
            if (isRequired) usage.append(">");
            else usage.append("]");
        }

        return usage.toString();
    }

    /**
     * Creates a string of {@link SubcommandData} usage.
     *
     * @param cmd         sub command data from a command.
     * @param commandName the name of the root command.
     * @return String with name and args stitched together.
     */
    private String getUsage(SubcommandData cmd, String commandName) {
        StringBuilder usage = new StringBuilder("/" + commandName + " " + cmd.getName());

        if (cmd.getOptions().isEmpty()) return usage.toString();
        for (OptionData arg : cmd.getOptions()) {
            boolean isRequired = arg.isRequired();
            if (isRequired) usage.append(" <");
            else usage.append(" [");

            usage.append(arg.getName());
            if (isRequired) usage.append(">");
            else usage.append("]");
        }

        return usage.toString();
    }

    /**
     * Builds a string of permissions from command.
     *
     * @param cmd the command to draw permissions from
     * @return A string of command permissions.
     */
    private String getPermission(Command cmd) {
        if (cmd.permission == null) return "None";

        return cmd.permission.getName();
    }
}
