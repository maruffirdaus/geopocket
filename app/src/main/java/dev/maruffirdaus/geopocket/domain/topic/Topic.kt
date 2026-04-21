package dev.maruffirdaus.geopocket.domain.topic

enum class Topic(
    val id: String,
    val order: Int,
    val title: String,
    val description: String
) {
    LINE(
        id = "line",
        order = 0,
        title = "Konsep Garis",
        description = "Dasar bentuk geometri yang terbentuk dari kumpulan titik yang tersusun lurus."
    ),
    ANGLE(
        id = "angle",
        order = 1,
        title = "Konsep Sudut",
        description = "Bentuk yang terjadi ketika dua garis bertemu pada satu titik."
    ),
    TRIANGLE(
        id = "triangle",
        order = 2,
        title = "Konsep Segitiga",
        description = "Bangun datar dengan tiga sisi dan tiga sudut."
    ),
    QUADRILATERAL(
        id = "quadrilateral",
        order = 3,
        title = "Konsep Segiempat",
        description = "Bangun datar yang memiliki empat sisi dan empat sudut."
    )
}