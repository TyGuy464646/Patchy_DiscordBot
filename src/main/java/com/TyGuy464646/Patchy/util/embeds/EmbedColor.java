package com.TyGuy464646.Patchy.util.embeds;

public enum EmbedColor {

    DEFAULT(0xfd8989),
    ERROR(0xdd5f53),
    SUCCESS(0x77b255),
    WARNING(0xff8c03);

    public final int color;

    EmbedColor(int hexCode) { this.color = hexCode; }
}
