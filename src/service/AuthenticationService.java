package service;

import enums.Role;
import model.Admin;
import model.Person;
import model.Player;

/**
 * Handles login and logout, and remembers who is currently logged in.
 * The current user is stored as a Person, which can be an Admin or a Player
 * (polymorphism); the role decides what the user is allowed to do.
 */
public class AuthenticationService {

    private final GameDataManager data;
    private Person currentUser;

    public AuthenticationService(GameDataManager data) {
        this.data = data;
    }

    /** Tries to log in with the given username and password. Returns true on success. */
    public boolean login(String username, String password) {
        for (Admin a : data.getAllAdmins()) {
            if (a.getUsername().equals(username) && a.getPassword().equals(password)) {
                currentUser = a;
                return true;
            }
        }
        for (Player p : data.getAllPlayers()) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                currentUser = p;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public Person getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public boolean isPlayer() {
        return currentUser != null && currentUser.getRole() == Role.PLAYER;
    }
}