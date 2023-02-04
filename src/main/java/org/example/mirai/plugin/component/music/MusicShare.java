package org.example.mirai.plugin.component.music;

import net.mamoe.mirai.message.data.Message;
import org.example.mirai.plugin.component.music.cardprovider.MiraiMusicCard;
import org.example.mirai.plugin.component.music.commom.MusicCardProvider;
import org.example.mirai.plugin.component.music.commom.MusicInfo;
import org.example.mirai.plugin.component.music.commom.MusicSource;
import org.example.mirai.plugin.component.music.musicsource.KugouMusicSource;
import org.example.mirai.plugin.component.music.musicsource.NetEaseMusicSource;
import org.example.mirai.plugin.component.music.musicsource.QQMusicSource;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.example.mirai.plugin.JavaPluginMain.log;

/**
 * content
 *
 * @author: xin
 * @since: 2023/2/4
 */
public class MusicShare {
  /** 音乐来源. */
  public static final List<MusicSource> sources = new CopyOnWriteArrayList<>();

  /** 外观来源 */
  public static final MusicCardProvider card = new MiraiMusicCard();

  static {
    // 注册音乐来源
    // QQ音乐
    sources.add(new QQMusicSource());
    // 网易
    sources.add(new NetEaseMusicSource());
    // 酷狗
    sources.add(new KugouMusicSource());

    log.info("sources size: " + sources.size());
  }

  /**
   * 生成 MusicShare message
   *
   * @param keyword 搜索关键词
   * @return 抛出异常或者搜不到时返回 null
   */
  public static Message generateMusicMessage(String keyword) {
    log.info("正在搜索: " + keyword);
    MusicInfo musicInfo = null;

    for (MusicSource source : sources) {
      try {
        musicInfo = source.get(keyword);
      } catch (Exception e) {
        log.warning("关键词 " + keyword + " 搜索失败！", e);
      }
    }
    log.info("搜索结束: " + keyword);
    if (Objects.isNull(musicInfo)) {
      return null;
    }
    try {
      return card.process(musicInfo);
    } catch (Exception e) {
      log.error("process 失败", e);
      return null;
    }
  }
}
