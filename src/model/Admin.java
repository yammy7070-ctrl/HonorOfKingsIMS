package model;

import enums.Role;

/**
 * A user with full data-management permission. The actual create/delete/edit
 * operations live in GameDataManager (Step 4); the menu calls them only when
 * the logged-in user is an Admin, i.e. canEditAll() returns true.
 */
public class Admin extends Person {

    private int adminLevel;

    public Admin(String id, String name, String username, String password, int adminLevel) {
        super(id, name, username, password, Role.ADMIN);
        this.adminLevel = adminLevel;
    }

    public int getAdminLevel() { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }

    /** Admins may modify all data. Used by the menu to guard admin-only actions. */
    public boolean canEditAll() {
        return true;
    }

    @Override
    public String getInfo() {
        return "Admin: " + getName() + " (username: " + getUsername() + ", level " + adminLevel + ")";
    }
}
