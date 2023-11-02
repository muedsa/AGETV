/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.Whatshot: ImageVector
    get() {
        if (_whatshot != null) {
            return _whatshot!!
        }
        _whatshot = materialIcon(name = "Filled.Whatshot") {
            materialPath {
                moveTo(13.5F, 0.67F)
                reflectiveCurveToRelative(0.74F, 2.65F, 0.74F, 4.8F)
                curveToRelative(0.0F, 2.06F, -1.35F, 3.73F, -3.41F, 3.73F)
                curveToRelative(-2.07F, 0.0F, -3.63F, -1.67F, -3.63F, -3.73F)
                lineToRelative(0.03F, -0.36F)
                curveTo(5.21F, 7.51F, 4.0F, 10.62F, 4.0F, 14.0F)
                curveToRelative(0.0F, 4.42F, 3.58F, 8.0F, 8.0F, 8.0F)
                reflectiveCurveToRelative(8.0F, -3.58F, 8.0F, -8.0F)
                curveTo(20.0F, 8.61F, 17.41F, 3.8F, 13.5F, 0.67F)
                close()
                moveTo(11.71F, 19.0F)
                curveToRelative(-1.78F, 0.0F, -3.22F, -1.4F, -3.22F, -3.14F)
                curveToRelative(0.0F, -1.62F, 1.05F, -2.76F, 2.81F, -3.12F)
                curveToRelative(1.77F, -0.36F, 3.6F, -1.21F, 4.62F, -2.58F)
                curveToRelative(0.39F, 1.29F, 0.59F, 2.65F, 0.59F, 4.04F)
                curveToRelative(0.0F, 2.65F, -2.15F, 4.8F, -4.8F, 4.8F)
                close()
            }
        }
        return _whatshot!!
    }

private var _whatshot: ImageVector? = null