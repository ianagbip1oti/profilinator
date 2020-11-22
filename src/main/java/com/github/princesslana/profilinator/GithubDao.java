package com.github.princesslana.profilinator;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GithubDao {

  private final ApolloClient apollo;
  private final OkHttpClient http;

  public GithubDao(ApolloClient apollo, OkHttpClient http) {
    this.apollo = apollo;
    this.http = http;
  }

  public Optional<Profile> getProfile(String username) {
    var data = new CompletableFuture<UserProfileQuery.Data>();

    apollo.query(new UserProfileQuery(username)).enqueue(complete(data));

    return Optional.of(new Profile(get(data)));
  }

  public Optional<Repository> getRepository(String owner, String name) {
    var data = new CompletableFuture<RepositoryQuery.Data>();

    apollo.query(new RepositoryQuery(owner, name)).enqueue(complete(data));

    Request getContributors =
        new Request.Builder()
            .url(String.format("https://api.github.com/repos/%s/%s/contributors", owner, name))
            .build();

    JsonArray contributors;
    try {
      try (var response = http.newCall(getContributors).execute()) {
        contributors = JsonParser.parseString(response.body().string()).getAsJsonArray();
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to fetch contirbutors", e);
    }

    return Optional.of(new Repository(get(data), contributors));
  }

  private <T> T get(CompletableFuture<T> f) {
    try {
      return f.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> ApolloCall.Callback<T> complete(CompletableFuture<T> f) {
    return new ApolloCall.Callback<>() {
      public void onFailure(ApolloException e) {
        f.completeExceptionally(e);
      }

      public void onResponse(Response<T> response) {
        f.complete(response.getData());
      }
    };
  }
}
