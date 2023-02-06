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
import org.luoxin971.mirai.plugin.config.CommonConfig;

import java.util.Objects;
import java.util.Optional;

/**
 * github 监听，不考虑多线程
 *
 * @author luoxin
 * @since 2023/2/6
 */
public class GithubMessageEventHandler extends SimpleListenerHost {

  /** 缓存 issue，以便修改 */
  public static GHIssue currentIssue;

  /** issue 最近更新时间 */
  public static long issueUpdateTime = 0;

  @EventHandler
  public ListeningStatus onFriendMessage(FriendMessageEvent event) {
    // 如果不是本人发的，就忽略
    if (!CommonConfig.XIN_QQ_NUM.equals(event.getSender().getId())) {
      return ListeningStatus.LISTENING;
    }
    String url = getUrl(event);
    // 如果是 url，则创建 issue
    if (Objects.nonNull(url)) {
      GHIssue issue = GithubUtil.createIssue(url);
      updateIssueCache(issue);
    }

    return ListeningStatus.STOPPED;
  }

  private String getUrl(MessageEvent event) {
    if (Objects.nonNull(event.getMessage().get(PlainText.Key))) {
      String s = event.getMessage().get(PlainText.Key).contentToString();
      if (GithubUtil.isHttpUrl(s)) {
        return s;
      }
    } else if (Objects.nonNull(event.getMessage().get(LightApp.Key))) {
      String content = event.getMessage().get(LightApp.Key).getContent();
      JsonObject jo = JsonParser.parseString(content).getAsJsonObject();
      String url = Optional.ofNullable(jo.get("url")).map(JsonElement::getAsString).orElse("");
      if (GithubUtil.isHttpUrl(url)) {
        return url;
      }
    }
    return null;
  }

  /** 更新 issue 缓存 */
  private static void updateIssueCache() {
    GithubUtil.issueUpdateTime = System.currentTimeMillis();
  }

  /** 更新 issue 缓存 */
  private static void updateIssueCache(GHIssue issue) {
    if (issue.getId() != GithubUtil.currentIssue.getId()) {
      GithubUtil.currentIssue = issue;
    }
    GithubUtil.issueUpdateTime = System.currentTimeMillis();
  }
}
