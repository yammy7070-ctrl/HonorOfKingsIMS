package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A team aggregating multiple Player objects. Players exist independently and
 * can change teams, so this is aggregation rather than composition.
 */
public class Team implements Searchable, Persistable {

    private final String teamId;
    private String name;
    private List<Player> members;
    private int totalMatches;
    private int wins;

    public Team(String teamId, String name, int totalMatches, int wins) {
        this.teamId = teamId;
        this.name = name;
        this.members = new ArrayList<>();
        this.totalMatches = totalMatches;
        this.wins = wins;
    }

    public String getTeamId() { return teamId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Player> getMembers() { return members; }
    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public void addPlayer(Player player) {
        if (player != null && !members.contains(player)) {
            members.add(player);
        }
    }

    public void removePlayer(Player player) {
        members.remove(player);
    }

    /** Win rate computed on demand from stored wins / totalMatches (plan 6.1). */
    public double getWinRate() {
        if (totalMatches == 0) return 0.0;
        return (double) wins / totalMatches;
    }

    /** Average level of all current members; 0 if the team is empty. */
    public double getAverageLevel() {
        if (members.isEmpty()) return 0.0;
        int sum = 0;
        for (Player p : members) {
            sum += p.getLevel();
        }
        return (double) sum / members.size();
    }

    /** The member with the highest win rate; null if the team has no members. */
    public Player getTopPlayer() {
        Player top = null;
        for (Player p : members) {
            if (top == null || p.getWinRate() > top.getWinRate()) {
                top = p;
            }
        }
        return top;
    }

    // --- Searchable ---
    @Override
    public boolean matchesQuery(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return teamId.toLowerCase().equals(q) || name.toLowerCase().contains(q);
    }

    // --- Persistable ---
    // Format: teamId|name|totalMatches|wins|memberIds   (memberIds: P001,P002,...)
    @Override
    public String toFileFormat() {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            if (i > 0) ids.append(',');
            ids.append(members.get(i).getId());
        }
        return String.join("|",
                teamId, name, String.valueOf(totalMatches), String.valueOf(wins), ids.toString());
    }
    // NOTE: fromFileFormat(...) and member re-linking are implemented in Step 7.

    @Override
    public String toString() {
        return name + " (" + teamId + ")";
    }
}
