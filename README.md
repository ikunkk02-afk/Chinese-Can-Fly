# Chinese Can Fly / 中国人能飞

## 当前开发阶段：Stage 8

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

## Stage 6 — 阅读与觉醒

- 手持中华大字典连续按住右键 **10 秒（200 tick）** 阅读；松开、切换物品、丢弃字典、死亡或任何原版使用中断都会从零开始，且不会获得能力。
- 阅读期间会显示 ActionBar 进度、轻微翻页声与逐渐增强的附魔、红色和金色粒子。已经觉醒的玩家再次右键会收到“你已经读懂这本字典了。”，不会重新进入阅读。
- 成功后以服务器为权威一次性完成 `dictionaryRead`、`chineseUnlocked` 与 `powerUnlocked`，立即同步到客户端；汉字聊天恢复，中文、英文、数字和 Emoji 都保持原样。
- 状态由 Cardinal Components API 保存，退出重进、服务器重启、死亡重生及换维度都不会丢失。
- 觉醒会显示“**中国人能飞**”中央标题，产生红金粒子、真实 Unicode 汉字环绕升空效果，并对附近玩家同步可见的演出。
- 觉醒后的生存/冒险模式玩家获得接近原版创造模式的基础飞行权限（双击空格），但不会改游戏模式、速度、碰撞、伤害或物品。

## Stage 7 — 超级飞行核心

- 已觉醒玩家保留 Stage 6 的原版风格基础飞行（双击空格）；仅在基础飞行中按住原版 Sprint 和前进键才会进入超级飞行，地面疾跑保持 Vanilla 行为。
- 超级飞行会沿视线方向平滑加速、惯性转向，并支持高速爬升和俯冲；默认最高速度为 `2.50 blocks/tick`（约 `50 blocks/second`）。
- 第三人称会在 `super_fly` 与 `super_fly_fast` 超人飞行姿势之间平滑切换；附近玩家可以看到动画、气流和音爆。
- 高速 FOV、白灰气流与少量红金 Dust 粒子均由客户端本地生成；首次越过音速阈值会在 80 格内播放一次音爆并生成扩散圆环。
- 服务器验证全部启动条件、控制速度上限，并只在状态转换和音爆时发送自定义网络包，不会每 tick 同步速度或粒子。
- 高速移动会预先检查扫掠路径中的已加载 Chunk 与方块碰撞；Stage 8 会在正常移动前由服务器决定是否破坏路径中的普通方块。
- 水、岩浆、Elytra、骑乘、落地、死亡、退出、换维度与 `/chinesecanfly resetawakening` 都会清理瞬时超级飞行状态；它不会写入 CCA 或玩家 NBT。

## Stage 8 — 高速撞穿方块

- 超级飞行达到 `0.90 blocks/tick` 后，服务器会沿实际飞行路径每 `0.25` 格采样，并破坏碰撞路径中的普通方块，形成约 2–3 格宽、约 3 格高的自然穿行通道。
- 速度越高，可破坏硬度越高的方块；穿过方块会按实际硬度减速，因此 Dirt、Stone 和 Deepslate 的阻力不同。
- 方块使用正常服务器方块破坏流程且不掉落物品或经验；不会使用 noClip、临时旁观碰撞或客户端删除方块。
- 每 tick 最多破坏 64 个方块。达到上限时本 tick 暂停但维持超级飞行，下一 tick 继续；未加载 Chunk 不会被强制加载。
- BlockEntity 默认不可破坏，因此 Chest、Spawner、模组机器和 `chinese_can_fly:inscribed_rock` 都会阻挡玩家并保留数据。
- `#chinese_can_fly:super_flight_immune` 是可由数据包扩展的保护 Tag；Obsidian、Bedrock、Portal、命令方块等默认不可撞穿。
- 撞穿时服务器生成少量与实际方块匹配的碎片和限频破坏声；撞上不可破坏方块时会有重击反馈，并安全退出超级飞行但保留基础飞行。

## 依赖

运行时需要 Fabric Loader、Fabric API `0.116.15+1.21.1`、**Cardinal Components API `6.1.3`**，以及 **Player Animator `2.0.4+1.21.1`**（真实 Mod ID：`playeranimator`）。CCA 必须提供 `cardinal-components-base` 与 `cardinal-components-entity` 模块；缺少任一正式前置都会由 `fabric.mod.json` 的依赖检查明确提示。

TinyPinyin `2.0.3.RELEASE` 及其 Aho-Corasick `0.4.0` 依赖已嵌入模组 JAR，玩家无需单独下载。Cloth Config API 和 Mod Menu 仍仅为开发兼容依赖，本阶段没有设置 GUI。

## 开发/调试命令

- `/chinesecanfly status`：所有玩家可查看自己的 `dictionaryRead`、`chineseUnlocked`、`powerUnlocked` 与瞬时调试字段 `superFlightActive`。
- `/chinesecanfly resetawakening`：仅 OP（权限等级 2）可用；重置执行者的觉醒状态并立即停止超级飞行。生存/冒险模式同时撤销本模组提供的飞行权限；创造和旁观模式的原版飞行不会被修改。

## 后续开发计划

- Stage 8 - 高速撞穿方块
- Stage 9 - 超级力量
- Stage 10 - 抓取与地面猛砸
- Stage 11 - 冲击波
- Stage 12 - 超级防御

Stage 9 及之后的功能尚未实现。

## 构建

需要 Java 21：

```powershell
$env:JAVA_HOME = 'D:\\chinese-can-fly-1.21.1\\.toolchains\\jdk-21.0.12+8'
$env:Path = "$env:JAVA_HOME\\bin;$env:Path"
.\\gradlew.bat clean build
```

构建产物位于 `build/libs/chinese-can-fly-1.0.0.jar`。

## 许可证

项目采用 [MIT License](LICENSE)。嵌入依赖的说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
