package org.luoxin971.mirai.plugin;

import kotlin.Lazy;
import kotlin.LazyKt;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.permission.*;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.luoxin971.mirai.plugin.action.ScheduleAction;
import org.luoxin971.mirai.plugin.command.GithubCommand;
import org.luoxin971.mirai.plugin.command.MusicCommand;
import org.luoxin971.mirai.plugin.command.TopHotCommand;
import org.luoxin971.mirai.plugin.command.WeatherCommand;
import org.luoxin971.mirai.plugin.component.github.GithubConfig;
import org.luoxin971.mirai.plugin.component.github.GithubUtil;
import org.luoxin971.mirai.plugin.component.weather.WeatherForecastConfig;
import org.luoxin971.mirai.plugin.component.weather.WeatherForecastData;
import org.luoxin971.mirai.plugin.eventhandler.GithubMessageEventHandler;

import java.util.Arrays;
import java.util.List;

/**
 * 使用 Java 请把 {@code
 * /src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin} 文件内容改成 {@code
 * org.example.mirai.plugin.JavaPluginMain} <br>
 * 也就是当前主类全类名
 *
 * <p>使用 Java 可以把 kotlin 源集删除且不会对项目有影响
 *
 * <p>在 {@code settings.gradle.kts} 里改构建的插件名称、依赖库和插件版本
 *
 * <p>在该示例下的 {@link JvmPluginDescription} 修改插件名称，id 和版本等
 *
 * <p>可以使用 {@code src/test/kotlin/RunMirai.kt} 在 IDE 里直接调试， 不用复制到 mirai-console-loader 或其他启动器中调试
 */
public final class JavaPluginMain extends JavaPlugin {

  public static final JavaPluginMain INSTANCE = new JavaPluginMain();

  /** 日志 */
  public static final MiraiLogger log = INSTANCE.getLogger();

  private JavaPluginMain() {
    super(
        new JvmPluginDescriptionBuilder("org.luoxin971.personal", "0.1.0")
            .name("自用插件")
            .author("luoxin971")
            .info("自用插件")
            .build());
  }

  @Override
  public void onEnable() {
    getLogger().info("日志");
    getLogger().info("begin");
    ScheduleAction.init();
    EventChannel<Event> eventChannel = GlobalEventChannel.INSTANCE.parentScope(this);
    eventChannel.subscribeAlways(
        FriendMessageEvent.class,
        f -> {
          // 监听好友消息
          handleMessage(f);
        });

    myCustomPermission.getValue(); // 注册权限

    CommandManager.INSTANCE.registerCommand(GithubCommand.INSTANCE, true);
    CommandManager.INSTANCE.registerCommand(MusicCommand.INSTANCE, true);
    CommandManager.INSTANCE.registerCommand(TopHotCommand.INSTANCE, true);
    CommandManager.INSTANCE.registerCommand(WeatherCommand.INSTANCE, true);
    eventChannel.registerListenerHost(new GithubMessageEventHandler());
  }

  public void handleMessage(FriendMessageEvent event) {
    event.getBot().login();
    List<String> list = Arrays.asList("hello", "你好");
    if (list.contains(event.getMessage().contentToString())) {
      event.getBot().getFriend(event.getFriend().getId()).sendMessage("你好呀！");
    }
  }

  @Override
  public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
    // 设置与数据更新
    this.reloadPluginData(WeatherForecastData.INSTANCE);
    this.reloadPluginConfig(WeatherForecastConfig.INSTANCE);
    this.reloadPluginConfig(GithubConfig.INSTANCE);
  }

  @Override
  public void onDisable() {
    JavaPluginMain.INSTANCE.savePluginConfig(WeatherForecastConfig.INSTANCE);
    JavaPluginMain.INSTANCE.savePluginData(WeatherForecastData.INSTANCE);
    JavaPluginMain.INSTANCE.savePluginConfig(GithubConfig.INSTANCE);
    GithubUtil.init();
    log.info("Plugin unloaded");
  }

  // region mirai-console 权限系统示例
  public static final Lazy<Permission> myCustomPermission =
      LazyKt.lazy(
          () -> { // Lazy: Lazy 是必须的, console 不允许提前访问权限系统
            // 注册一条权限节点 org.example.mirai-example:my-permission
            // 并以 org.example.mirai-example:* 为父节点

            // @param: parent: 父权限
            //                 在 Console 内置权限系统中, 如果某人拥有父权限
            //                 那么意味着此人也拥有该权限 (org.example.mirai-example:my-permission)
            // @func: PermissionIdNamespace.permissionId: 根据插件 id 确定一条权限 id
            try {
              return PermissionService.getInstance()
                  .register(
                      INSTANCE.permissionId("my-permission"),
                      "一条自定义权限",
                      INSTANCE.getParentPermission());
            } catch (PermissionRegistryConflictException e) {
              throw new RuntimeException(e);
            }
          });

  public static boolean hasCustomPermission(User usr) {
    PermitteeId pid;
    if (usr instanceof Member) {
      pid = new AbstractPermitteeId.ExactMember(((Member) usr).getGroup().getId(), usr.getId());
    } else {
      pid = new AbstractPermitteeId.ExactUser(usr.getId());
    }
    return PermissionService.hasPermission(pid, myCustomPermission.getValue());
  }
  // endregion
}
