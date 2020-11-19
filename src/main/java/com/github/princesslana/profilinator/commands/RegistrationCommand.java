package com.github.princesslana.profilinator.commands;

import com.github.princesslana.profilinator.RegistrationDao;
import com.google.common.base.Preconditions;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;

public class RegistrationCommand {

  private final DiscordRequest request;

  private final RegistrationDao registrations;

  public RegistrationCommand(DiscordRequest request, RegistrationDao registrations) {
    this.request = request;
    this.registrations = registrations;
  }

  @CommandHandler(commandName = "register")
  public DiscordResponse register() {
    return Try.run(
        () -> {
          Preconditions.checkArgument(
              request.getArgs().size() == 1, "You must include your GitHub username");

          var discordUserId = request.getDispatcher().identityFromEvent(request.getEvent());
          var githubUsername = request.getArgs().get(0);

          registrations.register(discordUserId, githubUsername);

          return DiscordResponse.of(Emoji.CHECKMARK + "You have been registred");
        });
  }
}
