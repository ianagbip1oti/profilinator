package com.github.princesslana.profilinator;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GithubDao {

  private final ApolloClient apollo;

  public GithubDao(ApolloClient apollo) {
    this.apollo = apollo;
  }

  public Optional<Profile> getProfile(String username) {
    var query = new UserProfileQuery(username);

    var data = new CompletableFuture<UserProfileQuery.Data>();

    apollo
        .query(query)
        .enqueue(
            new ApolloCall.Callback<>() {
              public void onFailure(ApolloException e) {
                data.completeExceptionally(e);
              }

              public void onResponse(Response<UserProfileQuery.Data> response) {
                data.complete(response.getData());
              }
            });

    try {
      return Optional.of(new Profile(data.get()));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
