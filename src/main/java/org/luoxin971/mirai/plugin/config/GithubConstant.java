package org.luoxin971.mirai.plugin.config;

/**
 * content
 *
 * @author: xin
 * @since: 2023/2/7
 */
public class GithubConstant {

  /** url 正则 */
  public static final String URL_REGEX_PATTERN =
      "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
          + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";

  /** issue 缓存期 3 mins */
  public static final long VALID_ISSUE_DURATION = 3 * 60 * 1000;

  /** 微信文章 host */
  public static final String WX_HOST = "mp.weixin.qq.com";

  /** repo name */
  public static final String RECORD_REPO_NAME = "luoxin971/record_db";

  public static final String INIT_FAIL_MESSAGE = "Github 初始化失败";
}
