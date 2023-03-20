package com.TyGuy464646.Patchy.util.embeds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {


    /**
     * Sets a default object mapper with settings
     *
     * @return {@link ObjectMapper} defaultObjectMapper
     */
    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper defaultObjectMapper = new ObjectMapper();

        // --- OPTIONS
        defaultObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return defaultObjectMapper;
    }

    /**
     * Converts a {@link JsonNode} to {@link MessageEmbed}. <p>
     * Supported Fields: Title, Description, Color, Fields, Author, Footer, Timestamp, Image, Thumbnail.
     * Built for use of <a href="https://discohook.org/?data=eyJtZXNzYWdlcyI6W3siZGF0YSI6eyJjb250ZW50IjpudWxsLCJlbWJlZHMiOm51bGwsImF0dGFjaG1lbnRzIjpbXX19XX0">Discohook</a> embed builder.
     *
     * @param event   The command event at which this function was called
     * @param json    The input .json String
     * @param channel The {@link Channel} the embed will be sent to
     */
    public static void toEmbed(SlashCommandInteractionEvent event, String json, Channel channel) throws JsonProcessingException {
        JsonNode node = getDefaultObjectMapper().readTree(json);

        // Embeds
        JsonNode embedArray = node.get("embeds");
        if (embedArray != null) { // Make sure the object is not null before adding to embed
            List<MessageEmbed> embeds = new ArrayList<>();

            embedArray.forEach(embedEle -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                // Title
                JsonNode titleNode = embedEle.get("title");
                if (titleNode != null) {
                    embedBuilder.setTitle(titleNode.asText());
                }

                // Description
                JsonNode descriptionNode = embedEle.get("description");
                if (descriptionNode != null) {
                    embedBuilder.setDescription(descriptionNode.asText());
                }

                // Color
                JsonNode colorNode = embedEle.get("color");
                if (colorNode != null) {
                    embedBuilder.setColor(colorNode.asInt());
                }

                // Fields
                JsonNode fieldArray = embedEle.get("fields");
                if (fieldArray != null) {
                    fieldArray.forEach(fieldEle -> {
                        String name = fieldEle.get("name").asText();
                        String content = fieldEle.get("value").asText();
                        JsonNode inLine = fieldEle.get("inline");
                        embedBuilder.addField(name, content, inLine != null);
                    });
                }

                // Author
                JsonNode authorNode = embedEle.get("author");
                if (authorNode != null) {
                    String name = authorNode.get("name").asText();
                    String url = authorNode.get("url").asText();
                    String iconUrl = authorNode.get("icon_url").asText();
                    if (url != null && iconUrl != null)
                        embedBuilder.setAuthor(name, url, iconUrl);
                    else if (url != null)
                        embedBuilder.setAuthor(name, url);
                    else
                        embedBuilder.setAuthor(name);
                }

                // Footer
                JsonNode footerNode = embedEle.get("footer");
                if (footerNode != null) {
                    String content = footerNode.get("text").asText();
                    String url = footerNode.get("icon_url").asText();
                    if (url != null)
                        embedBuilder.setFooter(content, url);
                    else
                        embedBuilder.setFooter(content);
                }

                // Timestamp
                JsonNode timestampNode = embedEle.get("timestamp");
                if (timestampNode != null) {
                    embedBuilder.setTimestamp(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestampNode.asText()));
                }

                // Image
                JsonNode imageNode = embedEle.get("image");
                if (imageNode != null) {
                    embedBuilder.setImage(imageNode.get("url").asText());
                }

                // Thumbnail
                JsonNode thumbnailNode = embedEle.get("thumbnail");
                if (thumbnailNode != null) {
                    embedBuilder.setThumbnail(thumbnailNode.get("url").asText());
                }

                embeds.add(embedBuilder.build());
            });

            event.getGuild().getTextChannelById(channel.getIdLong()).sendMessageEmbeds(embeds).queue();
        }
    }
}
