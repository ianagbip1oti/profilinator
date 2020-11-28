package com.github.princesslana.profilinator;

import java.util.stream.Stream;

public class Profile {

  private final UserProfileQuery.User user;

  public Profile(UserProfileQuery.Data data) {
    this.user = data.user;
  }

  public String getName() {
    return user.name == null ? user.login : user.name;
  }

  public String getProfileUrl() {
    return user.url.toString();
  }

  public String getAvatarUrl() {
    return user.avatarUrl.toString();
  }

  public Stream<Repository> getTopRepositories() {
    return user.topRepositories.nodes.stream().map(Repository::new);
  }

  public static class Repository {

    private final UserProfileQuery.Node repository;

    public Repository(UserProfileQuery.Node repository) {
      this.repository = repository;
    }

    public String getName() {
      return repository.name;
    }

    public String getUrl() {
      return repository.url.toString();
    }

    public Integer getStars() {
      return repository.stargazerCount;
    }

    public String getPrimaryLanguage() {
      return repository.languages.nodes.get(0).name;
    }
  }
}
