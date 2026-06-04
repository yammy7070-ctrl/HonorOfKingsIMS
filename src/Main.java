import exception.DuplicateIdException;
import enums.HeroType;
import enums.EquipmentType;
import java.util.LinkedHashMap;
import java.util.Map;
import exception.RecordNotFoundException;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;
import model.Team;
import service.AuthenticationService;
import service.GameDataManager;
import service.RankingService;
import service.SearchService;
import util.ConsoleUtil;
import util.InputHelper;

import java.util.List;

/**
 * Console entry point. Shows a login screen, then an Admin or Player menu
 * depending on the role of the logged-in user, and routes each choice to the
 * matching feature in the service layer.
 */
public class Main {

    private static GameDataManager data;
    private static AuthenticationService auth;
    private static SearchService search;
    private static RankingService ranking;

    public static void main(String[] args) {
        data = GameDataManager.getInstance();
        data.loadInitialData();
        auth = new AuthenticationService(data);
        search = new SearchService(data);
        ranking = new RankingService(data);

        ConsoleUtil.printTitle("Honor of Kings Information Management System");
        run();
        System.out.println("Goodbye!");
    }

    /** Main loop: shows login when logged out, otherwise the role-based menu. */
    private static void run() {
        while (true) {
            if (!auth.isLoggedIn()) {
                if (loginScreen() == 0) return;        // user chose to exit
            } else if (auth.isAdmin()) {
                if (adminMenu() == 0) auth.logout();   // 0 = log out
            } else {
                if (playerMenu() == 0) auth.logout();
            }
        }
    }

    // ---------------- login ----------------
    private static int loginScreen() {
        ConsoleUtil.printTitle("Login");
        System.out.println("1. Log in");
        System.out.println("0. Exit program");
        int choice = InputHelper.readInt("Choose: ");
        if (choice == 1) {
            String username = InputHelper.readString("Username: ");
            String password = InputHelper.readString("Password: ");
            if (auth.login(username, password)) {
                System.out.println("Login successful. Welcome, " + auth.getCurrentUser().getName() + "!");
            } else {
                System.out.println("Invalid credentials.");
            }
            return 1;
        }
        return 0;
    }

    // ---------------- admin menu ----------------
    private static int adminMenu() {
        ConsoleUtil.printTitle("Admin Menu - " + auth.getCurrentUser().getName());
        System.out.println("1. Player lookup");
        System.out.println("2. Team overview");
        System.out.println("3. Hero details");
        System.out.println("4. Equipment statistics");
        System.out.println("5. Match history");
        System.out.println("6. Leaderboard");
        System.out.println("7. Data management (add / delete / edit)");
        System.out.println("0. Log out");
        int choice = InputHelper.readInt("Choose: ");
        switch (choice) {
            case 1: playerLookup(); break;
            case 2: teamOverview(); break;
            case 3: heroDetails(); break;
            case 4: equipmentStatistics(); break;
            case 5: matchHistory(); break;
            case 6: leaderboard(); break;
            case 7: dataManagement(); break;
            case 0: return 0;
            default: System.out.println("Invalid choice.");
        }
        return choice;
    }

    // ---------------- player menu ----------------
    private static int playerMenu() {
        Player me = (Player) auth.getCurrentUser();   // safe: only reached when role is PLAYER
        ConsoleUtil.printTitle("Player Menu - " + me.getName());
        System.out.println("1. View my profile");
        System.out.println("2. Player lookup");
        System.out.println("3. Team overview");
        System.out.println("4. Hero details");
        System.out.println("5. Equipment statistics");
        System.out.println("6. My match history");
        System.out.println("7. Leaderboard");
        System.out.println("0. Log out");
        int choice = InputHelper.readInt("Choose: ");
        switch (choice) {
            case 1: viewMyProfile(me); break;
            case 2: playerLookup(); break;
            case 3: teamOverview(); break;
            case 4: heroDetails(); break;
            case 5: equipmentStatistics(); break;
            case 6: myMatchHistory(me); break;
            case 7: leaderboard(); break;
            case 0: return 0;
            default: System.out.println("Invalid choice.");
        }
        return choice;
    }

    // ---------------- features ----------------
    private static void playerLookup() {
        String q = InputHelper.readString("Enter player id or name: ");
        try {
            Player p = search.searchPlayer(q);
            ConsoleUtil.printTitle("Player: " + p.getName());
            System.out.println("ID: " + p.getId());
            System.out.println("Team: " + teamName(p.getTeamId()));
            System.out.println("Level: " + p.getLevel());
            System.out.println("Win rate: " + ConsoleUtil.formatPercent(p.getWinRate()));
            System.out.println("Owned heroes:");
            for (Hero h : p.getOwnedHeroes()) {
                System.out.println("  - " + h.getName() + " [" + h.getHeroType().getDisplayName() + "]");
                System.out.println("      Equipped: " + h.getEquippedItems());
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void teamOverview() {
        String q = InputHelper.readString("Enter team id or name: ");
        try {
            Team t = search.searchTeam(q);
            ConsoleUtil.printTitle("Team: " + t.getName());
            System.out.println("ID: " + t.getTeamId());
            System.out.println("Members:");
            for (Player m : t.getMembers()) {
                System.out.println("  - " + m.getName() + " (level " + m.getLevel()
                        + ", " + ConsoleUtil.formatPercent(m.getWinRate()) + ")");
            }
            System.out.printf("Average level: %.1f%n", t.getAverageLevel());
            System.out.println("Total matches: " + t.getTotalMatches());
            System.out.println("Win rate: " + ConsoleUtil.formatPercent(t.getWinRate()));
            Player top = t.getTopPlayer();
            System.out.println("Top player: " + (top == null ? "none" : top.getName()));
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void heroDetails() {
        String q = InputHelper.readString("Enter hero name: ");
        try {
            Hero h = search.searchHero(q);
            ConsoleUtil.printTitle("Hero: " + h.getName());
            System.out.println("Type: " + h.getHeroType().getDisplayName());
            System.out.println("Base stats: " + h.getBaseStats());
            System.out.println("Total stats (with equipment): " + h.getStats());
            System.out.println("Equipped items: " + h.getEquippedItems());
            System.out.println("Owned by:");
            List<Player> owners = search.getOwnersOfHero(h);
            if (owners.isEmpty()) {
                System.out.println("  (no players own this hero)");
            } else {
                for (Player o : owners) System.out.println("  - " + o.getName());
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void equipmentStatistics() {
        ConsoleUtil.printTitle("Equipment Ranking");
        System.out.println("(Score = usage/maxUsage * 0.4 + rating/5 * 0.6)");
        List<Equipment> ranked = ranking.rankEquipment();
        int maxUsage = 0;
        for (Equipment e : ranked) if (e.getUsageCount() > maxUsage) maxUsage = e.getUsageCount();
        int rank = 1;
        for (Equipment e : ranked) {
            System.out.printf("%2d. %-22s usage=%-4d rating=%.1f  score=%.3f%n",
                    rank++, e.getName(), e.getUsageCount(), e.getAverageRating(),
                    ranking.equipmentScore(e, maxUsage));
        }
    }

    private static void matchHistory() {
        System.out.println("1. By player    2. By team");
        int c = InputHelper.readInt("Choose: ");
        int n = InputHelper.readInt("How many recent matches? ");
        try {
            if (c == 1) {
                Player p = search.searchPlayer(InputHelper.readString("Player id or name: "));
                printMatches(search.getRecentMatchesForPlayer(p, n), p);
            } else if (c == 2) {
                Team t = search.searchTeam(InputHelper.readString("Team id or name: "));
                printMatches(search.getRecentMatchesForTeam(t, n), null);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void myMatchHistory(Player me) {
        int n = InputHelper.readInt("How many recent matches? ");
        ConsoleUtil.printTitle("My recent matches");
        printMatches(search.getRecentMatchesForPlayer(me, n), me);
    }

    private static void printMatches(List<MatchRecord> matches, Player forPlayer) {
        if (matches.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        for (MatchRecord m : matches) {
            System.out.println(m.generateReport());
            if (forPlayer != null) {
                Hero picked = m.getPlayerPerformance(forPlayer);
                System.out.println("    " + forPlayer.getName() + " picked: "
                        + (picked == null ? "did not play" : picked.getName()));
            }
        }
    }

    private static void leaderboard() {
        ConsoleUtil.printTitle("Leaderboard");
        System.out.println("Rank by: 1. Win rate   2. Level   3. Match count   4. Custom score");
        int c = InputHelper.readInt("Choose: ");
        int x = InputHelper.readInt("Top how many? ");
        List<Player> board;
        switch (c) {
            case 1: board = ranking.leaderboardByWinRate(x); break;
            case 2: board = ranking.leaderboardByLevel(x); break;
            case 3: board = ranking.leaderboardByMatchCount(x); break;
            case 4: board = ranking.leaderboardByCustomScore(x); break;
            default: System.out.println("Invalid choice."); return;
        }
        System.out.println("(Ties are broken alphabetically by username.)");
        int rank = 1;
        for (Player p : board) {
            System.out.printf("%2d. %-12s level=%-3d winRate=%s matches=%d%n",
                    rank++, p.getName(), p.getLevel(),
                    ConsoleUtil.formatPercent(p.getWinRate()), ranking.countMatches(p));
        }
    }

    private static void viewMyProfile(Player me) {
        ConsoleUtil.printTitle("My Profile");
        System.out.println(me.getInfo());
        System.out.println("Team: " + teamName(me.getTeamId()));
        System.out.println("Owned heroes:");
        for (Hero h : me.getOwnedHeroes()) {
            System.out.println("  - " + h.getName() + " " + h.getEquippedItems());
        }
    }

    // ---------------- helper ----------------
    private static String teamName(String teamId) {
        Team t = data.getTeam(teamId);
        return (t == null) ? teamId : t.getName();
    }
    // ---------------- data management (admin only) ----------------
    private static void dataManagement() {
        while (true) {
            ConsoleUtil.printTitle("Data Management (Admin)");
            System.out.println("1. Add player       2. Delete player    3. Edit player");
            System.out.println("4. Add hero         5. Delete hero");
            System.out.println("6. Add equipment    7. Delete equipment");
            System.out.println("8. Add team         9. Delete team");
            System.out.println("10. Delete match");
            System.out.println("0. Back");
            int c = InputHelper.readInt("Choose: ");
            switch (c) {
                case 1: addPlayer(); break;
                case 2: deletePlayer(); break;
                case 3: editPlayer(); break;
                case 4: addHero(); break;
                case 5: deleteHero(); break;
                case 6: addEquipment(); break;
                case 7: deleteEquipment(); break;
                case 8: addTeam(); break;
                case 9: deleteTeam(); break;
                case 10: deleteMatch(); break;
                case 0: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void addPlayer() {
        String id = InputHelper.readString("New player id: ");
        String name = InputHelper.readString("Name: ");
        String username = InputHelper.readString("Username: ");
        String password = InputHelper.readString("Password: ");
        int level = InputHelper.readInt("Level: ");
        double winRate = InputHelper.readDouble("Win rate (0.0 - 1.0): ");
        String teamId = InputHelper.readString("Team id: ");
        try {
            Player p = new Player(id, name, username, password, level, winRate, teamId);
            data.addPlayer(p);                       // throws DuplicateIdException if id taken
            Team t = data.getTeam(teamId);
            if (t != null) t.addPlayer(p);           // keep the team's member list consistent
            System.out.println("Player added.");
        } catch (DuplicateIdException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deletePlayer() {
        String id = InputHelper.readString("Player id to delete: ");
        try {
            data.deletePlayer(id);                   // throws RecordNotFoundException if missing
            System.out.println("Player deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void editPlayer() {
        String id = InputHelper.readString("Player id to edit: ");
        Player p = data.getPlayer(id);
        if (p == null) {
            System.out.println("Player not found: " + id);
            return;
        }
        System.out.println("Editing " + p.getName() + ":");
        p.setLevel(InputHelper.readInt("New level: "));
        p.setWinRate(InputHelper.readDouble("New win rate (0.0 - 1.0): "));
        System.out.println("Updated: " + p.getInfo());
    }

    private static void addHero() {
        String id = InputHelper.readString("New hero id: ");
        String name = InputHelper.readString("Name: ");
        System.out.println("Types: TANK, MAGE, MARKSMAN, ASSASSIN, SUPPORT, WARRIOR");
        String typeStr = InputHelper.readString("Type: ");
        try {
            HeroType type = HeroType.valueOf(typeStr.trim().toUpperCase());   // bad type -> IllegalArgumentException
            Map<String, Integer> base = new LinkedHashMap<>();
            base.put("hp", InputHelper.readInt("Base hp: "));
            base.put("attack", InputHelper.readInt("Base attack: "));
            base.put("defense", InputHelper.readInt("Base defense: "));
            data.addHero(new Hero(id, name, type, base));
            System.out.println("Hero added.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid hero type.");
        } catch (DuplicateIdException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteHero() {
        String id = InputHelper.readString("Hero id to delete: ");
        try {
            data.deleteHero(id);
            System.out.println("Hero deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addEquipment() {
        String id = InputHelper.readString("New equipment id: ");
        String name = InputHelper.readString("Name: ");
        System.out.println("Types: WEAPON, ARMOR, ACCESSORY, BOOTS");
        String typeStr = InputHelper.readString("Type: ");
        try {
            EquipmentType type = EquipmentType.valueOf(typeStr.trim().toUpperCase());
            Map<String, Integer> bonus = new LinkedHashMap<>();
            bonus.put("attack", InputHelper.readInt("Attack bonus (0 if none): "));
            int usage = InputHelper.readInt("Usage count: ");
            double rating = InputHelper.readDouble("Average rating (0.0 - 5.0): ");
            data.addEquipment(new Equipment(id, name, type, bonus, usage, rating));
            System.out.println("Equipment added.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid equipment type.");
        } catch (DuplicateIdException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteEquipment() {
        String id = InputHelper.readString("Equipment id to delete: ");
        try {
            data.deleteEquipment(id);
            System.out.println("Equipment deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addTeam() {
        String id = InputHelper.readString("New team id: ");
        String name = InputHelper.readString("Name: ");
        int total = InputHelper.readInt("Total matches: ");
        int wins = InputHelper.readInt("Wins: ");
        try {
            data.addTeam(new Team(id, name, total, wins));
            System.out.println("Team added.");
        } catch (DuplicateIdException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteTeam() {
        String id = InputHelper.readString("Team id to delete: ");
        try {
            data.deleteTeam(id);
            System.out.println("Team deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteMatch() {
        String id = InputHelper.readString("Match id to delete: ");
        try {
            data.deleteMatch(id);
            System.out.println("Match deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}