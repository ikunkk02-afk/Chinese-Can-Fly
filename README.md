# Chinese Can Fly / 中国人能飞

## 当前开发阶段：Stage 5

这是一个面向 **Minecraft 1.21.1、Fabric、Java 21** 的模组。当前版本实现了无声调拼音聊天、主世界古代岩壁文字、拓印系统、可疑的书和中华大字典。

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

## Stage 3 — 拓印系统

- `minecraft:paper` 与 `minecraft:ink_sac` 可无序合成 2 张 `chinese_can_fly:rubbing_paper`（拓印纸）。
- 手持拓印纸右键古代岩壁文字的正面，可获得保存真实汉字的 `chinese_can_fly:character_rubbing`（文字拓片）。
- 文字拓片使用 Minecraft 1.21.1 Data Component `chinese_can_fly:inscription_character` 保存汉字；同一汉字可堆叠，不同汉字不会堆叠。
- 拓印不会消耗或改变岩壁文字；同一块岩壁可以重复拓印。

## Stage 4 — 可疑的书与创造模式分类页

- 在 3×3 工作台中，将 8 张代表**不同**汉字的有效文字拓片环绕 1 本普通书，可制作 `chinese_can_fly:suspicious_book`（可疑的书）。
- 普通书必须位于中心；重复汉字、空白拓片、伪造或池外字符、非文字拓片及错误位置都会使输出栏保持为空。
- 可疑的书目前没有右键效果；后续阶段才会实现中华大字典与觉醒能力。
- 增加独立的“**中国人能飞**”创造模式分类页，依次提供古代岩壁文字、拓印纸、全部 48 种带真实 Data Component 的有效文字拓片，以及可疑的书。

## Stage 5 — 中华大字典

- 将 `chinese_can_fly:suspicious_book` 放入原版附魔台，在 **15 个有效书架**、玩家至少 **30 级**、至少 **3 个青金石** 的条件下点击最高档附魔，可转换为 `chinese_can_fly:chinese_dictionary`（中华大字典）。
- 成功遵循原版最高档附魔的费用语义：生存模式消耗 3 级经验与 3 个青金石；创造模式不消耗两者。
- 中华大字典拥有持续附魔光效，但目前不能阅读或提供能力；阅读与觉醒属于下一阶段。
- “中国人能飞”创造模式分类页末尾加入中华大字典，并以其作为图标。

## 依赖

运行时需要 Fabric Loader 和 Fabric API `0.116.15+1.21.1`。TinyPinyin `2.0.3.RELEASE` 及其 Aho-Corasick `0.4.0` 依赖已嵌入模组 JAR，玩家无需单独下载。

Cardinal Components API、Cloth Config API、Mod Menu 和 Player Animator 已锁定兼容版本供后续开发使用，但 Stage 2 不使用它们，也不要求玩家安装。

## 后续开发计划

- Stage 6 - 觉醒（阅读中华大字典并获得能力）
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
