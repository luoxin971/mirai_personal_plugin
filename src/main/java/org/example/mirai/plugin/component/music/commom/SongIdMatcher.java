package org.example.mirai.plugin.component.music.commom;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SongIdMatcher {
  Pattern mc;
  MusicSource src;
  MusicCardProvider card;

  public SongIdMatcher(String regex, MusicSource src, MusicCardProvider cd) {
    mc = Pattern.compile(regex);
    this.src = Objects.requireNonNull(src);
    this.card = Objects.requireNonNull(cd);
  }

  public Message test(String s, Contact ct) throws Exception {
    Matcher m = mc.matcher(s);
    if (m.find()) {
      return card.process(src.getId(m.group(1)));
    }
    return null;
  }
}
