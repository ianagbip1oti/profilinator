package com.github.princesslana.profilinator.commands;

import com.github.princesslana.profilinator.GithubDao;
import com.github.princesslana.profilinator.RegistrationDao;
import com.github.princesslana.profilinator.Repository;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepositoryCommand {

  private final DiscordRequest request;
  private final GithubDao github;
  private final RegistrationDao registrations;

  public RepositoryCommand(
      DiscordRequest request, RegistrationDao registrations, GithubDao github) {
    this.request = request;
    this.registrations = registrations;
    this.github = github;
  }

  @CommandHandler(commandName = "repository")
  public DiscordResponse repository() {
    return Try.run(
        () -> {
          Preconditions.checkArgument(request.getArgs().size() == 1, "Repository name expected");

          var parts = request.getArgs().get(0).split("/");

          Preconditions.checkArgument(
              parts.length == 2, "Repository name expected in the form owner/repo");

          var owner = parts[0];
          var name = parts[1];

          var repository =
              github
                  .getRepository(owner, name)
                  .orElseThrow(() -> new IllegalStateException("Could not find repository"));

          return DiscordResponse.of(toEmbed(repository));
        });
  }

  public JsonElement toEmbed(Repository r) {
    var thumbnail = new JsonObject();
    thumbnail.addProperty("url", r.getAvatarUrl());

    var ownerMention =
        registrations.findDiscordId(r.getOwnerName()).map(id -> "<@!" + id + ">").orElse("");
    var owner = new JsonObject();
    owner.addProperty("name", "Owner");
    owner.addProperty(
        "value", String.format("[%s](%s) %s", r.getOwnerName(), r.getOwnerUrl(), ownerMention));

    var license = new JsonObject();
    license.addProperty("name", "License");
    license.addProperty("value", r.getLicense());

    var contributors = new JsonObject();
    contributors.addProperty("name", "Contributors");
    contributors.addProperty(
        "value", r.getContributors().map(this::formatContributor).collect(Collectors.joining()));

    var fields = new JsonArray();
    Stream.of(owner, license, contributors).forEach(fields::add);

    var stars = String.format("%d%s", r.getStars(), Emoji.STAR);
    var languages = r.getLanguages().collect(Collectors.joining(", "));

    JsonObject embed = new JsonObject();
    embed.addProperty("title", r.getName());
    embed.addProperty("description", languages + ", " + stars);
    embed.addProperty("url", r.getUrl());
    embed.add("thumbnail", thumbnail);
    embed.add("fields", fields);
    return embed;
  }

  private String formatContributor(Repository.Contributor c) {
    var mention = registrations.findDiscordId(c.getName()).map(id -> "<@!" + id + ">").orElse("");

    return String.format("[%s](%s) %s%n", c.getName(), c.getUrl(), mention);
  }
}
