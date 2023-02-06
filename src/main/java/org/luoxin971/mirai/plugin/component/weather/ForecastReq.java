package org.luoxin971.mirai.plugin.component.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * api请求数据
 *
 * @author: xin
 * @since: 2023/2/4
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForecastReq {
  @Nullable private String cityid;
  @Nullable private String city;
  @NonNull private String appId;
  @NonNull private String appSecret;
}
