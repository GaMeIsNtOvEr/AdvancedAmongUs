 package com.nktfh100.amongus.enums;
 
 public enum StatInt {
   GAMES_PLAYED("games_played"), IMPOSTER_WINS("imposter_wins"), CREWMATE_WINS("crewmate_wins"), TOTAL_WINS("total_wins"), IMPOSTER_KILLS("imposter_kills"), TASKS_COMPLETED("tasks_completed"), EMERGENCIES_CALLED("emergencies_called"),
   BODIES_REPORTED("bodies_reported"), TIMES_MURDERED("times_murdered"), TIMES_EJECTED("times_ejected"), TIME_PLAYED("time_played");
   
   private String name;
 
   
   StatInt(String name) { this.name = name; }
 
 
   
   public String getName() { return this.name; }
 }


