/**
 * Mirai Song Plugin Copyright (C) 2021 khjxiaogu
 *
 * <p>This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.example.mirai.plugin.component.music.commom;

import net.mamoe.mirai.message.data.Message;

/**
 * 生成音乐卡片的接口
 *
 * @author khjxiaogu file: MusicCardProvider.java time: 2020年8月26日
 */
@FunctionalInterface
public interface MusicCardProvider {

  /**
   * 处理音乐信息并生成卡片.<br>
   *
   * @param mi 音乐信息
   * @return 返回要回传的音乐卡片信息
   * @throws Exception 发生任意异常
   */
  public Message process(MusicInfo mi) throws Exception;
}
