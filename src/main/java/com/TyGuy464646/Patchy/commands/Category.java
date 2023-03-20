package com.TyGuy464646.Patchy.commands;

public enum Category {

    STAFF(":computer:", "Staff"),
    MUSIC(":musical_note:", "Music"),
    NPC(":busts_in_silhouette:", "NPC"),
    UTILITY(":tools:", "Utility");
//    QUEST("", "Quest"),
//    FINANCE("", "Finance"),
//    SHIP("", "Ship"),
//    LOCATION("", "Location")

    public final String emoji;
    public final String name;

    Category(String emoji, String name) {
        this.emoji = emoji;
        this.name = name;
    }
}
