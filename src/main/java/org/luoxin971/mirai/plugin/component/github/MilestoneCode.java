package org.luoxin971.mirai.plugin.component.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * content
 *
 * @author luoxin
 * @since 2023/2/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneCode {

  String code;

  String milestoneName;

  String labelColor;
}
