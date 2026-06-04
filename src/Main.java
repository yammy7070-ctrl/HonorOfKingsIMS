import model.Hero;
import model.Player;
import util.ConsoleUtil;
import util.DataInitializer;

public class Main {
    public static void main(String[] args) {
        DataInitializer.initialize();

        ConsoleUtil.printTitle("Honor of Kings IMS - Data Check");
        System.out.println("Teams:     " + DataInitializer.getTeams().size());
        System.out.println("Players:   " + DataInitializer.getPlayers().size());
        System.out.println("Heroes:    " + DataInitializer.getHeroes().size());
        System.out.println("Equipment: " + DataInitializer.getEquipment().size());
        System.out.println("Matches:   " + DataInitializer.getMatches().size());
        System.out.println("Admins:    " + DataInitializer.getAdmins().size());

        ConsoleUtil.printDivider();
        Player p = DataInitializer.getPlayers().get(0);
        System.out.println(p.getInfo());
        System.out.println("Win rate: " + ConsoleUtil.formatPercent(p.getWinRate()));
        System.out.println("Owned heroes: " + p.getOwnedHeroes());

        Hero h = DataInitializer.getHeroes().get(0);
        System.out.println(h + " stats: " + h.getStats());
        System.out.println("Equipped: " + h.getEquippedItems());
        System.out.println(DataInitializer.getMatches().get(0).getTeam1Picks());
    }
}