package com.TyGuy464646.Patchy.listeners;

import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.data.cache.NPC;
import com.TyGuy464646.Patchy.handlers.CharacterHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Listener for selection menus and handles all selection menu backend.
 *
 * @author TyGuy464646
 */
public class SelectionMenuListener extends ListenerAdapter {

    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectionMenuListener.class);

    // Minutes until menus are disabled
    public static final int MINUTES_TO_DISABLE = 3;
    public static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);

    public static final Map<String, List<StringSelectMenu>> selectMenus = new HashMap<>();

    /**
     * Adds selection menus to the NPC List command.
     * @param uuid ID of the user
     * @param action Action to send the message
     * @param pages Pages of the message
     */
    public static void sendNPCListMenu(String uuid, WebhookMessageCreateAction<Message> action, List<List<MessageEmbed>> pages) {
        List<StringSelectMenu> components = getNPCListSelectMenu(uuid, pages);

        selectMenus.put(uuid, components);

        action.addActionRow(components.get(0)).queue(
                interactionHook -> SelectionMenuListener.disableMenus(uuid, interactionHook)
        );
    }

    /**
     * Gets the selection menus for the NPC List command.
     * @param uuid ID of the user
     * @param pages Pages of the message
     * @return List of {@link StringSelectMenu}
     */
    private static List<StringSelectMenu> getNPCListSelectMenu(String uuid, List<List<MessageEmbed>> pages) {
        List<StringSelectMenu> menus = new ArrayList<>();

        for (int i = 0; i < pages.size(); i++) {
            StringSelectMenu.Builder menu = StringSelectMenu.create("npc-list:" + uuid + ":page:" + i)
                    .setPlaceholder("Select an NPC to view")
                    .setMinValues(1)
                    .setMaxValues(1)
                    .setRequiredRange(1, 1);

            for (int j = 0; j < pages.get(i).size(); j++) {
                String[] currentName = pages.get(i).get(j).getAuthor().getName().split(" ");
                String firstName = currentName[0];
                String lastName = currentName[1];

                menu.addOption(firstName + " " + lastName, firstName + ":" + lastName);
            }
            menus.add(menu.build());
        }
        return menus;
    }

    /**
     * Disables menus after a certain amount of time.
     * @param uuid ID of the user
     * @param hook Message hook
     */
    public static void disableMenus(String uuid, Message hook) {
        Runnable task = () -> {
            hook.editMessageComponents(new ArrayList<>())
                    .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

            selectMenus.remove(uuid);
            ButtonListener.buttons.remove(uuid);
            ButtonListener.menus.remove(uuid);
            ButtonListener.embedMenus.remove(uuid);
        };

        executor.schedule(task, MINUTES_TO_DISABLE, TimeUnit.MINUTES);
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Check that the menu is a NPC List menu
        String[] pressedArgs = event.getComponentId().split(":");

        // Check if user owns the menu
        long userID = Long.parseLong(pressedArgs[1]);
        if (userID != event.getUser().getIdLong()) return;

        // Get other menus
        String uuid = userID + ":" + pressedArgs[2];
        List<StringSelectMenu> menus = selectMenus.get(uuid);
        if (menus == null) return;
        String[] storedArgs = menus.get(0).getId().split(":");

        // NPC List
        if (pressedArgs[0].equals("npc-list") && storedArgs[0].equals("npc-list")) {
            CharacterHandler characterHandler = GuildData.get(event.getGuild()).characterHandler;

            // Get the NPC
            String[] npcName = event.getValues().get(0).split(":");
            String firstName = npcName[0];
            String lastName = npcName[1];

            // Get the NPC
            NPC npc = characterHandler.findNPCByFirstLastName(firstName, lastName);
            if (npc == null) return;

            // Get the NPC embed
            MessageEmbed embed = characterHandler.buildListEmbed(npc);

            event.editComponents(new ArrayList<>()).setEmbeds(embed).queue();
        }
    }

    // Getters and Setters
    public static Map<String, List<StringSelectMenu>> getSelectMenus() {
        return selectMenus;
    }
}
