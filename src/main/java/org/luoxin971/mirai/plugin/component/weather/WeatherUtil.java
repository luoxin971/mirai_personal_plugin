package org.luoxin971.mirai.plugin.component.weather;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class WeatherUtil {

  //  private static final MiraiLogger logger =
  // MiraiLogger.Factory.INSTANCE.create(WeatherUtil.class);

  public static final Gson GSON = new GsonBuilder().setLenient().create();

  //  /** 重新获取PluginData中的相关数据 */
  //  public static void reloadPluginData() {
  //    Value<String> urlValue = WeatherForecastConfig.INSTANCE.url;
  //    url = urlValue.get();
  //    Value<String> appidValue = WeatherForecastConfig.INSTANCE.appid;
  //    appid = appidValue.get();
  //    Value<String> appsecretValue = WeatherForecastConfig.INSTANCE.appsecret;
  //    appsecret = appsecretValue.get();
  //  }

  /** 发送请求获取天气，参数正确时返回ForecastInfo对象，若请求失败返回null */
  public static ForecastRes requestWeather(ForecastReq req) {
    StringBuilder sb = new StringBuilder(WeatherForecastConfig.INSTANCE.url.get());
    sb.append("?unescape=1&appid=")
        .append(req.getAppId())
        .append("&appsecret=")
        .append(req.getAppSecret());
    if (Objects.nonNull(req.getCity())) {
      sb.append("&city=").append(req.getCity());
    } else if (Objects.nonNull(req.getCityid())) {
      sb.append("&cityid=").append(req.getCityid());
    }
    try {
      Document document =
          Jsoup.connect(sb.toString())
              .header("Accept", "application/json, text/javascript, */*; q=0.01")
              .ignoreContentType(true)
              .timeout(3000)
              .get();
      String json = document.body().text();
      if (json.contains("errcode")) {
        return null;
      }
      ForecastRes info = GSON.fromJson(json, ForecastRes.class);
      return info;
    } catch (IOException e) {
      System.out.println("获取天气失败: " + e.getMessage());
      return null;
    }
  }
}
