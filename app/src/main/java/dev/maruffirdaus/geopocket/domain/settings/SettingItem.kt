package dev.maruffirdaus.geopocket.domain.settings

sealed class SettingItem(
    val title: String,
    val description: String
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
        title = "Koreksi otomatis",
        description = "Ukuran akan disesuaikan otomatis jika sudah mendekati nilai yang benar",
        default = true
    )

    object SmoothInteraction : Switch(
        title = "Retikel halus",
        description = "Membuat pergerakan retikel lebih mulus, namun dapat membebani perangkat",
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
    }
}