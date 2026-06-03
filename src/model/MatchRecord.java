package model;

import enums.MatchResult;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A single match between two teams. Stores the date, both teams, the hero each
 * player picked, and the result (from team1's perspective).
 * Implements Reportable (printable summary) and Persistable (saved to file).
 */
public class MatchRecord implements Reportable, Persistable {

    private final String matchId;
    private LocalDate date;
    private Team team1;
    private Team team2;
    private Map<Player, Hero> team1Picks;   // which hero each team1 player used
    private Map<Player, Hero> team2Picks;
    private MatchResult result;             // result for team1: WIN / LOSS / DRAW

    public MatchRecord(String matchId, LocalDate date, Team team1, Team team2, MatchResult result) {
        this.matchId = matchId;
        this.date = date;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Picks = new LinkedHashMap<>();
        this.team2Picks = new LinkedHashMap<>();
        this.result = result;
    }

    public String getMatchId() { return matchId; }
    public LocalDate getDate() { return date; }
    public Team getTeam1() { return team1; }
    public Team getTeam2() { return team2; }
    public Map<Player, Hero> getTeam1Picks() { return team1Picks; }
    public Map<Player, Hero> getTeam2Picks() { return team2Picks; }
    public MatchResult getResult() { return result; }
    public void setResult(MatchResult result) { this.result = result; }

    /** Records that a player used a given hero in this match. */
    public void addPick(Team team, Player player, Hero hero) {
        if (team == team1) {
            team1Picks.put(player, hero);
        } else if (team == team2) {
            team2Picks.put(player, hero);
        }
    }

    /** Returns the winning team, or null for a draw. Result is from team1's view. */
    public Team getWinner() {
        if (result == MatchResult.WIN) return team1;
        if (result == MatchResult.LOSS) return team2;
        return null; // DRAW
    }

    /** Returns the hero the player used in this match, or null if they did not play. */
    public Hero getPlayerPerformance(Player player) {
        if (team1Picks.containsKey(player)) return team1Picks.get(player);
        if (team2Picks.containsKey(player)) return team2Picks.get(player);
        return null;
    }

    /** True if the player took part in this match (used by match-history search). */
    public boolean involvesPlayer(Player player) {
        return team1Picks.containsKey(player) || team2Picks.containsKey(player);
    }

    // --- Reportable ---
    @Override
    public String generateReport() {
        Team winner = getWinner();
        String outcome = (winner == null) ? "Draw" : winner.getName() + " won";
        return "Match " + matchId + " (" + date + "): "
                + team1.getName() + " vs " + team2.getName() + " -> " + outcome;
    }

    // --- Persistable ---
    // Format: matchId|date|team1Id|team2Id|result|team1Picks|team2Picks
    // picks: playerId:heroId pairs joined by ';'  e.g. P001:H001;P002:H002
    @Override
    public String toFileFormat() {
        return String.join("|",
                matchId, date.toString(), team1.getTeamId(), team2.getTeamId(),
                result.name(), encodePicks(team1Picks), encodePicks(team2Picks));
    }

    private String encodePicks(Map<Player, Hero> picks) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<Player, Hero> e : picks.entrySet()) {
            if (i++ > 0) sb.append(';');
            sb.append(e.getKey().getId()).append(':').append(e.getValue().getHeroId());
        }
        return sb.toString();
    }
    // NOTE: fromFileFormat(...) and team/player/hero re-linking are implemented in Step 7.

    @Override
    public String toString() {
        return generateReport();
    }
}