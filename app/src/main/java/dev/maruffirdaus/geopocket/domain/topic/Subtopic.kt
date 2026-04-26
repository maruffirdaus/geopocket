package dev.maruffirdaus.geopocket.domain.topic

import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.Constraint
import dev.maruffirdaus.geopocket.domain.topic.constraint.SegmentConstraint

enum class Subtopic(
    val id: String,
    val topic: Topic,
    val order: Int,
    val title: String,
    val description: String,
    val constraint: Constraint
) {
    LINE_SEGMENT(
        id = "line_segment",
        topic = Topic.LINE,
        order = 0,
        title = "Ruas Garis",
        description = "Ruas garis adalah garis lurus yang memiliki dua titik ujung.",
        constraint = Constraint(
            pointCount = 2,
            segments = mapOf("AB" to SegmentConstraint("AB", 0.4f, 0.4f))
        )
    ),

    ANGLE_ACUTE(
        id = "angle_acute",
        topic = Topic.ANGLE,
        order = 0,
        title = "Sudut Lancip",
        description = "Sudut yang besarnya kurang dari 90°.",
        constraint = Constraint(
            pointCount = 3,
            angles = mapOf("ABC" to AngleConstraint("ABC", 1f, 89f))
        )
    ),
    ANGLE_RIGHT(
        id = "angle_right",
        topic = Topic.ANGLE,
        order = 1,
        title = "Sudut Siku-siku",
        description = "Sudut yang besarnya tepat 90°.",
        constraint = Constraint(
            pointCount = 3,
            angles = mapOf("ABC" to AngleConstraint("ABC", 90f, 90f)),
        )
    ),
    ANGLE_OBTUSE(
        id = "angle_obtuse",
        topic = Topic.ANGLE,
        order = 2,
        title = "Sudut Tumpul",
        description = "Sudut yang besarnya lebih dari 90°.",
        constraint = Constraint(
            pointCount = 3,
            angles = mapOf("ABC" to AngleConstraint("ABC", 91f, 179f))
        )
    ),

    TRIANGLE_EQUILATERAL(
        id = "triangle_equilateral",
        topic = Topic.TRIANGLE,
        order = 0,
        title = "Segitiga Sama Sisi",
        description = "Segitiga dengan tiga sisi yang sama panjang dan tiga sudut yang sama besar.",
        constraint = Constraint(
            pointCount = 3,
            closedShape = true,
            angles = buildMap {
                val pointCount = 3
                repeat(pointCount) { index ->
                    val id = buildString {
                        val a = ('A' + index % pointCount).toString()
                        val b = ('A' + (index + 1) % pointCount).toString()
                        val c = ('A' + (index + 2) % pointCount).toString()
                        append("$a$b$c")
                    }
                    put(id, AngleConstraint(id, 60f, 60f))
                }
            }
        )
    ),
    TRIANGLE_ISOSCELES(
        id = "triangle_isosceles",
        topic = Topic.TRIANGLE,
        order = 1,
        title = "Segitiga Sama Kaki",
        description = "Segitiga dengan dua sisi yang sama panjang dan dua sudut yang sama besar.",
        constraint = Constraint(
            pointCount = 3,
            closedShape = true,
            angles = mapOf(
                "ABC" to AngleConstraint("ABC", 70f, 70f),
                "BCA" to AngleConstraint("BCA", 70f, 70f),
                "CAB" to AngleConstraint("CAB", 40f, 40f)
            )
        )
    ),
    TRIANGLE_SCALENE(
        id = "triangle_scalene",
        topic = Topic.TRIANGLE,
        order = 2,
        title = "Segitiga Sembarang",
        description = "Segitiga dengan panjang sisi dan besar sudut yang berbeda-beda.",
        constraint = Constraint(
            pointCount = 3,
            closedShape = true,
            angles = mapOf(
                "ABC" to AngleConstraint("ABC", 40f, 40f),
                "BCA" to AngleConstraint("BCA", 60f, 60f),
                "CAB" to AngleConstraint("CAB", 80f, 80f)
            )
        )
    ),

    QUADRILATERAL_SQUARE(
        id = "quadrilateral_square",
        topic = Topic.QUADRILATERAL,
        order = 0,
        title = "Persegi",
        description = "Bangun datar dengan empat sisi sama panjang dan semua sudutnya 90°.",
        constraint = Constraint(
            pointCount = 4,
            closedShape = true,
            segments = buildMap {
                val pointCount = 4
                repeat(pointCount) { index ->
                    val id = buildString {
                        append('A' + index)
                        append('A' + (index + 1) % pointCount)
                    }
                    put(id, SegmentConstraint(id, 0.25f, 0.25f))
                }
            },
            angles = buildMap {
                val pointCount = 4
                repeat(4) { index ->
                    val id = buildString {
                        val a = ('A' + index % pointCount).toString()
                        val b = ('A' + (index + 1) % pointCount).toString()
                        val c = ('A' + (index + 2) % pointCount).toString()
                        append("$a$b$c")
                    }
                    put(id, AngleConstraint(id, 90f, 90f))
                }
            }
        )
    ),
    QUADRILATERAL_RECTANGLE(
        id = "quadrilateral_rectangle",
        topic = Topic.QUADRILATERAL,
        order = 1,
        title = "Persegi Panjang",
        description = "Bangun datar dengan empat sudut 90° dan sisi berhadapan sama panjang.",
        constraint = Constraint(
            pointCount = 4,
            closedShape = true,
            segments = buildMap {
                val pointCount = 4
                repeat(pointCount) { index ->
                    val id = buildString {
                        append('A' + index)
                        append('A' + (index + 1) % pointCount)
                    }
                    if (index % 2 == 0) {
                        put(id, SegmentConstraint(id, 0.25f, 0.25f))
                    } else {
                        put(id, SegmentConstraint(id, 0.35f, 0.35f))
                    }
                }
            },
            angles = buildMap {
                val pointCount = 4
                repeat(4) { index ->
                    val id = buildString {
                        val a = ('A' + index % pointCount).toString()
                        val b = ('A' + (index + 1) % pointCount).toString()
                        val c = ('A' + (index + 2) % pointCount).toString()
                        append("$a$b$c")
                    }
                    put(id, AngleConstraint(id, 90f, 90f))
                }
            }
        )
    );

    fun steps(): List<String> {
        val steps = mutableListOf<String>()

        repeat(constraint.pointCount) { index ->
            steps.add("Tempatkan titik ${('A' + index)}")
        }

        constraint.segments.values.forEach { segment ->
            val minLength = segment.minLength?.times(100)?.toInt()
            val maxLength = segment.maxLength?.times(100)?.toInt()
            val lengthHint = when {
                minLength != null && maxLength != null && minLength == maxLength ->
                    "tepat $minLength cm"

                minLength != null && maxLength != null ->
                    "antara ${segment.minLength} cm dan $maxLength cm"

                minLength != null -> "minimal $minLength cm"
                maxLength != null -> "maksimal $maxLength cm"
                else -> ""
            }
            if (lengthHint.isNotEmpty()) steps.add("Pastikan panjang garis ${segment.id} $lengthHint")
        }

        constraint.angles.values.forEach { angle ->
            val minDegree = angle.minDegree?.toInt()
            val maxDegree = angle.maxDegree?.toInt()
            val angleHint = when {
                minDegree != null && maxDegree != null && minDegree == maxDegree ->
                    "tepat $minDegree°"

                minDegree != null && maxDegree != null -> "antara $minDegree° dan $maxDegree°"
                minDegree != null -> "lebih dari $minDegree°"
                maxDegree != null -> "kurang dari $maxDegree°"
                else -> ""
            }
            if (angleHint.isNotEmpty()) steps.add("Pastikan sudut ${angle.id} $angleHint")
        }

        return steps
    }
}