package org.example.mirai.plugin.action;

import cn.hutool.cron.CronUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.example.mirai.plugin.component.music.MusicShare;
import org.example.mirai.plugin.config.CommonConfig;
import org.example.mirai.plugin.config.ScheduleConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.mirai.plugin.JavaPluginMain.log;

/**
 * 定时任务
 *
 * @author: xin
 * @since: 2023/2/4
 */
public class ScheduleAction {

  private static Map<String, List<Long>> scheduleReceiver;

  public static void init() {
    log.info("初始化定时任务...");
    CronUtil.setMatchSecond(true);
    CronUtil.start(true);

    scheduleReceiver = new ConcurrentHashMap<>();
    scheduleReceiver.put(
        "睡觉", new ArrayList<>(Arrays.asList(CommonConfig.XIN_QQ_NUM, CommonConfig.GRACE_QQ_NUM)));

    try {
      CronUtil.schedule(
          "睡觉",
          ScheduleConfig.GOOD_NIGHT_CRON,
          () -> {
            List<Bot> botList = Bot.getInstances();
            log.info("botList size: " + botList.size() + "content: " + botList);
            botList.forEach(
                bot ->
                    scheduleReceiver
                        .get("睡觉")
                        .forEach(
                            f -> {
                              Friend friend = bot.getFriend(f);
                              friend.sendMessage("睡觉了！！！");
                              friend.sendMessage(MusicShare.generateMusicMessage("稻香"));
                            }));
          });
      log.info("添加定时任务1成功!!");
    } catch (Exception e) {
      log.error("添加定时任务1出错!!", e);
    }

    log.info("定时器加载成功!");
  }
}
