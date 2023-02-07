package org.luoxin971.mirai.plugin.exceptions;

/**
 * content
 *
 * @author: xin
 * @since: 2023/2/7
 */
public class GithubInitFailException extends RuntimeException {
  public GithubInitFailException() {
    super();
  }

  public GithubInitFailException(String message) {
    super(message);
  }

  public GithubInitFailException(String message, Throwable cause) {
    super(message, cause);
  }

  public GithubInitFailException(Throwable cause) {
    super(cause);
  }
}
