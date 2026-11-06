# 构建 / Building

需要 JDK 25。先将 JAVA_HOME 指向 JDK 根目录，然后在仓库根目录运行：

```powershell
.\build.cmd build
```

Linux / macOS：

```sh
chmod +x gradlew
./gradlew --no-daemon build
```

Gradle Wrapper 自动下载 Gradle 9.2.1，产物位于 `build/libs/`。
Minecraft 和 NeoForge 的目标版本由当前分支的 `gradle.properties` 定义。
GitHub Actions 自动安装 JDK 25 并使用相同的 Gradle Wrapper 构建。

## 可选的本机代理 / Optional local proxy

仓库默认直接连接依赖仓库，不要求本地代理、工作区外的 JDK/Gradle 目录或依赖镜像。
如本机确需代理，可将以下设置放入用户级 `~/.gradle/gradle.properties`
（Windows 为 `%USERPROFILE%\.gradle\gradle.properties`），端口按实际情况修改：

```properties
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=65532
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=65532
systemProp.http.nonProxyHosts=localhost|127.0.0.1
```

请勿将本机代理、JDK 绝对路径或 JAVA_TOOL_OPTIONS 提交到项目配置。
`build.cmd` 保留调用者的 JAVA_HOME、GRADLE_USER_HOME 和 JAVA_TOOL_OPTIONS。
如有现成的 `nf-mirror`，可放在项目目录或其上一级；没有镜像也能从远程仓库构建。
