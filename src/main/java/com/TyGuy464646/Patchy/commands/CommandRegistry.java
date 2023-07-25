package com.TyGuy464646.Patchy.commands;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.music.*;
import com.TyGuy464646.Patchy.commands.npc.npcCommand;
import com.TyGuy464646.Patchy.commands.staff.resetCommand;
import com.TyGuy464646.Patchy.commands.staff.setupCommand;
import com.TyGuy464646.Patchy.commands.utility.AvatarCommand;
import com.TyGuy464646.Patchy.commands.utility.EmbedCommand;
import com.TyGuy464646.Patchy.commands.utility.HelpCommand;
import com.TyGuy464646.Patchy.commands.utility.RolesCommand;
import com.TyGuy464646.Patchy.data.GuildData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Registers, listens, and executes commands.
 *
 * @author TyGuy464646
 */
public class CommandRegistry extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    /**
     * List of commands in the exact order registered.
     */
    public static final List<Command> commands = new ArrayList<>();

    /**
     * Map of command names to command objects.
     */
    public static final Map<String, Command> commandsMap = new HashMap<>();

    /**
     * Adds commands to a global list and registers them as event listener.
     *
     * @param bot An instance of Patchy.
     */
    public CommandRegistry(Patchy bot) {
        mapCommand(
                // Music Commands
                new NowPlayingCommand(bot),
                new PauseCommand(bot),
                new PlayCommand(bot),
                new QueueCommand(bot),
                new RepeatCommand(bot),
                new ResumeCommand(bot),
                new SeekCommand(bot),
                new StopCommand(bot),
                new VolumeCommand(bot),

                // NPC Commands
                new npcCommand(bot),

                // Staff Commands
                new resetCommand(bot),
                new setupCommand(bot),

                // Utility Commands
                new AvatarCommand(bot),
                new EmbedCommand(bot),
                new RolesCommand(bot),
                new HelpCommand(bot)
        );
    }

    /**
     * Adds a {@link Command} to the static list and map.
     *
     * @param cmds a spread list of Command objects.
     */
    private void mapCommand(Command... cmds) {
        for (Command cmd : cmds) {
            commandsMap.put(cmd.name, cmd);
            commands.add(cmd);
        }
    }

    /**
     * Creates a list of {@link CommandData} for all commands.
     *
     * @return a list of {@link CommandData} to be used for registration.
     */
    public static List<CommandData> unpackCommandData() {
        // Register slash commands
        List<CommandData> commandData = new ArrayList<>();
        for (Command command : commands) {
            SlashCommandData slashCommand = Commands.slash(command.name, command.description).addOptions(command.args);
            if (command.permission != null)
                slashCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.permission));
            if (!command.subCommands.isEmpty())
                slashCommand.addSubcommands(command.subCommands);

            commandData.add(slashCommand);
        }

        return commandData;
    }

    /**
     * Runs whenever a slash command is run in Discord.
     *
     * @param event the event in which the slash command was ran.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Get command by name
        Command cmd = commandsMap.get(event.getName());
        if (cmd != null) {
            // Check for required bot permissions
            Role botRole = Objects.requireNonNull(event.getGuild()).getBotRole();
            if (cmd.botPermission != null) {
                if (Objects.requireNonNull(botRole).hasPermission(cmd.botPermission) && !botRole.hasPermission(Permission.ADMINISTRATOR)) {
                    String text = "I need the `" + cmd.botPermission.getName() + "` permission to execute that command.";
                    event.reply(text).setEphemeral(true).queue();
                    return;
                }
            }

            // Run command
            cmd.execute(event);
        }
    }

    /**
     * Runs whenever an isAutoComplete OptionData is called
     *
     * @param event the event in which the Auto Complete OptionData was called.
     */
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        // Get command by name
        Command cmd = commandsMap.get(event.getName());
        if (cmd != null) {
            // Check for required bot permissions
            Role botRole = Objects.requireNonNull(event.getGuild()).getBotRole();
            if (cmd.botPermission != null) {
                if (!Objects.requireNonNull(botRole).hasPermission(cmd.botPermission) && !botRole.hasPermission(Permission.ADMINISTRATOR)) {
                    return;
                }
            }

            // Run command
            cmd.autoCompleteExecute(event);
        }
    }

    /**
     * Registers slash commands as guild commands.
     * NOTE: May change to global commands on release.
     *
     * @param event executes when a guild is ready.
     */
    @Override
    public void onGuildReady(GuildReadyEvent event) {
        // Get GuildData from database
        GuildData.get(event.getGuild());

        // Register slash commands
        event.getGuild().updateCommands().addCommands(unpackCommandData()).queue();
        LOGGER.info("Guild commands have been updated.");
    }
}
