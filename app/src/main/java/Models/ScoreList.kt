package Models

data class ScoreList private constructor(
    private val score:      Int,
    private val location:   Location,
    private val date:       String
) {
    class Builder(
        var location: Location = Location.Builder().build(),
        var score: Int = 0,
        var date:  String = ""
    ) {
        fun setScore(score: Int) = apply { this.score = score }
        fun setLocation(location: Location) = apply { this.location = location }
        fun setDate(date: String) = apply { this.date = date}
        fun build() = ScoreList(score, location, date)
    }
    fun getScore(): Int{return this.score}
    fun getLocation():Location{return location}
    fun getDate():String{return date}
}
