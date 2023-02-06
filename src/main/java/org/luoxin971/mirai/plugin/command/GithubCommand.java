package org.luoxin971.mirai.plugin.command;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import org.luoxin971.mirai.plugin.JavaPluginMain;

/**
 * github api command
 *
 * @author luoxin
 * @since 2023/2/6
 */
public class GithubCommand extends JRawCommand {

  public static final GithubCommand INSTANCE = new GithubCommand();

  public GithubCommand() {
    super(JavaPluginMain.INSTANCE, "github", "g");
    setDescription("github api");
    setUsage("/g <>");
    setPrefixOptional(true);
  }

  @Override
  public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {}
}
