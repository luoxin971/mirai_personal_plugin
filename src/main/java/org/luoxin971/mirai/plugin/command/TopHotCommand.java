package org.luoxin971.mirai.plugin.command;

import lombok.SneakyThrows;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.luoxin971.mirai.plugin.JavaPluginMain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 点歌，修改自 https://github.com/khjxiaogu/MiraiSongPlugin
 *
 * @author: xin
 * @since: 2023/2/4
 */
public final class TopHotCommand extends JRawCommand {
  public static final TopHotCommand INSTANCE = new TopHotCommand();

  public static final Map<String, Integer> map = new ConcurrentHashMap<>();

  static {
    map.put("wb", 1);
    map.put("zh", 6);
    map.put("wx", 5);
    map.put("bd", 2);
    map.put("36ke", 11);
    map.put("ssp", 137);
    map.put("hx", 32);
    map.put("it", 119);
    map.put("52pj", 68);
    map.put("tb", 3);
    map.put("ty", 46);
    map.put("hp", 42);
    map.put("xq", 215);
    map.put("dycj", 2413);
    map.put("cx", 2496);
    map.put("xlcj", 252);
  }

  public TopHotCommand() {
    super(JavaPluginMain.INSTANCE, "hot", "h");
    setDescription("热点");
    setPrefixOptional(true);
  }

  /**
   * @param context
   * @param args
   */
  @Override
  @SneakyThrows
  public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {
    CommandSender sender = context.getSender();
    String keyword = args.contentToString();
    Message message = null;
    if (Objects.isNull(keyword)
        || keyword.isBlank()
        || keyword.isEmpty()
        || "list".equals(keyword)) {
      MessageChainBuilder builder = new MessageChainBuilder();
      map.keySet().forEach(x -> builder.append(x).append("\n"));
      message = builder.build();
    } else {

      Connection connect = Jsoup.connect("https://tophub.today");
      connect.header(
          "user-agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
      Document doc = connect.get();
      List<Map.Entry<String, String>> res = jsoup(doc, map.get(keyword));

      MessageChainBuilder builder = new MessageChainBuilder();
      res.forEach(x -> builder.append(x.getKey() + " " + x.getValue()).append("\n"));
      message = builder.build();
    }
    sender.sendMessage(
        Optional.ofNullable(message)
            .orElse(new QuoteReply(context.getOriginalMessage()).plus("无法查询！")));
  }

  public List<Map.Entry<String, String>> jsoup(Document doc, Integer nodeId) {
    List<Map.Entry<String, String>> list = new ArrayList<>();
    Element wb = doc.getElementById("node-" + nodeId);
    Elements elements = wb.getElementsByAttribute("itemid");
    for (Element e : elements) {
      String href = e.attr("href");
      String title = e.getElementsByClass("t").text();
      list.add(Map.entry(title, href));
    }
    return list;
  }
}
