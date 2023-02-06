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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.luoxin971.mirai.plugin.JavaPluginMain.log;

/**
 * 点歌，修改自 https://github.com/khjxiaogu/MiraiSongPlugin
 *
 * @author: xin
 * @since: 2023/2/4
 */
public final class TopHotCommand extends JRawCommand {
  public static final TopHotCommand INSTANCE = new TopHotCommand();

  public static final Map<String, Map.Entry<Integer, String>> map = new ConcurrentHashMap<>();

  static {
    map.put("wb", Map.entry(1, "微博"));
    map.put("zh", Map.entry(6, "知乎"));
    map.put("wx", Map.entry(5, "微信"));
    map.put("bd", Map.entry(2, "百度"));
    map.put("36kr", Map.entry(11, "36氪"));
    map.put("ssp", Map.entry(137, "少数派"));
    map.put("hx", Map.entry(32, "虎嗅"));
    map.put("it", Map.entry(119, "it之家"));
    map.put("52pj", Map.entry(68, "52破解"));
    map.put("tb", Map.entry(3, "贴吧"));
    map.put("ty", Map.entry(46, "天涯"));
    map.put("hp", Map.entry(42, "虎扑"));
    map.put("xq", Map.entry(215, "雪球"));
    map.put("dycj", Map.entry(2413, "第一财经"));
    map.put("cx", Map.entry(2496, "财新网"));
    map.put("xlcj", Map.entry(252, "新浪财经"));
  }

  public TopHotCommand() {
    super(JavaPluginMain.INSTANCE, "热点", "hot", "h");
    setDescription("热点");
    setUsage("/h <内容平台缩写> 获取平台热点内容");
    setPrefixOptional(true);
  }

  @Override
  @SneakyThrows
  public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {
    CommandSender sender = context.getSender();
    int size = args.size();
    Message message = null;
    if (size == 0) {
      MessageChainBuilder builder = new MessageChainBuilder();
      builder.append("获取平台热点：\n发送 h <平台代码> [热点个数] 即可获取指定平台当前热点\n目前支持的平台有：\n");
      map.entrySet()
          .forEach(x -> builder.append(x.getKey() + ": " + x.getValue().getValue()).append("\n"));
      message = builder.build();
      sender.sendMessage(message);
    }
    // 默认20条数据
    int length = 20;
    String platform = args.get(0).contentToString();

    Connection connect = Jsoup.connect("https://tophub.today");
    connect.header(
        "user-agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
    Document doc = connect.get();
    List<Map.Entry<String, String>> res = jsoup(doc, map.get(platform).getKey());

    if (size > 1) {
      String s = args.get(1).contentToString();
      try {
        length = Integer.parseInt(s);
      } catch (Exception e) {
        log.warning("无法解析热点长度: " + s);
      }
    }

    MessageChainBuilder builder = new MessageChainBuilder();
    for (int i = 0; i < res.size() && i < length; i++) {
      builder.append(res.get(i).getKey() + " " + res.get(i).getValue()).append("\n");
    }
    message = builder.build();
    sender.sendMessage(
        Optional.ofNullable(message)
            .orElse(new QuoteReply(context.getOriginalMessage()).plus("无法查询！")));
  }

  /**
   * 获取热点list
   *
   * @param doc html
   * @param nodeId 平台nodeid
   * @return
   */
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
