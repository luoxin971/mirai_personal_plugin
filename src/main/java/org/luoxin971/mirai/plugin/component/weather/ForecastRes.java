package org.luoxin971.mirai.plugin.component.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * api响应数据json <br>
 * { "nums":226, //今日实时请求次数 "cityid":"101120101", //城市ID "city":"济南", "date":"2022-05-05",
 * "week":"星期四", "update_time":"22:38", //更新时间 "wea":"多云", //天气情况 "wea_img":"yun", //天气标识
 * "tem":"25", //实况温度 "tem_day":"30", //白天温度(高温) "tem_night":"23", //夜间温度(低温) "win":"南风", //风向
 * "win_speed":"3级", //风力 "win_meter":"19km\/h", //风速 "air":"53", //空气质量 "pressure":"987", //气压
 * "humidity":"27%" //湿度 }
 *
 * @author: xin
 * @since: 2023/2/4
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForecastRes {
  private String nums;
  private String cityid;
  private String city;
  private String date;
  private String week;
  private String update_time;
  private String wea;
  private String wea_img;
  private String tem;
  private String tem_day;
  private String tem_night;
  private String win;
  private String win_speed;
  private String win_meter;
  private String air;
  private String pressure;
  private String humidity;
}
