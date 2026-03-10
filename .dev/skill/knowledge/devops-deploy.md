<!-- Last updated: 2026-03-10 -->
# DevOps & 部署知识库

## Android 构建 & 签名

### 签名配置
```kotlin
// composeApp/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 构建命令
```bash
# Debug APK
./gradlew :composeApp:assembleDebug

# Release APK
./gradlew :composeApp:assembleRelease

# AAB (Google Play)
./gradlew :composeApp:bundleRelease
```

## iOS 分发

### KMP iOS构建
```bash
# 构建iOS Framework
./gradlew :shared:linkReleaseFrameworkIosArm64

# 或使用XCFramework
./gradlew :shared:assembleXCFramework
```

### 分发方式
- TestFlight — 测试分发
- App Store — 正式发布
- Ad Hoc — 企业内部
- Firebase App Distribution — 快速测试

## Spring Boot 后端部署

### Docker化
```dockerfile
# server/Dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/server-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### docker-compose
```yaml
version: '3.8'
services:
  app:
    build: ./server
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:postgresql://db:5432/skincare
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - db

  db:
    image: postgres:16-alpine
    environment:
      - POSTGRES_DB=skincare
      - POSTGRES_USER=${DB_USERNAME}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"

volumes:
  pgdata:
```

### 构建命令
```bash
# 构建JAR
./gradlew :server:bootJar

# Docker构建
docker build -t skincare-api ./server

# Docker Compose启动
docker-compose up -d
```

## CI/CD (GitHub Actions)

### 后端CI
```yaml
# .github/workflows/backend-ci.yml
name: Backend CI
on:
  push:
    paths: ['server/**']
  pull_request:
    paths: ['server/**']

jobs:
  build:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: skincare_test
          POSTGRES_PASSWORD: test
        ports: ['5432:5432']
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: ./gradlew :server:test
      - run: ./gradlew :server:bootJar
```

### 客户端CI
```yaml
# .github/workflows/client-ci.yml
name: Client CI
on:
  push:
    paths: ['shared/**', 'composeApp/**']

jobs:
  build:
    runs-on: macos-latest  # iOS需要macOS
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: ./gradlew :shared:allTests
      - run: ./gradlew :composeApp:assembleDebug
```

## 环境管理

### 环境分离
| 环境 | 用途 | 数据库 | 配置文件 |
|------|------|--------|---------|
| dev | 本地开发 | 本地PostgreSQL | application-dev.yml |
| test | 测试 | H2内存数据库 | application-test.yml |
| staging | 预发布 | 独立PostgreSQL | application-staging.yml |
| prod | 生产 | 生产PostgreSQL | application-prod.yml |

### 环境变量
```
DB_URL          — 数据库连接URL
DB_USERNAME     — 数据库用户名
DB_PASSWORD     — 数据库密码
JWT_SECRET      — JWT签名密钥
UPLOAD_PATH     — 文件上传路径
AI_API_KEY      — AI分析服务密钥
```

## 监控 & 日志

### Spring Boot Actuator
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

### 健康检查端点
```
GET /actuator/health — 服务健康状态
GET /actuator/info — 应用信息
```

### 日志配置
```yaml
logging:
  level:
    root: INFO
    com.skincare: DEBUG  # 开发环境
    org.hibernate.SQL: DEBUG  # 开发环境查看SQL
```

## 版本管理
- Android: versionCode (递增整数) + versionName (语义化)
- iOS: CFBundleVersion + CFBundleShortVersionString
- 后端: Gradle version属性
- 统一语义化版本: MAJOR.MINOR.PATCH

## 常见陷阱
1. **签名文件不要提交到Git** — 用 .gitignore + 环境变量
2. **Docker镜像要用slim/alpine** — 减小体积
3. **数据库迁移先于应用部署** — 避免schema不匹配
4. **不要在生产用 ddl-auto: update** — 用Flyway管理
5. **iOS构建需要macOS runner** — GitHub Actions使用 macos-latest
