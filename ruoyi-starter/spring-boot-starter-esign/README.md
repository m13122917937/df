# ruoyi-esign-starter

e签宝开放平台认证授权Spring Boot Starter，提供个人认证和机构认证功能。

## 配置说明

在application.yml中添加配置：

```yaml
esign:
  # 开放平台地址，默认：https://open.esign.cn
  host: https://open.esign.cn
  # 开发者应用ID
  app-id: your-app-id
  # 应用密钥
  app-secret: your-app-secret
  # 默认回调地址
  redirect-uri: https://your-domain.com/callback
  # 连接超时时间（毫秒），默认10000
  connect-timeout: 10000
  # 读取超时时间（毫秒），默认30000
  read-timeout: 30000
```

## 使用示例

### 1. 个人认证流程

```java
@RestController
@RequestMapping("/esign")
public class EsignAuthController {

    @Autowired
    private EsignAuthApi esignAuthApi;

    /**
     * 获取个人认证URL
     */
    @GetMapping("/personal/auth-url")
    public Result<String> getPersonalAuthUrl() {
        PersonalAuthRequest request = new PersonalAuthRequest();
        request.setThirdPartyUserId("user123456");
        request.setName("张三");
        request.setIdCard("110101199001011234");
        request.setMobile("13800138000");
        request.setFaceRecognition(true);
        
        String authUrl = esignAuthApi.buildPersonalAuthUrl(request);
        return Result.success(authUrl);
    }

    /**
     * 个人认证回调
     */
    @PostMapping("/callback/personal")
    public Result<PersonalAuthInfo> personalCallback(@RequestBody String callbackJson) {
        AuthCallbackResult callbackResult = esignAuthApi.parseCallbackResult(callbackJson);
        AuthInfoResponse authInfo = esignAuthApi.getAuthInfo(callbackResult.getAuthFlowId());
        return Result.success(authInfo.getPersonalAuthInfo());
    }
}
```

### 2. 机构认证流程

```java
@RestController
@RequestMapping("/esign")
public class EsignOrgAuthController {

    @Autowired
    private EsignAuthApi esignAuthApi;

    /**
     * 获取机构认证URL
     */
    @GetMapping("/org/auth-url")
    public Result<String> getOrgAuthUrl() {
        OrgAuthRequest request = new OrgAuthRequest();
        request.setThirdPartyUserId("agent123456");
        request.setAgentName("李四");
        request.setAgentIdCard("110101199001011234");
        request.setAgentMobile("13900139000");
        request.setOrgName("北京示例科技有限公司");
        request.setOrgCertType(3); // 统一社会信用代码
        request.setOrgCertNo("91110105MA00000000");
        request.setLegalRepName("王五");
        request.setLegalRepIdCard("110101198001011234");
        request.setLegalRepAuth(false);
        
        String authUrl = esignAuthApi.buildOrgAuthUrl(request);
        return Result.success(authUrl);
    }

    /**
     * 机构认证回调查询
     */
    @GetMapping("/query/{authFlowId}")
    public Result<OrgAuthInfo> queryOrgAuth(String authFlowId) {
        AuthInfoResponse authInfo = esignAuthApi.getAuthInfo(authFlowId);
        return Result.success(authInfo.getOrgAuthInfo());
    }
}
```

## 功能说明

| 功能 | 方法 | 说明 |
|------|------|------|
| 获取AccessToken | `getAccessToken()` | 自动管理token缓存和过期 |
| 构建个人认证URL | `buildPersonalAuthUrl(PersonalAuthRequest)` | 返回可重定向的认证URL |
| 构建机构认证URL | `buildOrgAuthUrl(OrgAuthRequest)` | 返回可重定向的认证URL |
| 查询认证信息 | `getAuthInfo(String authFlowId)` | 查询认证状态和详情 |
| 解析回调结果 | `parseCallbackResult(String callbackJson)` | 解析e签宝回调结果 |

## 模块结构

```
ruoyi-esign-starter/
├── src/main/java/com/ruoyi/esign/
│   ├── api/
│   │   └── EsignAuthApi.java          # API接口定义
│   ├── core/
│   │   └── DefaultEsignAuthClient.java # 默认实现
│   ├── config/
│   │   └── EsignAutoConfiguration.java # 自动配置
│   ├── properties/
│   │   └── EsignProperties.java        # 配置属性
│   ├── model/
│   │   ├── AccessTokenRequest.java
│   │   ├── AccessTokenResponse.java
│   │   ├── PersonalAuthRequest.java
│   │   ├── OrgAuthRequest.java
│   │   ├── PersonalAuthInfo.java
│   │   ├── OrgAuthInfo.java
│   │   ├── AuthInfoRequest.java
│   │   ├── AuthInfoResponse.java
│   │   └── AuthCallbackResult.java
│   ├── enums/
│   │   ├── AuthStatusEnum.java
│   │   └── GenderEnum.java
│   ├── exception/
│   │   └── EsignException.java
│   └── support/
└── src/main/resources/
    └── META-INF/spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 依赖说明

- Spring Boot 2.6.15+
- Hutool 5.8.24+
- FastJSON2 2.0.58+
- Lombok

## 认证流程

1. **前端跳转**：调用buildPersonalAuthUrl/buildOrgAuthUrl获取认证URL，前端跳转到该URL
2. **用户认证**：用户在e签宝页面完成认证流程
3. **回调通知**：认证完成后e签宝回调到配置的redirectUri
3. **查询结果**：服务端调用getAuthInfo查询认证详情

## 注意事项

- AccessToken会自动缓存和刷新，无需手动管理
- 参数合法性校验由调用方负责，本Starter只做基础非空校验
- HTTPS请求由Hutool Http处理，默认支持
