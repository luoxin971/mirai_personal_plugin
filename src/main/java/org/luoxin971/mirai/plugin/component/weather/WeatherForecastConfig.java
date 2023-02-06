package org.luoxin971.mirai.plugin.component.weather;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;

public class WeatherForecastConfig extends JavaAutoSavePluginConfig {

  public static final WeatherForecastConfig INSTANCE = new WeatherForecastConfig();

  private WeatherForecastConfig() {
    // 文件名 xxx.yml
    super("org.luoxin971.WeatherForecast.config");
  }

  /** 天气API请求地址，不建议修改 */
  public final Value<String> url = value("url", "https://v0.yiketianqi.com/free/day");

  /** 天气api的用户id */
  public final Value<String> appid = value("appid", "23516491");

  /** 天气api的用户 */
  public final Value<String> appsecret = value("appsecret", "WxV9R4zP");
}
