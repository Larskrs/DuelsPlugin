package net.larskrs.plugins.duels.instances;

public class ArenaOptions {
    public Arena arena;
    public String type;
    public int winAmount;
    public int maxPlayers;
    public int timeLimit;
    public boolean preMatchMeeting;
    
    public ArenaOptions(Arena arena, String type) {
        this.arena = arena;
        this.type = type;
    }        
}
