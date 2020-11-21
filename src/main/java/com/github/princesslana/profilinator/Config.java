package com.github.princesslana.profilinator;

import com.github.princesslana.smalld.SmallD;
import com.google.common.base.Preconditions;
import disparse.parser.reflection.Injectable;
import io.github.cdimascio.dotenv.Dotenv;

public class Config {

  private static final Dotenv ENV = Dotenv.load();

  private static final SmallD SMALLD = SmallD.create(getToken());

  private static final RegistrationDao REGISTRATION_DAO = new RegistrationDao();

  public static SmallD getSmallD() {
    return SMALLD;
  }

  public static String getToken() {
    return Preconditions.checkNotNull(ENV.get("PROF_TOKEN"));
  }

  @Injectable
  public static RegistrationDao getRegistrationDao() {
    return REGISTRATION_DAO;
  }
}
