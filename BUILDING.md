# 构建 / Building

需要 64 位 JDK 17。将 JAVA_HOME 指向 JDK 根目录，在项目目录执行：

```powershell
.\build.cmd --no-daemon clean build
```

Linux/macOS：

```sh
./gradlew --no-daemon clean build
```

成品：`build/libs/everlast_gear-1.0.2-mc1.20.1-forge.jar`。构建包含 reobfJar，发布时使用经过重混淆的最终 JAR。

技术栈：Forge 47.4.28、ForgeGradle 6.0.54、Gradle 8.8、MixinGradle 0.7.38、Java 17。
首次构建需要联网下载 Gradle、Minecraft、Forge 及相关依赖。仓库不设置本机代理或本机 JDK 路径；网络代理按需配置于用户 Gradle 配置或运行环境。

游戏内自动验证：

```powershell
.\build.cmd --no-daemon runGameTestServer -x downloadAssets
```

测试代码位于 src/testmod，只加载于 GameTest 运行，不进入发布 JAR。测试涵盖耐久耗尽保留、大额损耗、破损交互、护甲属性、铁砧、经验修补、绑定诅咒和名单配置。请在独立测试目录运行；测试会暂时更改该运行目录的配置并在结束后还原。

GameTest 使用专用服务器，`-x downloadAssets` 跳过无关的客户端声音、字体等资源下载。
