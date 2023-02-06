package org.luoxin971.mirai.plugin.command;

import lombok.SneakyThrows;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHIssue;
import org.luoxin971.mirai.plugin.JavaPluginMain;
import org.luoxin971.mirai.plugin.component.github.GithubUtil;
import org.luoxin971.mirai.plugin.config.CommonConstant;
import org.luoxin971.mirai.plugin.eventhandler.GithubMessageEventHandler;

import java.util.stream.Collectors;

/**
 * github api command
 *
 * @author luoxin
 * @since 2023/2/6
 */
public class GithubCommand extends JRawCommand {

  public static final GithubCommand INSTANCE = new GithubCommand();

  public static final String SUBCOMMAND = "c";

  public GithubCommand() {
    super(JavaPluginMain.INSTANCE, "github", "g");
    setDescription("github api");
    setUsage("/g <> <> github api");
    setPrefixOptional(true);
  }

  @Override
  @SneakyThrows
  public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {
    if (!CommonConstant.XIN_QQ_NUM.equals(context.getSender().getUser().getId())) {
      return;
    }
    int size = args.size();
    // 帮助指令
    if (size == 0) {
      MessageChainBuilder chainBuilder =
          new MessageChainBuilder()
              .append("github api 该指令需要在待收藏的链接之后才生效\n")
              .append("g <milestone> <labels...> 为链接添加milestone和label\n")
              .append("g c <comment> 为链接添加附加信息\n")
              .append("milestone 对应关系如下:\n");
      GithubUtil.milestoneMap
          .entrySet()
          .forEach(entry -> chainBuilder.append(entry.getKey() + ": " + entry.getValue() + "\n"));
      context.getSender().sendMessage(chainBuilder.build());
      return;
    }
    if (GithubUtil.checkIssueExpire()) {
      context.getSender().sendMessage("当前暂无需要保存的链接");
      return;
    }
    SingleMessage firstMessage = args.get(0);
    String firstArg = firstMessage.contentToString();
    // 无效指令
    if (size == 1
        || !SUBCOMMAND.equalsIgnoreCase(firstArg)
            && !GithubUtil.milestoneMap.keySet().contains(firstArg)) {
      context.getSender().sendMessage("当前指令无效，请检查参数！");
      return;
    }
    GithubMessageEventHandler.updateIssueCache();
    // add comment 指令
    if (SUBCOMMAND.equalsIgnoreCase(firstArg)) {
      String lastArg =
          args.stream().skip(1).map(Message::contentToString).collect(Collectors.joining());
      GithubUtil.currentIssue.comment(lastArg);
      context.getSender().sendMessage(GithubUtil.transferIssueToString(GithubUtil.currentIssue));
      return;
    }
    // 添加 milestone labels 指令
    GHIssue ghIssue =
        GithubUtil.addMetaToIssue(
            GithubUtil.currentIssue,
            firstArg,
            args.stream().skip(1).map(Message::contentToString).collect(Collectors.toList()));
    context.getSender().sendMessage(GithubUtil.transferIssueToString(ghIssue));
  }
}
