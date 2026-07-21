# TvbSmp Rankup Plugin

This repository contains a Minecraft (Spigot/Paper) plugin that provides rank-up functionality with configurable requirements: play time, money (Vault), and mined blocks.

Structure
- src/main/java/
  - tvbclub/tvbsmp/
    - TvbSmpPlugin.java (main plugin class)
    - rank/
      - RankManager.java
      - requirements/
        - RankRequirement.java
        - TimeRequirement.java
        - MoneyRequirement.java
        - MineBlockRequirement.java
      - command/
        - RankCommand.java
      - listener/
        - PlaytimeListener.java
        - BlockListener.java
    - storage/
      - PlayerData.java
      - YamlStorageProvider.java
- src/main/resources/
  - plugin.yml
  - config.yml
  - messages.yml
- pom.xml (Maven build)
- README.md

Build
1. Ensure you have Java 11+ and Maven installed.
2. Run mvn package to build the plugin JAR (target/*.jar).
3. Place the JAR in your server's plugins/ directory and start the server.

Notes
- Vault is required for economy (money) support. Install Vault and an economy plugin on your server.
- The plugin uses YAML storage (players.yml) by default. You can replace storage with a database later by implementing a different StorageProvider.

If you want Gradle instead of Maven, or want me to open a PR instead of pushing directly to main, tell me which option you prefer.
