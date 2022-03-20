package kda.learn.microservices.project.integrations.telegram;

/*
  For Emoji code see telegram bot:
    @getemojicodebot ( https://t.me/getemojicodebot )
  or site:
    https://apps.timwhitlock.info/emoji/tables/unicode
 */

public enum Emoji {
    CONFUSED("\ud83d\ude15"),
    SAD("\ud83d\ude14");

    private final String code;

    Emoji(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
