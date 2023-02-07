package org.luoxin971.mirai.plugin.component.github;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;

import java.util.ArrayList;
import java.util.List;

public class GithubConfig extends JavaAutoSavePluginConfig {

  public static final GithubConfig INSTANCE = new GithubConfig();

  private GithubConfig() {
    // 文件名 xxx.yml
    super("org.luoxin971.github.config");
  }

  /** 天气API请求地址，不建议修改 */
  public final Value<String> token = value("token", "ghp_abc");

  /** 天气api的用户id */
  public final Value<String> repo = value("repo", "username/repo_name");

  public final Value<List<MilestoneCode>> milestone =
      typedValue(
          "milestone",
          createKType(List.class, createKType(MilestoneCode.class)),
          new ArrayList<>() {
            {
              add(new MilestoneCode("1-1", "技术干货", "F0F8FF"));
              add(new MilestoneCode("1-2", "技术杂谈", "F0F8FF"));
              add(new MilestoneCode("1-3", "技术踩坑", "F0F8FF"));
              add(new MilestoneCode("1-4", "职场", "F0F8FF"));
              add(new MilestoneCode("2-1", "折腾", "FFEC8B"));
              add(new MilestoneCode("2-2", "tool/resource", "FFEC8B"));
              add(new MilestoneCode("3-1", "生活观点", "EE82EE"));
              add(new MilestoneCode("3-2", "生活情调", "EE82EE"));
              add(new MilestoneCode("3-3", "个人发展", "EE82EE"));
              add(new MilestoneCode("3-4", "指南、评测", "EE82EE"));
              add(new MilestoneCode("4-1", "其他", "BEBEBE"));
            }
          });
}
