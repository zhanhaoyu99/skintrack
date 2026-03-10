<!-- Last updated: 2026-03-10 -->
# 测试策略知识库

## 测试金字塔

```
          ╱  E2E  ╲         少量：关键流程端到端
         ╱─────────╲
        ╱ 集成测试   ╲       中量：API + 数据库
       ╱─────────────╲
      ╱   单元测试     ╲     大量：Service/UseCase/ViewModel
     ╱─────────────────╲
```

## 后端测试 (Spring Boot)

### 单元测试 — Service层
```kotlin
// 框架: JUnit 5 + MockK
// 位置: server/src/test/kotlin/{package}/service/

@ExtendWith(MockKExtension::class)
class SkinRecordServiceTest {
    @MockK private lateinit var recordRepo: SkinRecordRepository
    @MockK private lateinit var userRepo: UserRepository
    @InjectMockKs private lateinit var service: SkinRecordService

    @Test
    fun `createRecord should save and return response`() {
        // Given
        val user = createTestUser()
        val request = CreateSkinRecordRequest(skinType = "OILY", notes = "test")
        every { userRepo.findByIdOrNull(1L) } returns user
        every { recordRepo.save(any()) } answers { firstArg() }

        // When
        val result = service.createRecord(1L, request)

        // Then
        assertThat(result.skinType).isEqualTo("OILY")
        verify { recordRepo.save(any()) }
    }
}
```

**覆盖重点：**
- 正常流程
- 资源不存在（→ BusinessException）
- 权限校验（用户A不能操作用户B的数据）
- 边界条件（空列表、最大值）

### 集成测试 — Controller层
```kotlin
// 框架: @WebMvcTest 或 @SpringBootTest
// 位置: server/src/test/kotlin/{package}/controller/

@WebMvcTest(SkinRecordController::class)
@AutoConfigureMockMvc(addFilters = false) // 跳过JWT过滤器（如需）
class SkinRecordControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc
    @MockkBean private lateinit var service: SkinRecordService

    @Test
    fun `POST skin-records should return 201`() {
        val request = """{"skinType":"OILY","notes":"test"}"""
        every { service.createRecord(any(), any()) } returns testResponse

        mockMvc.post("/api/v1/skin-records") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isCreated() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.skinType") { value("OILY") }
        }
    }

    @Test
    fun `POST with invalid request should return 400`() {
        val request = """{"skinType":""}"""

        mockMvc.post("/api/v1/skin-records") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
```

### Repository测试
```kotlin
// 框架: @DataJpaTest + 内嵌数据库
@DataJpaTest
class SkinRecordRepositoryTest {
    @Autowired private lateinit var repository: SkinRecordRepository
    @Autowired private lateinit var entityManager: TestEntityManager

    @Test
    fun `findByUserId should return records for specific user`() {
        val user = entityManager.persist(User(email = "test@test.com", ...))
        entityManager.persist(SkinRecord(user = user, skinType = SkinType.OILY))

        val records = repository.findByUserId(user.id, PageRequest.of(0, 10))

        assertThat(records.content).hasSize(1)
    }
}
```

## KMP共享层测试

### UseCase测试
```kotlin
// 位置: shared/src/commonTest/kotlin/{package}/domain/usecase/
// 框架: kotlin.test + kotlinx-coroutines-test

class GetSkinRecordsUseCaseTest {
    private val repository = FakeSkinRecordRepository()
    private val useCase = GetSkinRecordsUseCase(repository)

    @Test
    fun invoke_withValidPage_returnsRecords() = runTest {
        repository.setRecords(listOf(testRecord1, testRecord2))

        val result = useCase(page = 0, size = 20)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailure() = runTest {
        repository.setShouldFail(true)

        val result = useCase(page = 0, size = 20)

        assertTrue(result.isFailure)
    }
}
```

### Fake实现（替代Mock）
```kotlin
// KMP中使用Fake比Mock更方便（跨平台兼容）
class FakeSkinRecordRepository : SkinRecordRepository {
    private var records: List<SkinRecord> = emptyList()
    private var shouldFail = false

    fun setRecords(records: List<SkinRecord>) { this.records = records }
    fun setShouldFail(fail: Boolean) { shouldFail = fail }

    override suspend fun getRecords(page: Int, size: Int): Result<List<SkinRecord>> {
        if (shouldFail) return Result.failure(Exception("Fake error"))
        return Result.success(records)
    }
}
```

## CMP UI测试（按需）

### ViewModel测试
```kotlin
// 位置: composeApp/src/commonTest/ 或 composeApp/src/androidUnitTest/

class SkinRecordViewModelTest {
    private val fakeUseCase = FakeGetSkinRecordsUseCase()
    private val viewModel = SkinRecordViewModel(fakeUseCase)

    @Test
    fun `init should load records and update uiState`() = runTest {
        fakeUseCase.setResult(Result.success(testRecords))
        viewModel.init()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(testRecords, state.records)
    }
}
```

## 测试工具和依赖

### 后端
```kotlin
// build.gradle.kts
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("io.mockk:mockk:1.13.x")
testImplementation("com.ninja-squad:springmockk:4.x")
```

### KMP共享
```kotlin
// build.gradle.kts
commonTest {
    dependencies {
        implementation(kotlin("test"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.x")
    }
}
```

## 测试命名规范
```
方法名_场景_期望结果 (下划线分隔)
或
`方法名 should 期望结果 when 场景` (反引号+自然语言)

示例:
createRecord_validRequest_returnsSavedRecord
`createRecord should return saved record when request is valid`
```

## MVP测试优先级
1. **必须测试**: Service层核心业务逻辑
2. **应该测试**: Controller层API正确性
3. **可以测试**: UseCase层
4. **低优先级**: UI测试、E2E测试

## 常见陷阱
1. **MockK在KMP中不可用** — 共享层用Fake实现
2. **runTest 必须用 kotlinx-coroutines-test** — 不要用 `runBlocking`
3. **@WebMvcTest 不加载完整上下文** — 需要 `@MockkBean` 所有依赖
4. **JPA测试需要内嵌数据库** — 使用H2（注意与PostgreSQL差异）
5. **测试数据隔离** — 每个测试方法独立，不依赖执行顺序
