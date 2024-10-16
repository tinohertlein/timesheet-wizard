package dev.hertlein.timesheetwizard.shared.model

data class ExportConfig(
    val strategiesByCustomerId: Map<String, List<String>> = emptyMap(),
    val detailsByStrategy: Map<String, Map<String, String>> = emptyMap()
) {

    fun findStrategiesFor(customer: Customer): List<String> = strategiesByCustomerId[customer.id.value].orEmpty()
}