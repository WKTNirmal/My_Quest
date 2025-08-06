package com.wktnirmal.myquest.Leaderboard;

public class UserEntry {
    private String username;
    private int xp;
    private int placement;

    public UserEntry(String username, int xp, int placement) {
        this.username = username;
        this.xp = xp;
        this.placement = placement;
    }

    public int getXp() {
        return xp;
    }

    public int getPlacement() {
        return placement;
    }

    public String getUsername() {
        return username;
    }
}
