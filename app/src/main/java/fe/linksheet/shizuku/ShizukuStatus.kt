package fe.linksheet.shizuku

enum class ShizukuStatus {
    Enabled, NotRunning, NoPermission, NotInstalled;

    companion object {
        fun findStatus(installed: Boolean, running: Boolean, permission: Boolean): ShizukuStatus {
            if (installed && running && permission) return Enabled
            if (!installed) return NotInstalled
            if (!running) return NotRunning

            return NoPermission
        }
    }
}

