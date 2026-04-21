package dev.maruffirdaus.geopocket.domain.topic

import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.Constraint

enum class Subtopic(
    val id: String,
    val topic: Topic,
    val order: Int,
    val title: String,
    val description: String,
    val constraint: Constraint
) {
    LINE_STRAIGHT(
        id = "line_straight",
        topic = Topic.LINE,
        order = 0,
        title = "Garis Lurus",
        description = "Garis yang menghubungkan dua titik secara langsung tanpa belokan.",
        constraint = Constraint(
            nodeCount = 2
        )
    ),

    ANGLE_ACUTE(
        id = "angle_acute",
        topic = Topic.ANGLE,
        order = 0,
        title = "Sudut Lancip",
        description = "Sudut yang besarnya kurang dari 90 derajat.",
        constraint = Constraint(
            nodeCount = 3
        )
    ),
    ANGLE_RIGHT(
        id = "angle_right",
        topic = Topic.ANGLE,
        order = 1,
        title = "Sudut Siku-siku",
        description = "Sudut yang besarnya tepat 90 derajat.",
        constraint = Constraint(
            nodeCount = 3
        )
    ),
    ANGLE_OBTUSE(
        id = "angle_obtuse",
        topic = Topic.ANGLE,
        order = 2,
        title = "Sudut Tumpul",
        description = "Sudut yang besarnya lebih dari 90 derajat.",
        constraint = Constraint(
            nodeCount = 3
        )
    ),

    TRIANGLE_EQUILATERAL(
        id = "triangle_equilateral",
        topic = Topic.TRIANGLE,
        order = 0,
        title = "Segitiga Sama Sisi",
        description = "Segitiga dengan tiga sisi yang sama panjang dan tiga sudut yang sama besar.",
        constraint = Constraint(
            nodeCount = 3
        )
    ),
    TRIANGLE_ISOSCELES(
        id = "triangle_isosceles",
        topic = Topic.TRIANGLE,
        order = 1,
        title = "Segitiga Sama Kaki",
        description = "Segitiga dengan dua sisi yang sama panjang dan dua sudut yang sama besar.",
        constraint = Constraint(
            nodeCount = 3
        )
    ),
    TRIANGLE_SCALENE(
        id = "triangle_scalene",
        topic = Topic.TRIANGLE,
        order = 2,
        title = "Segitiga Sembarang",
        description = "Segitiga dengan panjang sisi dan besar sudut yang berbeda-beda.",
        constraint = Constraint(
            nodeCount = 3
        )
    ),

    QUADRILATERAL_SQUARE(
        id = "quadrilateral_square",
        topic = Topic.QUADRILATERAL,
        order = 0,
        title = "Persegi",
        description = "Bangun datar dengan empat sisi sama panjang dan semua sudutnya 90 derajat.",
        constraint = Constraint(
            nodeCount = 4,
            angles = List(4) { AngleConstraint(90f) }
        )
    ),
    QUADRILATERAL_RECTANGLE(
        id = "quadrilateral_rectangle",
        topic = Topic.QUADRILATERAL,
        order = 1,
        title = "Persegi Panjang",
        description = "Bangun datar dengan empat sudut 90 derajat dan sisi berhadapan sama panjang.",
        constraint = Constraint(
            nodeCount = 4,
            angles = List(4) { AngleConstraint(90f) }
        )
    )
}