package org.luoxin971.mirai.plugin.component.github

import lombok.AllArgsConstructor
import lombok.Data

/**
 * content
 * @author: xin
 * @since: 2023/2/7
 */
@Data
@AllArgsConstructor
@kotlinx.serialization.Serializable
class MilestoneCode {

    constructor(code: String?, milestoneName: String?, labelColor: String?) {
        this.code = code
        this.milestoneName = milestoneName
        this.labelColor = labelColor
    }

    var code: String? = ""

    var milestoneName: String? = ""

    var labelColor: String? = ""
}