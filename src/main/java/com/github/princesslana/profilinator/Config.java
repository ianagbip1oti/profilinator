package com.github.princesslana.profilinator;

import com.apollographql.apollo.ApolloClient;
import com.github.princesslana.smalld.SmallD;
import com.google.common.base.Preconditions;
import disparse.parser.reflection.Injectable;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class Config {

  private static final Dotenv ENV = Dotenv.configure().ignoreIfMissing().load();

  private static final SmallD SMALLD = SmallD.create(getDiscordToken());

  private static final OkHttpClient GITHUB_HTTP_CLIENT =
      new OkHttpClient.Builder()
          .addInterceptor(addHeader("Authorization", "bearer " + getGithubToken()))
          .build();

  private static final ApolloClient APOLLO_CLIENT =
      ApolloClient.builder()
          .serverUrl("https://api.github.com/graphql")
          .okHttpClient(GITHUB_HTTP_CLIENT)
          .build();

  private static final GithubDao GITHUB_DAO = new GithubDao(APOLLO_CLIENT, GITHUB_HTTP_CLIENT);
  private static final RegistrationDao REGISTRATION_DAO = new RegistrationDao();

  public static SmallD getSmallD() {
    return SMALLD;
  }

  public static String getPrefix() {
    return ENV.get("PROF_PREFIX", "gh!");
  }

  public static String getDiscordToken() {
    return Preconditions.checkNotNull(ENV.get("PROF_DISCORD_TOKEN"));
  }

  public static String getGithubToken() {
    return Preconditions.checkNotNull(ENV.get("PROF_GITHUB_TOKEN"));
  }

  @Injectable
  public static GithubDao getGithubDao() {
    return GITHUB_DAO;
  }

  @Injectable
  public static RegistrationDao getRegistrationDao() {
    return REGISTRATION_DAO;
  }

  private static Interceptor addHeader(String name, String value) {
    return c -> c.proceed(c.request().newBuilder().header(name, value).build());
  }
}
