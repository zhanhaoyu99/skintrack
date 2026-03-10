# 测试计划模板

## 测试对象
- **模块**: [模块名称]
- **类/函数**: [被测目标]
- **测试类型**: 单元测试 / 集成测试

## 测试环境
- **框架**: JUnit 5 + MockK / kotlin.test
- **依赖Mock**: [需要Mock的依赖列表]
- **测试文件**: [测试文件路径]

## 测试用例

### 正常流程
| # | 方法 | 场景 | 输入 | 期望结果 |
|---|------|------|------|---------|
| 1 | | 正常数据 | | |

### 边界条件
| # | 方法 | 场景 | 输入 | 期望结果 |
|---|------|------|------|---------|
| 1 | | 空列表 | | |
| 2 | | 最大值 | | |

### 异常处理
| # | 方法 | 场景 | 输入 | 期望异常 |
|---|------|------|------|---------|
| 1 | | 资源不存在 | | BusinessException |
| 2 | | 权限不足 | | BusinessException |

### 权限校验
| # | 方法 | 场景 | 期望结果 |
|---|------|------|---------|
| 1 | | 操作他人数据 | 拒绝 |
| 2 | | 操作自己数据 | 允许 |

## 测试数据
```kotlin
// 测试辅助函数
fun createTestUser(id: Long = 1L) = User(...)
fun createTestRecord(userId: Long = 1L) = SkinRecord(...)
fun createTestRequest() = CreateSkinRecordRequest(...)
```

## 执行命令
```bash
# 运行特定测试类
./gradlew :server:test --tests "com.skincare.service.SkinRecordServiceTest"

# 运行所有测试
./gradlew :server:test

# KMP共享层测试
./gradlew :shared:allTests
```

## 覆盖率目标
- Service层: ≥80%
- Controller层: 关键路径覆盖
- UseCase层: ≥80%
- Repository层: 自定义查询覆盖
