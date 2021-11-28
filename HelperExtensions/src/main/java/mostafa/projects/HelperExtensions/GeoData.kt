package mostafa.projects.HelperExtensions

data class GeoData(
    var cityName: String?,
    var countryName: String?,
    var stateName: String?,
    var countryCode: String?,
) {
    override fun toString(): String {
        var data = "CityName = ${cityName} , CountryName = ${countryName} , StateName = ${stateName} , CountryCode = ${countryCode}"
        return data
    }
}