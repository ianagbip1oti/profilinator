package com.github.princesslana.profilinator;

import com.github.princesslana.somedb.TheDB;
import java.util.Optional;

public class RegistrationDao {

  public void register(String discordId, String githubUsername) {
    TheDB.execute(
        "insert into registration(discord_user_id, github_username) values (?, ?)",
        discordId,
        githubUsername);
  }

  public Optional<String> findGithubUsername(String discordId) {
    return TheDB.select(
            rs -> rs.getString("github_username"),
            "select github_username from registration where discord_user_id = ?",
            discordId)
        .findFirst();
  }

  public Optional<String> findDiscordId(String githubUsername) {
    return TheDB.select(
            rs -> rs.getString("discord_user_id"),
            "select discord_user_id from registration where github_username = ?",
            githubUsername)
        .findFirst();
  }
}
