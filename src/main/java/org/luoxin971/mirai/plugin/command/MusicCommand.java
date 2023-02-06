package org.luoxin971.mirai.plugin.command;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;
import org.luoxin971.mirai.plugin.JavaPluginMain;
import org.luoxin971.mirai.plugin.component.music.MusicShare;

import java.util.Objects;
import java.util.Optional;

/**
 * 点歌，修改自 https://github.com/khjxiaogu/MiraiSongPlugin
 *
 * @author: xin
 * @since: 2023/2/4
 */
public final class MusicCommand extends JRawCommand {
  public static final MusicCommand INSTANCE = new MusicCommand();

  public MusicCommand() {
    super(JavaPluginMain.INSTANCE, "music");
    setDescription("点歌");
    setPrefixOptional(true);
  }

  /**
   * 处理点歌命令，若搜索有结果则返回音乐链接，否则默认播放<br>
   * 命令如下：<br>
   * /music 稻香
   *
   * @param context
   * @param args
   */
  @Override
  public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {
    CommandSender sender = context.getSender();
    // fixme 英文歌名中间由空格的话，空格会不见
    String keyword = args.contentToString();
    if (Objects.isNull(keyword) || keyword.isBlank() || keyword.isEmpty()) {
      keyword = "你曾是少年";
    }
    JavaPluginMain.log.info(sender.getUser().getNick() + "点歌：" + keyword);
    Message message = MusicShare.generateMusicMessage(keyword);

    sender.sendMessage(
        Optional.ofNullable(message)
            .orElse(new QuoteReply(context.getOriginalMessage()).plus("暂无该歌曲信息！")));
  }
}
