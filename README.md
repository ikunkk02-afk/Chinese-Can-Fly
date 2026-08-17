# Chinese Can Fly / 中国人能飞

## 当前开发阶段：Stage 2

这是一个面向 **Minecraft 1.21.1、Fabric、Java 21** 的模组。当前版本实现了无声调拼音聊天和主世界古代岩壁文字。

从 Stage 2 起，模组必须同时安装在客户端和服务器端：服务器负责注册方块及生成岩壁文字，客户端负责显示真实汉字。Dedicated Server 不会加载客户端渲染代码。

## Stage 1 — 中文聊天转无声调拼音

- 中文聊天转无声调拼音，例如 `我今天要去挖钻石` → `wo jin tian yao qu wa zuan shi`。
- 中英文混合文本处理，例如 `Hello中国人123` → `Hello zhong guo ren 123`。
- 英文、数字、标点和 Emoji 保持原样。
- 所有 `/` 开头的命令不经过转换，包括 `/say 你好`。
- 聊天输入框保持玩家原始输入；仅实际发送的普通聊天被转换。

## Stage 2 — 古代岩壁文字

- `chinese_can_fly:inscribed_rock` 是可通过 `/give @s chinese_can_fly:inscribed_rock` 获得的完整石质方块。
- 每个岩壁文字都保存一个真实汉字；重新进入世界、服务器重启及 Chunk 卸载后都不会改变。
- 客户端使用 Minecraft 自带字体渲染汉字，使其贴在朝向空气的岩壁正面。
- 主世界会低频尝试在自然、暴露且具有一定规模的岩壁上生成单个文字；不生成在水、熔岩、空中或普通地表孤石中。
- 候选字符池含 48 个汉字。每约 6 个主世界 Chunk 会尝试一次，实际成功率取决于是否找到合格岩壁。
- 岩壁文字只会出现在**新生成的 Chunk**。已经探索过的 Chunk 不会 retro-generate。

## 依赖

运行时需要 Fabric Loader 和 Fabric API `0.116.15+1.21.1`。TinyPinyin `2.0.3.RELEASE` 及其 Aho-Corasick `0.4.0` 依赖已嵌入模组 JAR，玩家无需单独下载。

Cardinal Components API、Cloth Config API、Mod Menu 和 Player Animator 已锁定兼容版本供后续开发使用，但 Stage 2 不使用它们，也不要求玩家安装。

## 后续开发计划

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
