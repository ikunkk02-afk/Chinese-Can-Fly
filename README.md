# Chinese Can Fly / 中国人能飞

## 当前开发阶段：Stage 1

这是一个面向 **Minecraft 1.21.1、Fabric、Java 21** 的客户端模组。当前版本只实现一项玩法：玩家发送普通聊天时，汉字会自动作为无声调拼音发送到服务器。

## 已实现

- 中文聊天转无声调拼音，例如 `我今天要去挖钻石` → `wo jin tian yao qu wa zuan shi`。
- 中英文混合文本处理，例如 `Hello中国人123` → `Hello zhong guo ren 123`。
- 英文、数字、标点和 Emoji 保持原样。
- 所有 `/` 开头的命令不经过转换，包括 `/say 你好`。
- 聊天输入框保持玩家原始输入；仅实际发送的普通聊天被转换。
- 红金 Minecraft 像素风模组图标。

本模组只需安装在客户端；服务器不需要安装它。

## 依赖

运行时需要 Fabric Loader 和 Fabric API `0.116.15+1.21.1`。TinyPinyin `2.0.3.RELEASE` 及其 Aho-Corasick `0.4.0` 依赖已嵌入模组 JAR，玩家无需单独下载。

Cardinal Components API、Cloth Config API、Mod Menu 和 Player Animator 已锁定兼容版本供后续开发使用，但 Stage 1 不使用它们，也不要求玩家安装。

## 后续开发计划

- Stage 2 - 岩壁汉字
- Stage 3 - 拓印系统
- Stage 4 - 可疑的书
- Stage 5 - 中华大字典
- Stage 6 - 觉醒
- Stage 7 - 超级飞行
- Stage 8 - 超级力量

这些功能尚未实现。

## 构建

需要 Java 21：

```powershell
$env:JAVA_HOME = 'C:\\Program Files\\Microsoft\\jdk-21.0.8.9-hotspot'
$env:Path = "$env:JAVA_HOME\\bin;$env:Path"
.\\gradlew.bat clean build
```

构建产物位于 `build/libs/chinese-can-fly-1.0.0.jar`。

## 许可证

项目采用 [MIT License](LICENSE)。嵌入依赖的说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
