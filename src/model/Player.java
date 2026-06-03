package model;

import enums.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * A game player. Owns a list of heroes and belongs to a team (by id).
 * Implements Searchable (found by id or name) and Persistable (saved to file).
 */
public class Player extends Person implements Searchable, Persistable {

    private int level;
    private double winRate;          // 0.0 - 1.0, stored as initialization data (plan 6.1)
    private String teamId;
    private List<Hero> ownedHeroes;

    public Player(String id, String name, String username, String password,
                  int level, double winRate, String teamId) {
        super(id, name, username, password, Role.PLAYER);
        this.level = level;
        this.winRate = winRate;
        this.teamId = teamId;
        this.ownedHeroes = new ArrayList<>();
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public List<Hero> getOwnedHeroes() { return ownedHeroes; }

    public void addHero(Hero hero) {
        if (hero != null && !ownedHeroes.contains(hero)) {
            ownedHeroes.add(hero);
        }
    }

    public void removeHero(Hero hero) {
        ownedHeroes.remove(hero);
    }

    @Override
    public String getInfo() {
        return "Player: " + getName() + " (level " + level
                + ", win rate " + String.format("%.0f%%", winRate * 100) + ")";
    }

    // --- Searchable ---
    @Override
    public boolean matchesQuery(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return getId().toLowerCase().equals(q) || getName().toLowerCase().contains(q);
    }

    // --- Persistable ---
    // Format: id|name|username|password|level|winRate|teamId|ownedHeroIds
    // ownedHeroIds: H001,H002,H003
    @Override
    public String toFileFormat() {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < ownedHeroes.size(); i++) {
            if (i > 0) ids.append(',');
            ids.append(ownedHeroes.get(i).getHeroId());
        }
        return String.join("|",
                getId(), getName(), getUsername(), getPassword(),
                String.valueOf(level), String.valueOf(winRate), teamId, ids.toString());
    }
    // NOTE: fromFileFormat(...) and hero re-linking are implemented in Step 7.

    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }
}