<!-- Last updated: 2026-03-10 -->
# 工作流：写测试

## 触发命令
`/app test [target]` — 如 `/app test skin-record-service`, `/app test auth-api`, `/app test record-viewmodel`

## 执行流程

### 第1步：确定测试范围
根据target确定测试类型和范围：

| Target格式 | 测试类型 | 位置 |
|-----------|---------|------|
| `xxx-service` | Service单元测试 | server/src/test/ |
| `xxx-api` | Controller集成测试 | server/src/test/ |
| `xxx-repository` | Repository测试 | server/src/test/ |
| `xxx-viewmodel` | ViewModel单元测试 | composeApp/src/commonTest/ |
| `xxx-usecase` | UseCase单元测试 | shared/src/commonTest/ |

### 第2步：加载测试标准
- `knowledge/testing-strategy.md` — 测试策略
- `templates/test-plan.md` — 测试计划模板
- 目标模块的源代码

### 第3步：设计测试用例
```
测试对象: [类名]
测试类型: [单元/集成]

测试用例:
1. [方法名]_[场景]_[期望结果]
   输入: ...
   期望: ...

2. [正常流程]
3. [边界条件]
4. [异常处理]
5. [权限校验]
```

### 第4步：编写测试代码

**后端Service测试（JUnit 5 + MockK）**
```kotlin
@ExtendWith(MockKExtension::class)
class SkinRecordServiceTest {
    @MockK private lateinit var recordRepository: SkinRecordRepository
    @MockK private lateinit var userRepository: UserRepository
    @InjectMockKs private lateinit var service: SkinRecordService

    @Test
    fun `getRecords should return paged records for user`() {
        // Given
        val userId = 1L
        val records = listOf(createTestRecord())
        every { recordRepository.findByUserId(userId, any()) } returns PageImpl(records)

        // When
        val result = service.getRecords(userId, 0, 20)

        // Then
        assertThat(result.content).hasSize(1)
        verify { recordRepository.findByUserId(userId, any()) }
    }

    @Test
    fun `createRecord should throw when user not found`() {
        // Given
        every { userRepository.findByIdOrNull(any()) } returns null

        // When & Then
        assertThrows<BusinessException> {
            service.createRecord(999L, createRequest())
        }
    }
}
```

**后端Controller测试（@WebMvcTest）**
```kotlin
@WebMvcTest(SkinRecordController::class)
class SkinRecordControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc
    @MockkBean private lateinit var service: SkinRecordService

    @Test
    fun `GET records should return 200 with records`() {
        every { service.getRecords(any(), any(), any()) } returns pagedResult

        mockMvc.get("/api/v1/skin-records") {
            header("Authorization", "Bearer $testToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
        }
    }
}
```

**KMP共享层测试**
```kotlin
class GetSkinRecordsUseCaseTest {
    private val repository = mockk<SkinRecordRepository>()
    private val useCase = GetSkinRecordsUseCase(repository)

    @Test
    fun `invoke should return records from repository`() = runTest {
        coEvery { repository.getRecords(any(), any()) } returns Result.success(testRecords)

        val result = useCase(page = 0, size = 20)

        assertTrue(result.isSuccess)
        assertEquals(testRecords, result.getOrNull())
    }
}
```

### 第5步：运行测试
1. 运行新写的测试确保通过
2. 运行相关模块的全部测试确保无回归

### 第6步：更新记忆
- `app-progress.md` — 记录测试覆盖情况（按需）

---

## 测试命名规范
```
方法名_场景_期望结果

示例:
getRecords_validUser_returnPagedRecords
createRecord_userNotFound_throwBusinessException
deleteRecord_otherUserRecord_throwForbidden
```

---

## 输出格式

```
🧪 测试: [target]

━━━ 测试用例 ━━━
| # | 方法 | 场景 | 期望 | 状态 |
|---|------|------|------|------|
| 1 | getRecords | 正常 | 返回分页数据 | ✅ |
| 2 | createRecord | 用户不存在 | 抛BusinessException | ✅ |

━━━ 创建的文件 ━━━
{test file path}

━━━ 运行结果 ━━━
[X] 个测试全部通过 / [Y] 个失败

下一步建议：
→ `/app test [next-target]` 测试下一个模块
→ `/app review [module]` 审查代码质量
```
