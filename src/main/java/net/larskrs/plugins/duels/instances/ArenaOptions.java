package net.larskrs.plugins.duels.instances;

public class ArenaOptions {
    public Arena arena;
    public String type;
    public int winAmount;
    public boolean preMatchMeeting;
    
    public ArenaOptions(Arena arena, String type, int winAmount, boolean preMatchMeeting) {
        this.arena = arena;
        this.type = type;
        this.winAmount = winAmount;
        this.preMatchMeeting = preMatchMeeting;
    }        
}
