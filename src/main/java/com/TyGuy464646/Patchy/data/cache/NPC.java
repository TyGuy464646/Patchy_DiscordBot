package com.TyGuy464646.Patchy.data.cache;

/**
 * POJO object that represents an NPC.
 *
 * @author TyGuy464646
 */
public class NPC {

    // ID of guild it belongs to
    private long guild;

    // first name
    private String firstName;

    // last name
    private String lastName;

    // description
    private String description;

    // gender
    private String gender;

    // age
    private int age;

    // alignment
    private String alignment;

    // faction
    private String faction;

    // attractiveness
    private int attractiveness;

    // mug shot
    private String mugShot;

    /**
     * For POJO
     */
    public NPC() {
    }

    /**
     * Constructor
     *
     * @param guild       Guild the NPC belongs to
     * @param firstName   First name of the NPC
     * @param lastName    Last name of the NPC
     * @param description Description of the NPC
     * @param faction     Faction of the NPC
     * @param mugShot     Mug shot of the NPC
     */
    public NPC(long guild, String firstName, String lastName, String description, String gender, int age, String alignment, String faction, int attractiveness, String mugShot) {
        this.guild = guild;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.gender = gender;
        this.age = age;
        this.alignment = alignment;
        this.faction = faction;
        this.attractiveness = attractiveness;
        this.mugShot = mugShot;
    }

    // Getters and Setters
    public long getGuild() {
        return guild;
    }

    public void setGuild(long guild) {
        this.guild = guild;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public int getAttractiveness() {
        return attractiveness;
    }

    public void setAttractiveness(int attractiveness) {
        this.attractiveness = attractiveness;
    }

    public String getMugShot() {
        return mugShot;
    }

    public void setMugShot(String mugShot) {
        this.mugShot = mugShot;
    }
}
