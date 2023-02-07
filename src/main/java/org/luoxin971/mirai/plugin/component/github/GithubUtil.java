package org.luoxin971.mirai.plugin.component.github;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.kohsuke.github.*;
import org.luoxin971.mirai.plugin.JavaPluginMain;
import org.luoxin971.mirai.plugin.config.GithubConstant;
import org.luoxin971.mirai.plugin.exceptions.GithubInitFailException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.luoxin971.mirai.plugin.JavaPluginMain.log;
import static org.luoxin971.mirai.plugin.config.GithubConstant.*;

/**
 * github api
 *
 * @author luoxin
 * @since 2023/2/6
 */
public class GithubUtil {
  /** GitHub 实例 */
  public static GitHub github;

  public static GHRepository repo;

  public static ConcurrentHashMap<String, Map.Entry<String, String>> milestoneMap;

  /** 缓存 issue，以便修改 */
  public static GHIssue currentIssue;

  /** issue 最近更新时间 */
  public static long issueUpdateTime = 0;

  public static void init() {
    try {
      github = GitHub.connectUsingOAuth(GithubConfig.INSTANCE.token.get());
      repo = github.getRepository(GithubConfig.INSTANCE.repo.get());
      List<MilestoneCode> list = GithubConfig.INSTANCE.milestone.get();
      milestoneMap = new ConcurrentHashMap<>(list.size());
      for (MilestoneCode milestone : list) {
        milestoneMap.put(
            milestone.getCode(),
            Map.entry(milestone.getMilestoneName(), milestone.getLabelColor()));
      }
    } catch (IOException e) {
      throw new GithubInitFailException(GithubConstant.INIT_FAIL_MESSAGE, e);
    }
  }

  /** 重新读取配置文件进行更新 */
  public static final void updateConfig() {
    JavaPluginMain.INSTANCE.reloadPluginConfig(GithubConfig.INSTANCE);
    init();
  }

  /**
   * 根据 url 创建 issue
   *
   * @param url 收藏网页
   * @return
   */
  @SneakyThrows
  public static GHIssue createIssue(String url) {
    Connection connect = Jsoup.connect(url);
    connect.method(Connection.Method.GET);
    Document document = connect.get();
    GHIssue issue = repo.createIssue(parseTitle(url, document)).body(url).create();
    return issue;
  }

  private static String parseTitle(String url, Document document) {
    String host = null;
    try {
      host = new URL(url).getHost();
    } catch (MalformedURLException e) {
      return document.title();
    }
    // 微信文章的 title 会是空的
    Elements elements = document.head().getElementsByAttributeValue("property", "og:title");
    if (WX_HOST.equalsIgnoreCase(host) && !elements.isEmpty()) {
      return elements.get(0).attr("content");
    }

    return document.title().isBlank() ? url : document.title();
  }

  @SneakyThrows
  @NotNull
  public static GHIssue addMetaToIssue(
      GHIssue issue, String milestoneCode, List<String> labelNames) {
    List<GHMilestone> miles = repo.listMilestones(GHIssueState.ALL).toList();
    String milestoneName = milestoneMap.get(milestoneCode).getKey();
    GHMilestone m =
        miles.stream()
            .filter(x -> x.getTitle().equals(milestoneName))
            .findAny()
            .orElseGet(
                () -> {
                  try {
                    return repo.createMilestone(milestoneName, "");
                  } catch (IOException e) {
                    throw new RuntimeException("不存在且无法创建该 milestone");
                  }
                });
    issue.setMilestone(m);
    String color = milestoneMap.get(milestoneCode).getValue();
    List<GHLabel> labels =
        labelNames.stream()
            .map(
                x -> {
                  try {
                    return repo.listLabels().toList().stream()
                        .filter(label -> label.getName().equals(x))
                        .findAny()
                        .orElseGet(
                            () -> {
                              try {
                                return repo.createLabel(x.toLowerCase(), color);
                              } catch (IOException e) {
                                log.error("创建label失败: " + x);
                                throw new RuntimeException(e);
                              }
                            });
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                })
            .collect(Collectors.toList());
    issue.addLabels(labels);
    return repo.getIssue(issue.getNumber());
  }

  /** 检查 issue 最近更新时间是否在 3 mins 前 */
  public static boolean checkIssueExpire() {
    return System.currentTimeMillis() - GithubUtil.issueUpdateTime > VALID_ISSUE_DURATION;
  }

  /**
   * 判断字符串是否为URL
   *
   * @param urls url
   * @return true:是URL、false:不是URL
   */
  public static boolean isHttpUrl(String urls) {
    Pattern pat = Pattern.compile(URL_REGEX_PATTERN);
    Matcher mat = pat.matcher(urls.trim());
    return mat.matches();
  }

  public static String transferIssueToString(@NotNull GHIssue issue) {
    return String.format(
        "number: %s\ntitle: %s\nbody: %s\nurl: %s\nmilestone: %s\nlabels: %s",
        issue.getNumber(),
        issue.getTitle(),
        issue.getBody(),
        issue.getHtmlUrl(),
        Optional.ofNullable(issue.getMilestone()).map(GHMilestone::getTitle).orElse(""),
        issue.getLabels().stream().map(GHLabel::getName).collect(Collectors.joining(", ")));
  }
}
