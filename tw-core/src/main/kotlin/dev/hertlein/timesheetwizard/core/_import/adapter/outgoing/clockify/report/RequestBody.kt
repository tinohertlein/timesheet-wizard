package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report


internal data  class RequestBody(
    val clients: ClientsFilter,
    val dateRangeStart: String,
    val dateRangeEnd: String,
    val detailedFilter: DetailedFilter = DetailedFilter()
) {

    fun withIncrementedPageNumber() = this.copy(detailedFilter = detailedFilter.withIncrementedPageNumber())

    fun page() = detailedFilter.page

   internal data  class ClientsFilter(val ids: List<String>) {
        constructor(id: String) : this(listOf(id))
    }

   internal data  class DetailedFilter(val options: Options = Options(), val page: Int = 1, val pageSize: Int = 1000) {

        fun withIncrementedPageNumber() = this.copy(page = page + 1)

       internal data  class Options(val totals: String = "EXCLUDE")
    }
}
