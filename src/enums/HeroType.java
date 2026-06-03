package enums;

/**
 * The role category a hero belongs to in Honor of Kings.
 * Each constant carries a human-readable display name.
 */
public enum HeroType {
    TANK("Tank"),
    MAGE("Mage"),
    MARKSMAN("Marksman"),
    ASSASSIN("Assassin"),
    SUPPORT("Support"),
    WARRIOR("Warrior");

    private final String displayName;

    HeroType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}