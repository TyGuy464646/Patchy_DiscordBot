package com.TyGuy464646.Patchy.listeners;

import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.handlers.CharacterHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Listens for button input and handles all button backend.
 *
 * @author TyGuy464646
 */
public class ButtonListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonListener.class);

    // Minutes until buttons are disabled
    public static final int MINUTES_TO_DISABLE = 3;

    public static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);

    public static final Map<String, List<MessageEmbed>> menus = new HashMap<>();
    public static final Map<String, List<List<MessageEmbed>>> embedMenus = new HashMap<>();
    public static final Map<String, List<Button>> buttons = new HashMap<>();

    /**
     * Adds pagination buttons to a message action.
     *
     * @param userID the ID of the user who is accessing this menu.
     * @param action the {@link ReplyCallbackAction} to add components to.
     * @param embeds the embed pages.
     */
    public static void sendPaginatedMenu(String userID, ReplyCallbackAction action, List<MessageEmbed> embeds) {
        String uuid = userID + ":" + UUID.randomUUID();
        List<Button> components = getPaginationButtons(uuid, embeds.size());
        buttons.put(uuid, components);
        menus.put(uuid, embeds);
        action.addActionRow(components).queue(
                interactionHook -> ButtonListener.disableButtons(uuid, interactionHook)
        );
    }

    /**
     * Adds pagination buttons to a deferred reply message action.
     *
     * @param userID the ID of the user who is accessing this menu.
     * @param action the {@link WebhookMessageCreateAction} to add components to.
     * @param embeds The embed pages.
     */
    public static void sendPaginatedMenu(String userID, WebhookMessageCreateAction<Message> action, List<MessageEmbed> embeds) {
        String uuid = userID + ":" + UUID.randomUUID();
        List<Button> components = getPaginationButtons(uuid, embeds.size());
        buttons.put(uuid, components);
        menus.put(uuid, embeds);
        action.addActionRow(components).queue(
                interactionHook -> ButtonListener.disableButtons(uuid, interactionHook)
        );
    }

    /**
     * Adds pagination buttons to a deferred reply message action.
     * @param userID the ID of the user who is accessing this menu.
     * @param action The {@link WebhookMessageCreateAction} to add components to.
     * @param pages The embed pages.
     */
    public static void sendEmbedPaginatedMenu(String userID, WebhookMessageCreateAction<Message> action, List<List<MessageEmbed>> pages) {
        String uuid = userID + ":" + UUID.randomUUID();
        List<Button> components = getEmbedPaginationButtons(uuid, pages.size());

        buttons.put(uuid, components);
        embedMenus.put(uuid, pages);

        action.addActionRow(components);
        SelectionMenuListener.sendNPCListMenu(uuid, action, pages);
    }

    /**
     * Adds reset buttons to a deferred reply message action.
     *
     * @param userID     the ID of the user who is accessing this menu.
     * @param systemName The name of the system to reset.
     * @param action     The {@link WebhookMessageCreateAction} to add components to.
     */
    public static void sendResetMenu(String userID, String systemName, WebhookMessageCreateAction<Message> action) {
        String uuid = userID + ":" + UUID.randomUUID();
        List<Button> components = getResetButtons(uuid, systemName);
        buttons.put(uuid, components);
        action.addActionRow(components).queue(
                interactionHook -> ButtonListener.disableButtons(uuid, interactionHook)
        );
    }

    /**
     * Adds confirmation buttons to a deferred reply message action.
     * @param userID the ID of the user who is accessing this menu.
     * @param action The {@link WebhookMessageCreateAction} to add components to.
     */
    public static void sendConfirmationMenu(String userID, String systemName, WebhookMessageCreateAction<Message> action) {
        String uuid = userID + ":" + UUID.randomUUID();
        List<Button> components = getConfirmationButtons(uuid, systemName);
        buttons.put(uuid, components);
        action.addActionRow(components).queue(
                interactionHook -> ButtonListener.disableButtons(uuid, interactionHook)
        );
    }

    /**
     * Get a list of buttons for paginated embeds.
     * @param uuid the unique ID generated for these buttons.
     * @param size the total number of embed pages
     * @return A list of components to use on a paginated embed
     */
    private static List<Button> getPaginationButtons(String uuid, int size) {
        return Arrays.asList(
                Button.primary("pagination:prev:" + uuid, "Previous").asDisabled(),
                Button.of(ButtonStyle.SECONDARY, "pagination:page:0", "1/" + size).asDisabled(),
                Button.primary("pagination:next:" + uuid, "Next")
        );
    }

    /**
     * Get a list of buttons for paginated embeds.
     * @param uuid the unique ID generated for these buttons.
     * @param size the total number of embed pages
     * @return A list of components to use on a paginated embed
     */
    private static List<Button> getEmbedPaginationButtons(String uuid, int size) {
        return Arrays.asList(
                Button.primary("embedpagination:prev:" + uuid, "Previous").asDisabled(),
                Button.of(ButtonStyle.SECONDARY, "pagination:page:0", "1/" + size).asDisabled(),
                Button.primary("embedpagination:next:" + uuid, "Next")
        );
    }

    /**
     * Get a list of buttons for reset embeds (selectable yes and no).
     *
     * @param uuid       The unique ID generated for these buttons.
     * @param systemName The name of the system being reset.
     * @return A list of components to use on a reset embed.
     */
    private static List<Button> getResetButtons(String uuid, String systemName) {
        return Arrays.asList(
                Button.success("reset:yes:" + uuid + ":" + systemName, Emoji.fromUnicode("\u2714")),
                Button.danger("reset:no:" + uuid + ":" + systemName, Emoji.fromUnicode("\u2716"))
        );
    }

    /**
     * Get a list of buttons for confirmation embeds (selectable yes and no).
     * @param uuid The unique ID generated for these buttons.
     * @return A list of components to use on a confirmation embed.
     */
    private static List<Button> getConfirmationButtons(String uuid, String systemName) {
        return Arrays.asList(
                Button.success("confirm:yes:" + uuid + ":" + systemName, "Yes"),
                Button.danger("confirm:no:" + uuid + ":" + systemName, "No")
        );
    }

    /**
     * Schedules a timer task to disable buttons and clear cache after a set time.
     * @param uuid the uuid of the components to disable
     * @param interactionHook An {@link InteractionHook} pointing to original message
     */
    public static void disableButtons(String uuid, InteractionHook interactionHook) {
        Runnable task = () -> {
            interactionHook.editOriginalComponents(new ArrayList<>())
                    .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

            ButtonListener.buttons.remove(uuid);
            ButtonListener.menus.remove(uuid);
            ButtonListener.embedMenus.remove(uuid);
            SelectionMenuListener.selectMenus.remove(uuid);
        };

        ButtonListener.executor.schedule(task, MINUTES_TO_DISABLE, TimeUnit.MINUTES);
    }

    /**
     * Schedules a timer task to disable buttons and clear cache after a set time.
     *
     * @param uuid The uuid of the components to disable.
     * @param hook A {@link Message} hook pointing to original message.
     */
    public static void disableButtons(String uuid, Message hook) {
        Runnable task = () -> {
            hook.editMessageComponents(new ArrayList<>())
                    .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

            ButtonListener.buttons.remove(uuid);
            ButtonListener.menus.remove(uuid);
            ButtonListener.embedMenus.remove(uuid);
            SelectionMenuListener.selectMenus.remove(uuid);
        };

        ButtonListener.executor.schedule(task, MINUTES_TO_DISABLE, TimeUnit.MINUTES);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        // Check that these are 'help' buttons
        String[] pressedArgs = event.getComponentId().split(":");

        // Check if user owns this menu
        long userID = Long.parseLong(pressedArgs[2]);
        if (userID != event.getUser().getIdLong()) return;

        // Get other buttons
        String uuid = userID + ":" + pressedArgs[3];
        List<Button> components = buttons.get(uuid);
        List<StringSelectMenu> selectMenus = SelectionMenuListener.getSelectMenus().get(uuid);
        if (components == null) return;
        String[] storedArgs = components.get(0).getId().split(":");

        // Pagination
        if (pressedArgs[0].equals("pagination") && storedArgs[0].equals("pagination")) {
            // If next button is pressed
            if (pressedArgs[1].equals("next")) {
                // Move to next embed
                int page = Integer.parseInt(components.get(1).getId().split(":")[2]) + 1;
                List<MessageEmbed> embeds = menus.get(uuid);

                if (page < embeds.size()) {
                    // Update buttons
                    components.set(1, components.get(1).withId("pagination:page:" + page).withLabel((page + 1) + "/" + embeds.size()));
                    components.set(0, components.get(0).asEnabled());
                    if (page == embeds.size() - 1) components.set(2, components.get(2).asDisabled());

                    // Edit components to new components and change embed
                    buttons.put(uuid, components);
                    event.editComponents(ActionRow.of(components)).setEmbeds(embeds.get(page)).queue();
                }
            }
            // If previous button is pressed
            else if (pressedArgs[1].equals("prev")) {
                // Move to previous embed
                int page = Integer.parseInt(components.get(1).getId().split(":")[2]) - 1;
                List<MessageEmbed> embeds = menus.get(uuid);

                if (page >= 0) {
                    // Update buttons
                    components.set(1, components.get(1).withId("pagination:page:" + page).withLabel((page + 1) + "/" + embeds.size()));
                    components.set(2, components.get(2).asEnabled());
                    if (page == 0) components.set(0, components.get(0).asDisabled());

                    // Edit components to new components and change embed
                    buttons.put(uuid, components);
                    event.editComponents(ActionRow.of(components)).setEmbeds(embeds.get(page)).queue();
                }
            }
        }
        // Embed Pagination
        else if (pressedArgs[0].equals("embedpagination") && storedArgs[0].equals("embedpagination")) {
            // If next button is pressed
            if (pressedArgs[1].equals("next")) {
                // Move to next embed
                int page = Integer.parseInt(components.get(1).getId().split(":")[2]) + 1;
                List<List<MessageEmbed>> pages = embedMenus.get(uuid);

                if (page < pages.size()) {
                    // Update buttons
                    components.set(1, components.get(1).withId("embedpagination:page:" + page).withLabel((page + 1) + "/" + pages.size()));
                    components.set(0, components.get(0).asEnabled());

                    if (page == pages.size() - 1) components.set(2, components.get(2).asDisabled());

                    // Edit components to new components and change embed
                    buttons.put(uuid, components);
                    event.editComponents(
                            ActionRow.of(components),
                            ActionRow.of(selectMenus.get(page)))
                            .setEmbeds(pages.get(page))
                            .queue();
                }
            }
            // If previous button is pressed
            else if (pressedArgs[1].equals("prev")) {
                // Move to previous embed
                int page = Integer.parseInt(components.get(1).getId().split(":")[2]) - 1;
                List<List<MessageEmbed>> pages = embedMenus.get(uuid);

                if (page >= 0) {
                    // Update buttons
                    components.set(1, components.get(1).withId("embedpagination:page:" + page).withLabel((page + 1) + "/" + pages.size()));
                    components.set(2, components.get(2).asEnabled());
                    if (page == 0) components.set(0, components.get(0).asDisabled());

                    // Edit components to new components and change embed
                    buttons.put(uuid, components);
                    event.editComponents(
                            ActionRow.of(components),
                            ActionRow.of(selectMenus.get(page)))
                            .setEmbeds(pages.get(page))
                            .queue();
                }
            }
        }
        // Reset
        else if (pressedArgs[0].equals("reset") && storedArgs[0].equals("reset")) {
            String systemName = pressedArgs[4];

            // If yes button was pressed
            if (pressedArgs[1].equals("yes")) {
                event.deferEdit().queue();
                GuildData data = GuildData.get(event.getGuild());
                MessageEmbed embed = null;

                if (systemName.equalsIgnoreCase("npc")) {
                    data.characterHandler.reset();
                    embed = EmbedUtils.createSuccess(systemName + " system was successfully reset!");
                }
                else if (systemName.equalsIgnoreCase("npc_channel")) {
                    data.configHandler.resetNpcChannel();
                    embed = EmbedUtils.createSuccess("The NPC text channel was successfully reset!");
                }
                else embed = EmbedUtils.createError("Invalid system name. Could not reset!");

                event.getHook().editOriginalComponents(new ArrayList<>()).setEmbeds(embed).queue();
            }
            // If no button was pressed
            else if (pressedArgs[1].equals("no")) {
                event.deferEdit().queue();
                MessageEmbed embed = EmbedUtils.createError(systemName + " system was NOT reset!");
                event.getHook().editOriginalComponents(new ArrayList<>()).setEmbeds(embed).queue();
            }
        }
        // Confirm
        else if (pressedArgs[0].equals("confirm") && storedArgs[0].equals("confirm")) {
            String systemName = pressedArgs[4];

            // If yes button was pressed
            if (pressedArgs[1].equals("yes")) {
                event.deferEdit().queue();
                GuildData data = GuildData.get(event.getGuild());
                MessageEmbed embed = null;

                if (systemName.equalsIgnoreCase("npc")) {
                    data.characterHandler.confirmNPC();
                    embed = EmbedUtils.createSuccess("Added NPC to the database!");
                }
                else embed = EmbedUtils.createError("Incorrect internal system name (fix the bot).");

                event.getHook().editOriginalComponents(new ArrayList<>()).setEmbeds(embed).queue();
            }
            // If no button was pressed
            else if (pressedArgs[1].equals("no")) {
                event.deferEdit().queue();

                CharacterHandler characterHandler = GuildData.get(event.getGuild()).characterHandler;
                characterHandler.resetConfirmNPC();

                MessageEmbed embed = EmbedUtils.createError("Did not add NPC to the database.");
                event.getHook().editOriginalComponents(new ArrayList<>()).setEmbeds(embed).queue();
            }
        }
    }
}
