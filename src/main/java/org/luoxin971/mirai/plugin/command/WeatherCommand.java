package org.luoxin971.mirai.plugin.command;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;
import org.luoxin971.mirai.plugin.JavaPluginMain;
import org.luoxin971.mirai.plugin.component.weather.ForecastReq;
import org.luoxin971.mirai.plugin.component.weather.ForecastRes;
import org.luoxin971.mirai.plugin.component.weather.WeatherForecastConfig;
import org.luoxin971.mirai.plugin.component.weather.WeatherUtil;
import org.luoxin971.mirai.plugin.config.CommonConstant;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import static org.luoxin971.mirai.plugin.JavaPluginMain.log;

/**
 * 天气预报，修改自 https://github.com/gaylong9/mirai-plugin-weather-forecast <br>
 * 天气api说明 https://www.tianqiapi.com/index/doc?version=day <br>
 * api 控制台 https://yikeapi.com/account gmail登录 <br>
 *
 * @author: xin
 * @since: 2023/2/4
 */
public final class WeatherCommand extends JRawCommand {

  public static final WeatherCommand INSTANCE = new WeatherCommand();

  private static final String ICON_PATH = JavaPluginMain.INSTANCE.getDataFolderPath() + "/wea_img/";

  public WeatherCommand() {
    super(JavaPluginMain.INSTANCE, "天气", "weather", "w");
    setDescription("获取当前天气");
    setUsage("/w <县市区名> 获取当前天气");
    setPrefixOptional(true);
  }

  @Override
  public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {
    String s = args.contentToString();
    User sender = context.getSender().getUser();
    log.info(sender.getNick() + "查询天气预报：" + s);
    Message forecastMessage = forecast(s, sender);
    sender.sendMessage(
        Optional.ofNullable(forecastMessage)
            .orElse(new QuoteReply(context.getOriginalMessage()).plus("暂无该城市天气信息，请检查输入是否有误！")));
  }

  public Message forecast(String city, Contact contact) {
    if ((Objects.isNull(city) || city.isBlank() || city.isEmpty())
        && !CommonConstant.XIN_QQ_NUM.equals(contact.getId())) {
      city = "广州";
    }
    ForecastRes res =
        WeatherUtil.requestWeather(
            new ForecastReq(
                null,
                city,
                WeatherForecastConfig.INSTANCE.appid.get(),
                WeatherForecastConfig.INSTANCE.appsecret.get()));
    Message message = null;
    if (Objects.isNull(res)) {
      log.warning("无法查询 " + city + " 天气信息");
    } else {
      log.info(city + " 天气信息为: " + res);
      message = transferToMessage(res, contact);
    }
    return message;
  }

  public Message transferToMessage(ForecastRes res, Contact contact) {
    String info = String.format("今日%s天气：\n%s", res.getCity(), res.getWea());
    MessageChainBuilder message = new MessageChainBuilder().append(info);
    File img = new File(ICON_PATH + res.getWea_img() + ".png");
    Image icon = null;
    if (img.exists()) {
      message.append(Contact.uploadImage(contact, img));
    }
    message.append(
        String.format(
            "当前温度：%s，全天温度：%s~%s°C，湿度：%s\n风向：%s，风力：%s，风速：%s",
            res.getTem(),
            res.getTem_night(),
            res.getTem_day(),
            res.getHumidity(),
            res.getWin(),
            res.getWin_speed(),
            res.getWin_meter()));
    return message.build();
  }
}
