package org.luoxin971.mirai.plugin.action;

import cn.hutool.cron.CronUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.Message;
import org.luoxin971.mirai.plugin.command.WeatherCommand;
import org.luoxin971.mirai.plugin.component.music.MusicShare;
import org.luoxin971.mirai.plugin.config.CommonConfig;
import org.luoxin971.mirai.plugin.config.ScheduleConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.luoxin971.mirai.plugin.JavaPluginMain.log;

/**
 * 定时任务
 *
 * @author: xin
 * @since: 2023/2/4
 */
public class ScheduleAction {

  private static Map<String, List<Long>> sleepSchedule;
  private static Map<String, List<Map.Entry<Long, String>>> getUpSchedule;

  public static void init() {
    log.info("初始化定时任务...");
    CronUtil.setMatchSecond(true);
    CronUtil.start(true);

    sleepSchedule = new ConcurrentHashMap<>();
    sleepSchedule.put(
        "睡觉", new ArrayList<>(Arrays.asList(CommonConfig.XIN_QQ_NUM, CommonConfig.GRACE_QQ_NUM)));
    getUpSchedule = new ConcurrentHashMap<>();
    getUpSchedule.put(
        "起床",
        new ArrayList<>(
            Arrays.asList(
                Map.entry(CommonConfig.XIN_QQ_NUM, "广州"),
                Map.entry(CommonConfig.GRACE_QQ_NUM, "广州"))));

    try {
      CronUtil.schedule(
          "睡觉",
          ScheduleConfig.GOOD_NIGHT_CRON,
          () -> {
            List<Bot> botList = Bot.getInstances();
            log.info("botList size: " + botList.size() + "content: " + botList);
            botList.forEach(
                bot ->
                    sleepSchedule
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

    try {
      CronUtil.schedule(
          "起床",
          ScheduleConfig.GOOD_MORNING_CRON,
          () -> {
            List<Bot> botList = Bot.getInstances();
            log.info("botList size: " + botList.size() + "content: " + botList);
            botList.forEach(
                bot ->
                    getUpSchedule
                        .get("起床")
                        .forEach(
                            f -> {
                              Friend friend = bot.getFriend(f.getKey());
                              friend.sendMessage("起床了！！！");
                              Message forecast =
                                  WeatherCommand.INSTANCE.forecast(f.getValue(), friend);
                              Optional.ofNullable(forecast).ifPresent(m -> friend.sendMessage(m));
                              friend.sendMessage(MusicShare.generateMusicMessage("稻香"));
                            }));
          });
      log.info("添加定时任务2成功!!");
    } catch (Exception e) {
      log.error("添加定时任务2出错!!", e);
    }

    log.info("定时器加载成功!");
  }
}
