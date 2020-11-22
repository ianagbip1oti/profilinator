package com.github.princesslana.profilinator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Repository {

  private final RepositoryQuery.Repository repository;
  private final JsonArray contributors;

  public Repository(RepositoryQuery.Data data, JsonArray contributors) {
    this.repository = data.repository;
    this.contributors = contributors;
  }

  public String getName() {
    return repository.name;
  }

  public int getStars() {
    return repository.stargazerCount;
  }

  public String getLicense() {
    return repository.licenseInfo.name;
  }

  public Stream<String> getLanguages() {
    return repository.languages.nodes.stream().map(n -> n.name);
  }

  public String getUrl() {
    return repository.url.toString();
  }

  public String getAvatarUrl() {
    return repository.owner.avatarUrl.toString();
  }

  public String getOwnerName() {
    return repository.owner.login;
  }

  public String getOwnerUrl() {
    return repository.owner.url.toString();
  }

  public Stream<Contributor> getContributors() {
    return StreamSupport.stream(contributors.spliterator(), false)
        .map(el -> new Contributor(el.getAsJsonObject()))
        .limit(5);
  }

  public static class Contributor {
    private final JsonObject contributor;

    public Contributor(JsonObject contributor) {
      this.contributor = contributor;
    }

    public String getName() {
      return contributor.get("login").getAsString();
    }

    public String getUrl() {
      return contributor.get("html_url").getAsString();
    }
  }
}
