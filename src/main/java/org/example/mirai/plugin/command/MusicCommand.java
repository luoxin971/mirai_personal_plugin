package org.example.mirai.plugin.command;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.QuoteReply;
import org.example.mirai.plugin.JavaPluginMain;
import org.example.mirai.plugin.component.music.MusicShare;

import java.util.Optional;

import static org.example.mirai.plugin.JavaPluginMain.log;

/**
 * content
 *
 * @author: xin
 * @since: 2023/2/4
 */
public final class MusicCommand extends JSimpleCommand {
  public static final MusicCommand INSTANCE = new MusicCommand();

  public MusicCommand() {
    super(JavaPluginMain.INSTANCE, "music");
    setDescription("点歌");
    setPrefixOptional(true);
  }

  /**
   * 处理点歌命令，若搜索有结果则返回音乐链接，否则给予提示<br>
   * 命令如下：<br>
   * /music 稻香
   *
   * @param context
   * @param keyword
   */
  @Handler
  public void handle(CommandContext context, String keyword) {
    CommandSender sender = context.getSender();
    log.info(sender.getUser().getNick() + "点歌：" + keyword);
    Message message = MusicShare.generateMusicMessage(keyword);

    sender.sendMessage(
        Optional.ofNullable(message)
            .orElse(new QuoteReply(context.getOriginalMessage()).plus("暂无该歌曲信息！")));
  }
}
