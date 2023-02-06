package org.luoxin971.mirai.plugin.component.github;

import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.kohsuke.github.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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
  public static final GitHub github;

  public static final GHRepository repo;

  public static final ConcurrentHashMap<String, String> milestoneMap;

  public static final ConcurrentHashMap<String, String> colorMap;

  /** 缓存 issue，以便修改 */
  public static GHIssue currentIssue;

  /** issue 最近更新时间 */
  public static long issueUpdateTime = 0;

  static {
    try {
      github = GitHubBuilder.fromPropertyFile().build();
      repo = github.getRepository(RECORD_REPO_NAME);
      milestoneMap = new ConcurrentHashMap<>(16);
      milestoneMap.put("1-1", "技术干货");
      milestoneMap.put("1-2", "技术杂谈");
      milestoneMap.put("1-3", "技术踩坑");
      milestoneMap.put("1-4", "职场");
      milestoneMap.put("2-1", "折腾");
      milestoneMap.put("2-2", "tool/resource");
      milestoneMap.put("3-1", "生活观点");
      milestoneMap.put("3-2", "生活情调");
      milestoneMap.put("3-3", "个人发展");
      milestoneMap.put("3-4", "指南、评测");
      milestoneMap.put("4-1", "其他");
      colorMap = new ConcurrentHashMap<>(16);
      colorMap.put("1", "F0F8FF");
      colorMap.put("2", "FFEC8B");
      colorMap.put("3", "EE82EE");
      colorMap.put("4", "BEBEBE");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

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
  public static GHIssue addMetaToIssue(
      GHIssue issue, String milestoneCode, List<String> labelNames) {
    if (checkIssueExpire()) {
      return null;
    }
    List<GHMilestone> miles = repo.listMilestones(GHIssueState.ALL).toList();
    GHMilestone m =
        miles.stream()
            .filter(x -> x.getTitle().equals(milestoneMap.get(milestoneCode)))
            .findAny()
            .orElseThrow(() -> new RuntimeException("不存在该 milestone"));
    issue.setMilestone(m);
    String color = colorMap.get(String.valueOf(milestoneCode.charAt(0)));
    List<GHLabel> labels =
        labelNames.stream()
            .map(
                x -> {
                  try {
                    return repo.listLabels().toList().stream()
                        .filter(label -> label.getName().equals(x))
                        .findAny()
                        .or(
                            () -> {
                              try {
                                return Optional.ofNullable(
                                    repo.createLabel(x.toLowerCase(), color));
                              } catch (IOException e) {
                                log.error("创建label失败: " + x);
                                throw new RuntimeException(e);
                              }
                            })
                        .get();
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                })
            .collect(Collectors.toList());
    issue.addLabels(labels);
    return repo.getIssue(issue.getNumber());
  }

  /** 检查 issue 最近更新时间是否在 3mins 前 */
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

  public static String transferIssueToString(GHIssue issue) {
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
