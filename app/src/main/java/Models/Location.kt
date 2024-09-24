package Models

data class Location(
    var address: String,
    val latitude: Double,
    val longitude: Double
) {
    class Builder(
        var address: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
    ) {
        fun address(address: String) = apply { this.address = address }
        fun coordinates(latitude: Double, longitude: Double) = apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        fun build() = Location(address, latitude, longitude)
    }
}