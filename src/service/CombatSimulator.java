package service;

import model.Hero;

import java.util.Random;

/**
 * Extra-credit feature: simulates a simple 1v1 battle between two heroes using
 * their combined stats (base + equipment). Each round one hero attacks the other;
 * damage has some randomness, a chance to critically hit, and a chance to dodge.
 * The fight ends when one hero's HP reaches 0, or after a maximum number of rounds.
 */
public class CombatSimulator {

    private static final int MAX_ROUNDS = 20;
    private static final double CRIT_CHANCE = 0.10;   // 10% chance for double damage
    private static final double DODGE_CHANCE = 0.05;  // 5% chance to take no damage
    private static final double DEFENSE_FACTOR = 0.5; // half of defense reduces damage

    private final Random random = new Random();

    /** Runs the battle, prints a round-by-round log, and returns the winner (null = draw). */
    public Hero simulate(Hero a, Hero b) {
        int hpA = statOf(a, "hp");
        int hpB = statOf(b, "hp");

        System.out.println(a.getName() + " (HP " + hpA + ")  VS  " + b.getName() + " (HP " + hpB + ")");

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            hpB -= attack(a, b, round);
            if (hpB <= 0) { announce(a); return a; }
            hpA -= attack(b, a, round);
            if (hpA <= 0) { announce(b); return b; }
        }

        System.out.println("Max rounds reached. " + a.getName() + " HP " + Math.max(hpA, 0)
                + ", " + b.getName() + " HP " + Math.max(hpB, 0));
        if (hpA > hpB) { announce(a); return a; }
        if (hpB > hpA) { announce(b); return b; }
        System.out.println("It's a draw!");
        return null;
    }

    /** One attack from attacker to defender; prints the line and returns damage dealt. */
    private int attack(Hero attacker, Hero defender, int round) {
        if (random.nextDouble() < DODGE_CHANCE) {
            System.out.println("Round " + round + ": " + defender.getName() + " dodged "
                    + attacker.getName() + "'s attack!");
            return 0;
        }
        int atk = statOf(attacker, "attack");
        int def = statOf(defender, "defense");
        double variance = 1.0 + random.nextDouble() * 0.3;            // damage varies 1.0x - 1.3x
        int damage = (int) Math.max(1, atk * variance - def * DEFENSE_FACTOR);
        boolean crit = random.nextDouble() < CRIT_CHANCE;
        if (crit) damage *= 2;
        System.out.println("Round " + round + ": " + attacker.getName() + " hits "
                + defender.getName() + " for " + damage + (crit ? " (CRITICAL!)" : ""));
        return damage;
    }

    private int statOf(Hero h, String key) {
        return h.getStats().getOrDefault(key, 0);
    }

    private void announce(Hero winner) {
        System.out.println(">>> " + winner.getName() + " wins the battle!");
    }
}