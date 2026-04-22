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
        title = "Garis",
        description = "Garis adalah bentuk dasar geometri yang memanjang lurus tanpa batas."
    ),
    ANGLE(
        id = "angle",
        order = 1,
        title = "Sudut",
        description = "Bentuk yang terjadi ketika dua garis bertemu pada satu titik."
    ),
    TRIANGLE(
        id = "triangle",
        order = 2,
        title = "Segitiga",
        description = "Bangun datar dengan tiga sisi dan tiga sudut."
    ),
    QUADRILATERAL(
        id = "quadrilateral",
        order = 3,
        title = "Segi Empat",
        description = "Bangun datar yang memiliki empat sisi dan empat sudut."
    )
}