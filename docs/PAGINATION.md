# Pagination Guide

Este documento explica el sistema de paginación de la plantilla, cómo implementarlo y las mejores prácticas.

## 📋 Table of Contents

- [Overview](#overview)
- [Pagination Response Format](#pagination-response-format)
- [Quick Start](#quick-start)
- [Implementation Guide](#implementation-guide)
- [Pagination Utils](#pagination-utils)
- [Advanced Examples](#advanced-examples)
- [Best Practices](#best-practices)
- [Frontend Integration](#frontend-integration)

## Overview

La plantilla incluye un sistema completo de paginación que:

✅ **Formato consistente** para todas las respuestas paginadas  
✅ **Metadatos completos** (hasNext, hasPrevious, totalPages, etc.)  
✅ **Utilities helper** para simplificar la implementación  
✅ **Integración con Spring Data** JPA  
✅ **Fácil de usar** con extension functions  
✅ **Type-safe** con genéricos de Kotlin  

## Pagination Response Format

### Response Structure

```json
{
    "content": [
        {
            "id": 1,
            "name": "Item 1",
            "...": "..."
        },
        {
            "id": 2,
            "name": "Item 2",
            "...": "..."
        }
    ],
    "page": {
        "number": 1,
        "size": 20,
        "totalElements": 150,
        "totalPages": 8,
        "hasNext": true,
        "hasPrevious": false,
        "isFirst": true,
        "isLast": false
    }
}
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `content` | Array | Lista de items de la página actual |
| `page.number` | Int | Número de página actual (1-indexed para API) |
| `page.size` | Int | Número de items en esta página |
| `page.totalElements` | Long | Total de elementos en todas las páginas |
| `page.totalPages` | Int | Número total de páginas |
| `page.hasNext` | Boolean | ¿Existe página siguiente? |
| `page.hasPrevious` | Boolean | ¿Existe página anterior? |
| `page.isFirst` | Boolean | ¿Es la primera página? |
| `page.isLast` | Boolean | ¿Es la última página? |

## Quick Start

### 1. Repository

```kotlin
interface UserRepository : JpaRepository<UserEntity, Long> {
    // Spring Data ya soporta paginación en findAll()
}
```

### 2. Service

```kotlin
@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    fun getAllUsers(page: Int): PaginatedResponse<User> {
        // Crear Pageable
        val pageable = PaginationUtils.createPageable(page)
        
        // Obtener datos
        val result = userRepository.findAll(pageable)
        
        // Convertir a PaginatedResponse
        return result.toPaginatedResponse(page) { entity ->
            entity.toDTO()
        }
    }
}
```

### 3. Controller

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    
    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<PaginatedResponse<User>> {
        val response = userService.getAllUsers(page)
        return ResponseEntity.ok(response)
    }
}
```

### 4. Test

```bash
# Get first page
curl http://localhost:6003/api/users?page=1

# Get second page  
curl http://localhost:6003/api/users?page=2
```

## Implementation Guide

### Step 1: Create Your Entity

```kotlin
@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false)
    val price: Double,
    
    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)
```

### Step 2: Create Repository

```kotlin
interface ProductRepository : JpaRepository<ProductEntity, Long> {
    
    // Método con paginación personalizado
    fun findByNameContaining(name: String, pageable: Pageable): Page<ProductEntity>
    
    // Con filtros múltiples
    fun findByPriceBetween(
        minPrice: Double,
        maxPrice: Double,
        pageable: Pageable
    ): Page<ProductEntity>
}
```

### Step 3: Create DTO

```kotlin
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Double,
    val createdAt: String
)

// Extension para conversión
fun ProductEntity.toDTO(): ProductResponse {
    return ProductResponse(
        id = id,
        name = name,
        price = price,
        createdAt = createdAt.toString()
    )
}
```

### Step 4: Implement Service

```kotlin
@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    
    /**
     * Get all products with pagination
     */
    fun getAllProducts(
        page: Int,
        size: Int = 20
    ): PaginatedResponse<ProductResponse> {
        val pageable = PaginationUtils.createPageable(
            page = page,
            size = size,
            sort = Sort.by(Sort.Direction.DESC, "createdAt")
        )
        
        val result = productRepository.findAll(pageable)
        
        return result.toPaginatedResponse(page) { it.toDTO() }
    }
    
    /**
     * Search products by name with pagination
     */
    fun searchProducts(
        name: String,
        page: Int
    ): PaginatedResponse<ProductResponse> {
        val pageable = PaginationUtils.createPageable(page)
        
        val result = productRepository.findByNameContaining(name, pageable)
        
        return result.toPaginatedResponse(page) { it.toDTO() }
    }
    
    /**
     * Filter products by price range with pagination
     */
    fun filterByPrice(
        minPrice: Double,
        maxPrice: Double,
        page: Int
    ): PaginatedResponse<ProductResponse> {
        val pageable = PaginationUtils.createPageable(page)
        
        val result = productRepository.findByPriceBetween(
            minPrice, maxPrice, pageable
        )
        
        return result.toPaginatedResponse(page) { it.toDTO() }
    }
}
```

### Step 5: Create Controller

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    
    @GetMapping
    fun getAllProducts(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PaginatedResponse<ProductResponse>> {
        val response = productService.getAllProducts(page, size)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam name: String,
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<PaginatedResponse<ProductResponse>> {
        val response = productService.searchProducts(name, page)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/filter")
    fun filterByPrice(
        @RequestParam minPrice: Double,
        @RequestParam maxPrice: Double,
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<PaginatedResponse<ProductResponse>> {
        val response = productService.filterByPrice(minPrice, maxPrice, page)
        return ResponseEntity.ok(response)
    }
}
```

## Pagination Utils

### Available Methods

```kotlin
// Crear Pageable con valores por defecto
val pageable = PaginationUtils.createPageable(page = 1)

// Crear Pageable con tamaño personalizado
val pageable = PaginationUtils.createPageable(page = 1, size = 50)

// Crear Pageable con ordenamiento personalizado
val pageable = PaginationUtils.createPageable(
    page = 1,
    size = 20,
    sort = Sort.by(Sort.Direction.ASC, "name")
)

// Ordenamiento múltiple
val sort = Sort.by("name").ascending().and(Sort.by("price").descending())
val pageable = PaginationUtils.createPageable(page = 1, sort = sort)

// Validar número de página
PaginationUtils.validatePageNumber(page)  // Throws IllegalArgumentException si < 1

// Crear respuesta vacía
val empty = PaginationUtils.emptyResponse<Product>()
```

### Extension Functions

```kotlin
// Convertir Page a PaginatedResponse directamente
val result: Page<ProductEntity> = productRepository.findAll(pageable)
val response: PaginatedResponse<ProductEntity> = result.toPaginatedResponse(page)

// Con transformación
val response: PaginatedResponse<ProductDTO> = result.toPaginatedResponse(page) { entity ->
    entity.toDTO()
}
```

### Constants

```kotlin
PaginationUtils.DEFAULT_PAGE_SIZE  // 20
PaginationUtils.MAX_PAGE_SIZE      // 100
```

## Advanced Examples

### Example 1: Complex Filtering with Specifications

```kotlin
@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    fun getUsersWithFilters(
        role: Role?,
        isEnabled: Boolean?,
        createdAfter: Instant?,
        page: Int
    ): PaginatedResponse<UserResponse> {
        val pageable = PaginationUtils.createPageable(page)
        
        // Usar Spring Data Specifications
        var spec = Specification.where<UserEntity>(null)
        
        role?.let {
            spec = spec.and { root, _, cb ->
                cb.equal(root.get<Role>("role"), it)
            }
        }
        
        isEnabled?.let {
            spec = spec.and { root, _, cb ->
                cb.equal(root.get<Boolean>("isEnabled"), it)
            }
        }
        
        createdAfter?.let {
            spec = spec.and { root, _, cb ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), it)
            }
        }
        
        val result = userRepository.findAll(spec, pageable)
        
        return result.toPaginatedResponse(page) { it.toResponse() }
    }
}
```

### Example 2: Custom Sort Fields

```kotlin
@GetMapping
fun getAllProducts(
    @RequestParam(defaultValue = "1") page: Int,
    @RequestParam(defaultValue = "20") size: Int,
    @RequestParam(defaultValue = "createdAt") sortBy: String,
    @RequestParam(defaultValue = "DESC") sortDirection: String
): ResponseEntity<PaginatedResponse<ProductResponse>> {
    
    val direction = if (sortDirection.uppercase() == "ASC") {
        Sort.Direction.ASC
    } else {
        Sort.Direction.DESC
    }
    
    val pageable = PaginationUtils.createPageable(
        page = page,
        size = size,
        sort = Sort.by(direction, sortBy)
    )
    
    val result = productRepository.findAll(pageable)
    val response = result.toPaginatedResponse(page) { it.toDTO() }
    
    return ResponseEntity.ok(response)
}
```

### Example 3: Search with Pagination

```kotlin
@Service
class SearchService(
    private val productRepository: ProductRepository
) {
    
    fun searchProducts(
        query: String,
        category: String?,
        minPrice: Double?,
        maxPrice: Double?,
        page: Int
    ): PaginatedResponse<ProductResponse> {
        val pageable = PaginationUtils.createPageable(page)
        
        val spec = Specification.where<ProductEntity> { root, criteriaQuery, cb ->
            val predicates = mutableListOf<Predicate>()
            
            // Search in name or description
            if (query.isNotBlank()) {
                val searchPattern = "%${query.lowercase()}%"
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get("name")), searchPattern),
                        cb.like(cb.lower(root.get("description")), searchPattern)
                    )
                )
            }
            
            // Filter by category
            category?.let {
                predicates.add(cb.equal(root.get<String>("category"), it))
            }
            
            // Filter by price range
            minPrice?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), it))
            }
            maxPrice?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), it))
            }
            
            cb.and(*predicates.toTypedArray())
        }
        
        val result = productRepository.findAll(spec, pageable)
        
        return result.toPaginatedResponse(page) { it.toDTO() }
    }
}
```

### Example 4: Multiple Entities with Join

```kotlin
@Service
class OrderService(
    private val orderRepository: OrderRepository
) {
    
    fun getOrdersWithDetails(
        userId: Long,
        page: Int
    ): PaginatedResponse<OrderWithDetailsResponse> {
        val pageable = PaginationUtils.createPageable(
            page = page,
            sort = Sort.by(Sort.Direction.DESC, "createdAt")
        )
        
        val result = orderRepository.findAllByUserIdWithProducts(userId, pageable)
        
        return result.toPaginatedResponse(page) { order ->
            OrderWithDetailsResponse(
                id = order.id,
                total = order.total,
                status = order.status,
                products = order.products.map { it.toDTO() },
                createdAt = order.createdAt.toString()
            )
        }
    }
}
```

## Best Practices

### ✅ DO

1. **Usa 1-indexed para la API** (más intuitivo para usuarios):
   ```kotlin
   // La API usa page=1, page=2, etc.
   // PaginationUtils convierte internamente a 0-indexed para Spring Data
   val pageable = PaginationUtils.createPageable(page = 1) // Se convierte a 0 internamente
   ```

2. **Proporciona defaults sensatos**:
   ```kotlin
   @GetMapping
   fun getItems(
       @RequestParam(defaultValue = "1") page: Int,
       @RequestParam(defaultValue = "20") size: Int
   )
   ```

3. **Valida los parámetros**:
   ```kotlin
   // PaginationUtils.createPageable ya valida el page number
   // Para el size, puedes agregar:
   if (size < 1 || size > 100) {
       throw IllegalArgumentException("Size must be between 1 and 100")
   }
   ```

4. **Transforma en el service, no en el controller**:
   ```kotlin
   // ✅ BIEN
   fun getUsers(page: Int): PaginatedResponse<UserDTO> {
       return repository.findAll(pageable).toPaginatedResponse(page) { it.toDTO() }
   }
   
   // ❌ MAL - transformación en controller
   ```

5. **Usa extension functions** para conversión limpia:
   ```kotlin
   fun UserEntity.toDTO() = UserDTO(...)
   
   // Luego
   result.toPaginatedResponse(page) { it.toDTO() }
   ```

### ❌ DON'T

1. **No retornes List en lugar de PaginatedResponse**:
   ```kotlin
   // ❌ MAL
   @GetMapping
   fun getUsers(): List<User>
   
   // ✅ BIEN
   @GetMapping
   fun getUsers(@RequestParam page: Int): PaginatedResponse<User>
   ```

2. **No ignores el page parameter**:
   ```kotlin
   // ❌ MAL
   @GetMapping
   fun getUsers(): PaginatedResponse<User> {
       return userRepository.findAll() // Sin paginación!
   }
   ```

3. **No uses page size muy grandes**:
   ```kotlin
   // ❌ MAL - puede causar problemas de performance
   val pageable = PageRequest.of(0, 10000)
   
   // ✅ BIEN - usa límites razonables
   val pageable = PaginationUtils.createPageable(page, size.coerceIn(1, 100))
   ```

## Frontend Integration

### React Example

```typescript
interface PaginatedResponse<T> {
  content: T[];
  page: {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
    isFirst: boolean;
    isLast: boolean;
  };
}

const UsersPage = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [data, setData] = useState<PaginatedResponse<User>>();

  useEffect(() => {
    fetch(`/api/users?page=${currentPage}`)
      .then(res => res.json())
      .then(setData);
  }, [currentPage]);

  return (
    <div>
      {data?.content.map(user => (
        <UserCard key={user.id} user={user} />
      ))}
      
      <Pagination
        currentPage={data?.page.number ?? 1}
        totalPages={data?.page.totalPages ?? 1}
        onPageChange={setCurrentPage}
        hasNext={data?.page.hasNext}
        hasPrevious={data?.page.hasPrevious}
      />
    </div>
  );
};
```

### Vue Example

```vue
<template>
  <div>
    <div v-for="item in items" :key="item.id">
      {{ item.name }}
    </div>
    
    <button @click="previousPage" :disabled="!pagination.hasPrevious">
      Previous
    </button>
    
    <span>Page {{ pagination.number }} of {{ pagination.totalPages }}</span>
    
    <button @click="nextPage" :disabled="!pagination.hasNext">
      Next
    </button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      items: [],
      pagination: {}
    };
  },
  methods: {
    async loadData(page = 1) {
      const response = await fetch(`/api/items?page=${page}`);
      const data = await response.json();
      this.items = data.content;
      this.pagination = data.page;
    },
    nextPage() {
      if (this.pagination.hasNext) {
        this.loadData(this.pagination.number + 1);
      }
    },
    previousPage() {
      if (this.pagination.hasPrevious) {
        this.loadData(this.pagination.number - 1);
      }
    }
  },
  mounted() {
    this.loadData();
  }
};
</script>
```

### Angular Example

```typescript
@Component({
  selector: 'app-products',
  template: `
    <div *ngFor="let product of products">
      {{ product.name }}
    </div>
    
    <app-pagination
      [currentPage]="currentPage"
      [totalPages]="totalPages"
      [hasNext]="hasNext"
      [hasPrevious]="hasPrevious"
      (pageChange)="loadPage($event)"
    ></app-pagination>
  `
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  currentPage = 1;
  totalPages = 1;
  hasNext = false;
  hasPrevious = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadPage(1);
  }

  loadPage(page: number) {
    this.http.get<PaginatedResponse<Product>>(`/api/products?page=${page}`)
      .subscribe(response => {
        this.products = response.content;
        this.currentPage = response.page.number;
        this.totalPages = response.page.totalPages;
        this.hasNext = response.page.hasNext;
        this.hasPrevious = response.page.hasPrevious;
      });
  }
}
```

## Testing Pagination

```kotlin
@WebMvcTest(ProductController::class)
class ProductControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var productService: ProductService
    
    @Test
    fun `should return paginated products`() {
        val products = listOf(
            ProductResponse(1, "Product 1", 10.0, "2024-01-01"),
            ProductResponse(2, "Product 2", 20.0, "2024-01-02")
        )
        
        val response = PaginatedResponse(
            content = products,
            pageNumber = 1,
            pageSize = 20,
            totalElements = 50,
            totalPages = 3
        )
        
        given(productService.getAllProducts(1, 20)).willReturn(response)
        
        mockMvc.perform(get("/api/products?page=1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.page.number").value(1))
            .andExpect(jsonPath("$.page.totalPages").value(3))
            .andExpect(jsonPath("$.page.hasNext").value(true))
            .andExpect(jsonPath("$.page.hasPrevious").value(false))
            .andExpect(jsonPath("$.page.isFirst").value(true))
            .andExpect(jsonPath("$.page.isLast").value(false))
    }
}
```

---

## Summary

El sistema de paginación proporciona:

✅ Formato consistente y rico en metadatos  
✅ Utilities para simplificar implementación  
✅ Extension functions para código limpio  
✅ Integración perfecta con Spring Data  
✅ Type-safe con genéricos  
✅ Fácil de usar en frontend  

**Next:** Ver [ERROR_HANDLING.md](ERROR_HANDLING.md) para manejo de errores.

