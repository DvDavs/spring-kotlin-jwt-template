package mx.evolutiondev.template.core.util

import mx.evolutiondev.template.core.model.PageInfo
import mx.evolutiondev.template.core.model.PaginatedResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Utility object for common pagination operations
 */
object PaginationUtils {

    /**
     * Default page size for paginated requests
     */
    const val DEFAULT_PAGE_SIZE = 20

    /**
     * Maximum page size to prevent performance issues
     */
    const val MAX_PAGE_SIZE = 100

    /**
     * Validates and creates a Pageable object
     * 
     * @param page Page number (1-indexed for API, converted to 0-indexed for Spring Data)
     * @param size Number of items per page
     * @param sort Sort configuration
     * @throws IllegalArgumentException if page number is invalid
     */
    fun createPageable(
        page: Int,
        size: Int = DEFAULT_PAGE_SIZE,
        sort: Sort = Sort.by(Sort.Direction.DESC, "createdAt")
    ): Pageable {
        validatePageNumber(page)
        val validatedSize = size.coerceIn(1, MAX_PAGE_SIZE)
        val pageIndex = (page - 1).coerceAtLeast(0)
        return PageRequest.of(pageIndex, validatedSize, sort)
    }

    /**
     * Validates page number
     * 
     * @throws IllegalArgumentException if page number is less than 1
     */
    fun validatePageNumber(page: Int) {
        if (page < 1) {
            throw IllegalArgumentException("Page number must be greater than 0. Received: $page")
        }
    }

    /**
     * Converts Spring Data Page to PaginatedResponse
     * 
     * @param page Spring Data Page object
     * @param pageNumber Original page number (1-indexed)
     * @return PaginatedResponse with proper metadata
     */
    fun <T> toPaginatedResponse(page: Page<T>, pageNumber: Int): PaginatedResponse<T> {
        return PaginatedResponse(
            content = page.content,
            pageNumber = pageNumber,
            pageSize = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages
        )
    }

    /**
     * Converts and maps Spring Data Page to PaginatedResponse with transformation
     * 
     * @param page Spring Data Page object
     * @param pageNumber Original page number (1-indexed)
     * @param transform Transformation function to apply to each element
     * @return PaginatedResponse with transformed content
     */
    fun <T, R> toPaginatedResponse(
        page: Page<T>,
        pageNumber: Int,
        transform: (T) -> R
    ): PaginatedResponse<R> {
        return PaginatedResponse(
            content = page.content.map(transform),
            pageNumber = pageNumber,
            pageSize = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages
        )
    }

    /**
     * Creates an empty PaginatedResponse
     */
    fun <T> emptyResponse(): PaginatedResponse<T> {
        return PaginatedResponse(
            content = emptyList(),
            page = PageInfo(
                number = 1,
                size = 0,
                totalElements = 0,
                totalPages = 0
            )
        )
    }
}

/**
 * Extension function to convert Page to PaginatedResponse easily
 */
fun <T> Page<T>.toPaginatedResponse(pageNumber: Int): PaginatedResponse<T> {
    return PaginationUtils.toPaginatedResponse(this, pageNumber)
}

/**
 * Extension function to convert and map Page to PaginatedResponse with transformation
 */
fun <T, R> Page<T>.toPaginatedResponse(pageNumber: Int, transform: (T) -> R): PaginatedResponse<R> {
    return PaginationUtils.toPaginatedResponse(this, pageNumber, transform)
}

