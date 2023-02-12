package org.luoxin971.mirai.plugin.eventhandler;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import org.luoxin971.mirai.plugin.config.CommonConstant;

import static org.luoxin971.mirai.plugin.JavaPluginMain.log;

/**
 * github 监听，不考虑多线程
 *
 * @author luoxin
 * @since 2023/2/6
 */
public class OfflineEventHandler extends SimpleListenerHost {

  /** 监听私聊消息 */
  @EventHandler
  public ListeningStatus offline(BotOfflineEvent event) throws InterruptedException {
    if (!event.getReconnect()) {
      log.warning("自动重连设置为 false");
    }
    Bot bot = event.getBot();
    while (!bot.isOnline()) {
      log.info("尝试一次重连");
      bot.login();
      Thread.sleep(5000);
    }
    log.info("重新上线");
    bot.getFriend(CommonConstant.XIN_QQ_NUM).sendMessage("机器人已重新上线");
    return ListeningStatus.LISTENING;
  }
}
