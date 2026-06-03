package model;

import enums.Role;

/**
 * Abstract base class for all system users (Player and Admin).
 * Holds the identity and login credentials shared by every user type.
 */
public abstract class Person {

    private final String id;
    private String name;
    private String username;
    private String password;
    private Role role;

    protected Person(String id, String name, String username, String password, Role role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }

    /** Polymorphic description of the user, overridden by each subclass. */
    public abstract String getInfo();
}