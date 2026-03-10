<!-- Last updated: 2026-03-10 -->
# 安全检查清单知识库

## JWT认证

### 实现要点
```kotlin
@Component
class JwtTokenProvider(private val jwtProperties: JwtProperties) {

    fun generateToken(userId: Long): String {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtProperties.expirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
            .parseClaimsJws(token).body
        return claims.subject.toLong()
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
```

### JWT安全检查
- [ ] Secret key从环境变量/配置中心获取，不硬编码
- [ ] Token过期时间合理（Access: 1-24h, Refresh: 7-30d）
- [ ] Refresh Token机制实现（避免频繁登录）
- [ ] Token在敏感操作前验证（不仅仅靠Filter）
- [ ] 登出时Token失效（黑名单/Redis）

## 密码安全

### 密码存储
```kotlin
// 使用 BCrypt 哈希
@Service
class AuthService(private val passwordEncoder: PasswordEncoder) {
    fun register(request: RegisterRequest): AuthResponse {
        val hashedPassword = passwordEncoder.encode(request.password)
        val user = User(email = request.email, passwordHash = hashedPassword)
        // ...
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw BusinessException(ErrorCode.INVALID_CREDENTIALS)
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BusinessException(ErrorCode.INVALID_CREDENTIALS)
        }
        // ...
    }
}
```

### 密码安全检查
- [ ] 密码使用 BCrypt/Argon2 哈希存储
- [ ] 永远不存储明文密码
- [ ] 登录失败不提示"密码错误"（统一"凭证无效"）
- [ ] 密码强度要求（长度≥8，含字母+数字）
- [ ] 登录失败限流（防暴力破解）

## API安全

### Spring Security 配置
```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }  // REST API不需要CSRF
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**").permitAll()
                  .requestMatchers("/actuator/health").permitAll()
                  .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
```

### API安全检查
- [ ] 所有API（除auth）需JWT认证
- [ ] 用户只能访问自己的数据（userId校验）
- [ ] 请求参数验证（@Valid + @NotBlank等）
- [ ] 文件上传限制大小和类型
- [ ] SQL注入防护（使用参数化查询/JPA）
- [ ] CORS配置正确（生产环境限制域名）
- [ ] Rate Limiting（防滥用）

## 数据安全

### 敏感数据处理
- [ ] 密码哈希存储
- [ ] API响应不包含密码字段
- [ ] 日志不记录敏感数据（密码、Token）
- [ ] 数据库连接使用SSL（生产环境）
- [ ] 环境变量存储敏感配置（数据库密码、JWT密钥、API密钥）

### 图片隐私
- [ ] 图片URL不可猜测（使用UUID）
- [ ] 图片上传前剥离EXIF元数据（GPS等）
- [ ] 图片存储路径不暴露用户信息
- [ ] 图片访问需要认证（或使用签名URL）

## HTTPS

### 检查项
- [ ] 生产环境强制HTTPS
- [ ] HSTS头配置
- [ ] HTTP自动重定向到HTTPS
- [ ] 证书有效且自动续期

## 输入验证

### 后端验证
```kotlin
data class CreateSkinRecordRequest(
    @field:NotBlank(message = "皮肤类型不能为空")
    @field:Pattern(regexp = "OILY|DRY|COMBINATION_OILY|COMBINATION_DRY|SENSITIVE|NORMAL")
    val skinType: String,

    @field:Size(max = 1000, message = "备注不超过1000字")
    val notes: String? = null,

    @field:Pattern(regexp = "https?://.*", message = "无效的图片URL")
    val imageUrl: String? = null
)
```

### 检查项
- [ ] 所有用户输入后端验证（不信任前端验证）
- [ ] 字符串长度限制
- [ ] 枚举值白名单校验
- [ ] URL/路径参数防注入
- [ ] 文件名消毒（防路径遍历）

## 依赖安全
- [ ] 定期更新依赖（关注安全补丁）
- [ ] 不使用已知有漏洞的版本
- [ ] 最小化依赖（不引入不需要的库）

## 配置安全
```yaml
# application-prod.yml
spring:
  jpa:
    hibernate:
      ddl-auto: none          # 生产禁止自动DDL
    show-sql: false            # 生产不显示SQL
  datasource:
    url: ${DB_URL}             # 从环境变量读取
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

app:
  jwt:
    secret: ${JWT_SECRET}      # 从环境变量读取
```
