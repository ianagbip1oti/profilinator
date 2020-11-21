package com.github.princesslana.profilinator.commands;

import com.github.princesslana.profilinator.GithubDao;
import com.github.princesslana.profilinator.Profile;
import com.github.princesslana.profilinator.RegistrationDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileCommand {

  private static final Logger LOG = LoggerFactory.getLogger(ProfileCommand.class);

  private final DiscordRequest request;
  private final RegistrationDao registrations;
  private final GithubDao github;

  public ProfileCommand(DiscordRequest request, RegistrationDao registrations, GithubDao github) {
    this.request = request;
    this.registrations = registrations;
    this.github = github;
  }

  @CommandHandler(commandName = "profile")
  public DiscordResponse profile() {
    return Try.run(
        () -> {
          var discordUserId = request.getDispatcher().identityFromEvent(request.getEvent());
          var githubUsername =
              registrations
                  .findGithubUsername(discordUserId)
                  .orElseThrow(() -> new IllegalArgumentException("No github username registered"));

          var profile =
              github
                  .getProfile(githubUsername)
                  .orElseThrow(
                      () ->
                          new IllegalStateException("Could not find profile for github username"));

          return DiscordResponse.of(ProfileCommand.toEmbed(profile));
        });
  }

  private static JsonElement toEmbed(Profile p) {
    var thumbnail = new JsonObject();
    thumbnail.addProperty("url", p.getAvatarUrl());

    var topRepos = new JsonObject();
    topRepos.addProperty("name", "Top Repositories");
    topRepos.addProperty(
        "value",
        p.getTopRepositories().map(ProfileCommand::formatRepository).collect(Collectors.joining()));

    var fields = new JsonArray();
    fields.add(topRepos);

    var embed = new JsonObject();
    embed.addProperty("title", p.getName());
    embed.addProperty("url", p.getProfileUrl());
    embed.add("thumbnail", thumbnail);
    embed.add("fields", fields);
    LOG.debug("Profile embed: {}", embed);
    return embed;
  }

  private static String formatRepository(Profile.Repository r) {
    var stars = String.format("%d%s", r.getStars(), Emoji.STAR);
    var name = String.format("[%s](%s)", r.getName(), r.getUrl());

    return String.format("**%s**, %s, %s%n", name, r.getPrimaryLanguage(), stars);
  }
}
