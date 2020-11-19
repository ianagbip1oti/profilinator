package com.github.princesslana.profilinator;

import com.github.princesslana.somedb.TheDB;

public class RegistrationDao {

  public void register(String discordId, String githubUsername) {
    TheDB.execute(
        "insert into registration(discord_user_id, github_username) values (?, ?)",
        discordId,
        githubUsername);
  }
}
