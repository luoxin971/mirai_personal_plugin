package org.luoxin971.mirai.plugin.component.music;

import net.mamoe.mirai.message.data.Message;
import org.luoxin971.mirai.plugin.JavaPluginMain;
import org.luoxin971.mirai.plugin.component.music.cardprovider.MiraiMusicCard;
import org.luoxin971.mirai.plugin.component.music.commom.MusicCardProvider;
import org.luoxin971.mirai.plugin.component.music.commom.MusicInfo;
import org.luoxin971.mirai.plugin.component.music.commom.MusicSource;
import org.luoxin971.mirai.plugin.component.music.musicsource.KugouMusicSource;
import org.luoxin971.mirai.plugin.component.music.musicsource.NetEaseMusicSource;
import org.luoxin971.mirai.plugin.component.music.musicsource.QQMusicSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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
    // 网易
    sources.add(new NetEaseMusicSource());
    // 酷狗
    sources.add(new KugouMusicSource());
    // QQ音乐
    sources.add(new QQMusicSource());

    JavaPluginMain.log.info("sources size: " + sources.size());
  }

  /**
   * 生成 MusicShare message
   *
   * @param keyword 搜索关键词
   * @return 抛出异常或者搜不到时返回 null
   */
  public static Message generateMusicMessage(String keyword) {
    JavaPluginMain.log.info("正在搜索: " + keyword);
    MusicInfo musicInfo = null;
    List<MusicSource> list = new ArrayList<>(sources);
    Collections.shuffle(list);

    for (MusicSource source : list) {
      try {
        musicInfo = source.get(keyword);
      } catch (Exception e) {
        JavaPluginMain.log.warning(
            source.getClass().getSimpleName() + "关键词 " + keyword + " 搜索失败！", e);
      }
    }
    JavaPluginMain.log.info("搜索结束: " + keyword);
    if (Objects.isNull(musicInfo)) {
      return null;
    }
    try {
      return card.process(musicInfo);
    } catch (Exception e) {
      JavaPluginMain.log.error("process 失败", e);
      return null;
    }
  }
}
