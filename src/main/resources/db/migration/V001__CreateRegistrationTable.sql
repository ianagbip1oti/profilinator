CREATE TABLE registration (
  discord_user_id VARCHAR(64),
  github_username VARCHAR(64) NOT NULL,
  PRIMARY KEY(discord_user_id),
  UNIQUE(github_username)
);
