package mx.evolutiondev.template.core.model

/**
 * Enhanced paginated response wrapper for API endpoints
 * 
 * @param T The type of items in the content list
 * @param content The list of items for the current page
 * @param page Current page information and metadata
 */
data class PaginatedResponse<T>(
    val content: List<T>,
    val page: PageInfo
) {
    /**
     * Simple constructor for backwards compatibility
     */
    constructor(
        content: List<T>,
        totalPages: Int,
        pageNumber: Int
    ) : this(
        content = content,
        page = PageInfo(
            number = pageNumber,
            size = content.size,
            totalElements = content.size.toLong(),
            totalPages = totalPages
        )
    )

    /**
     * Full constructor with all pagination details
     */
    constructor(
        content: List<T>,
        pageNumber: Int,
        pageSize: Int,
        totalElements: Long,
        totalPages: Int
    ) : this(
        content = content,
        page = PageInfo(
            number = pageNumber,
            size = pageSize,
            totalElements = totalElements,
            totalPages = totalPages
        )
    )
}

/**
 * Detailed pagination metadata
 * 
 * @param number Current page number (0-indexed or 1-indexed depending on your API design)
 * @param size Number of items in the current page
 * @param totalElements Total number of items across all pages
 * @param totalPages Total number of pages
 */
data class PageInfo(
    val number: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
) {
    val hasNext: Boolean get() = number < totalPages
    val hasPrevious: Boolean get() = number > 1
    val isFirst: Boolean get() = number == 1
    val isLast: Boolean get() = number == totalPages
}

