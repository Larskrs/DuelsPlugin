package net.larskrs.plugins.duels.instances;

public class ArenaOptions {
    Public Arena arena;
    public GameType type;
    public int winAmount;
    public boolean preMatchMeeting;
    
    public ArenaOptions(Arena arena, GameType type, int winAmount, boolean preMatchMeeting) {
        this.arena = arena;
        this.type = type;
        this.winAmount = winAmount;
        this.preMatchMeeting = preMatchMeeting;
    }        
}
