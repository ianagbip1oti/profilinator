package com.github.princesslana.profilinator;

import com.github.princesslana.somedb.TheDB;
import disparse.discord.smalld.Dispatcher;

public class App {

  public static void main(String[] args) {
    TheDB.initialize("profilinator");

    var disparse =
        new Dispatcher.Builder(App.class)
            .withSmalldClient(Config.getSmallD())
            .prefix(Config.getPrefix())
            .build();

    Dispatcher.init(disparse);

    Config.getSmallD().run();
  }
}
