package dev.maruffirdaus.geopocket.domain.settings

sealed class SettingItem(
    val title: String,
    val description: String,
) {
    sealed class Action(
        title: String,
        description: String
    ) : SettingItem(title, description)

    sealed class Switch(
        title: String,
        description: String,
        val default: Boolean
    ) : SettingItem(title, description)

    object MeasurementAssist : Switch(
        title = "Bantuan pengukuran",
        description = "Memudahkan pengukuran, namun tingkat akurasi dapat berkurang",
        default = true
    )

    object SmoothInteraction : Switch(
        title = "Respons lebih halus",
        description = "Mengutamakan kelancaran interaksi, namun meningkatkan beban sistem",
        default = false
    )

    object ResetProgress : Action(
        title = "Reset progres",
        description = "Semua progres akan dihapus dan tidak dapat dikembalikan",
    )

    object OpenSourceLicenses : Action(
        title = "Lisensi open source",
        description = "Lihat lisensi pustaka pihak ketiga"
    )

    enum class Group(val title: String) {
        AR("AR"),
        DATA("Data"),
        ABOUT("Tentang")
    }

    companion object {
        val entriesByGroup = mapOf(
            Group.AR to listOf(MeasurementAssist, SmoothInteraction),
            Group.DATA to listOf(ResetProgress),
            Group.ABOUT to listOf(OpenSourceLicenses)
        )
        val entries = entriesByGroup.flatMap { it.value }
        val saveableBoolean = entries.filterIsInstance<Switch>()
    }
}