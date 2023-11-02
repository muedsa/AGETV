package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.Comment: ImageVector
    get() {
        if (_comment != null) {
            return _comment!!
        }
        _comment = materialIcon(name = "Filled.Comment") {
            materialPath {
                moveTo(21.99F, 4.0F)
                curveToRelative(0.0F, -1.1F, -0.89F, -2.0F, -1.99F, -2.0F)
                lineTo(4.0F, 2.0F)
                curveToRelative(-1.1F, 0.0F, -2.0F, 0.9F, -2.0F, 2.0F)
                verticalLineToRelative(12.0F)
                curveToRelative(0.0F, 1.1F, 0.9F, 2.0F, 2.0F, 2.0F)
                horizontalLineToRelative(14.0F)
                lineToRelative(4.0F, 4.0F)
                lineToRelative(-0.01F, -18.0F)
                close()
                moveTo(18.0F, 14.0F)
                lineTo(6.0F, 14.0F)
                verticalLineToRelative(-2.0F)
                horizontalLineToRelative(12.0F)
                verticalLineToRelative(2.0F)
                close()
                moveTo(18.0F, 11.0F)
                lineTo(6.0F, 11.0F)
                lineTo(6.0F, 9.0F)
                horizontalLineToRelative(12.0F)
                verticalLineToRelative(2.0F)
                close()
                moveTo(18.0F, 8.0F)
                lineTo(6.0F, 8.0F)
                lineTo(6.0F, 6.0F)
                horizontalLineToRelative(12.0F)
                verticalLineToRelative(2.0F)
                close()
            }
        }
        return _comment!!
    }

private var _comment: ImageVector? = null