package org.luoxin971.mirai.plugin.eventhandler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.PlainText;
import org.kohsuke.github.GHIssue;
import org.luoxin971.mirai.plugin.component.github.GithubUtil;
import org.luoxin971.mirai.plugin.config.CommonConstant;

import java.util.Objects;
import java.util.Optional;

import static org.luoxin971.mirai.plugin.JavaPluginMain.log;

/**
 * github 监听，不考虑多线程
 *
 * @author luoxin
 * @since 2023/2/6
 */
public class GithubMessageEventHandler extends SimpleListenerHost {

  /** 监听私聊消息 */
  @EventHandler
  public ListeningStatus onFriendMessage(FriendMessageEvent event) {
    log.info(event.getMessage().serializeToMiraiCode());
    // 如果不是本人发的，就忽略
    if (!CommonConstant.XIN_QQ_NUM.equals(event.getSender().getId())) {
      return ListeningStatus.LISTENING;
    }
    String url = getUrl(event);
    // 如果是 url，则创建 issue
    if (Objects.nonNull(url)) {
      GHIssue issue = GithubUtil.createIssue(url);
      updateIssueCache(issue);
      log.info("issue: " + GithubUtil.transferIssueToString(issue));
      event.getSender().sendMessage(GithubUtil.transferIssueToString(issue));
    }

    return ListeningStatus.LISTENING;
  }

  /** 从 event 中获取 url，若非有效 url，则返回 null */
  private String getUrl(MessageEvent event) {
    if (Objects.nonNull(event.getMessage().get(LightApp.Key))) {
      String content = event.getMessage().get(LightApp.Key).getContent();
      JsonObject jo = JsonParser.parseString(content).getAsJsonObject();
      String url =
          Optional.ofNullable(jo.get("meta"))
              .map(JsonElement::getAsJsonObject)
              .map(x -> x.get("news"))
              .map(x -> x.getAsJsonObject())
              .map(x -> x.get("jumpUrl"))
              .map(JsonElement::getAsString)
              .orElse("");
      if (GithubUtil.isHttpUrl(url)) {
        log.info("LightApp url: " + url);
        return url;
      }
    }
    if (Objects.nonNull(event.getMessage().get(PlainText.Key))) {
      String url = event.getMessage().get(PlainText.Key).contentToString();
      if (GithubUtil.isHttpUrl(url)) {
        log.info("PlainText url: " + url);
        return url;
      }
    }
    return null;
  }

  /** 更新 issue 缓存 */
  public static void updateIssueCache() {
    GithubUtil.issueUpdateTime = System.currentTimeMillis();
  }

  /** 更新 issue 缓存 */
  private static void updateIssueCache(GHIssue issue) {
    if (GithubUtil.currentIssue == null || issue.getId() != GithubUtil.currentIssue.getId()) {
      GithubUtil.currentIssue = issue;
    }
    GithubUtil.issueUpdateTime = System.currentTimeMillis();
  }
}
