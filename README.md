# VideoApp - 影视站 Android 客户端

基于 Kotlin 开发的 Android 影视应用客户端。

## 功能特性

- ✅ 首页视频展示
- ✅ 分类浏览
- ✅ 视频搜索
- ✅ 在线播放（HLS/M3U8）
- ✅ 用户登录注册

## 技术栈

- **语言**: Kotlin
- **架构**: MVVM
- **UI**: Material Design 3
- **网络**: Retrofit + OkHttp
- **图片**: Glide
- **播放**: ExoPlayer (androidx.media3)

## 环境要求

- Android Studio Hedgehog (2023.1.1+)
- JDK 17
- Android SDK 24+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/YOUR_USERNAME/VideoApp.git
cd VideoApp
```

### 2. 配置后端地址

编辑 `app/src/main/java/com/example/videoapp/data/api/ApiClient.kt`:

```kotlin
// 替换为你的 Render 后端地址
private const val BASE_URL = "https://videoweb-backend-xxxx.onrender.com"
```

### 3. 打开项目

使用 Android Studio 打开项目目录。

### 4. 同步依赖

Android Studio 会自动同步 Gradle 依赖。

### 5. 运行应用

- 连接设备或启动模拟器
- 点击 Run 按钮

## 构建 APK

### Debug 版本

```bash
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### Release 版本

```bash
./gradlew assembleRelease
```

APK 位置：`app/build/outputs/apk/release/app-release-unsigned.apk`

## 项目结构

```
app/src/main/java/com/example/videoapp/
├── data/
│   ├── api/           # API 接口
│   ├── model/         # 数据模型
│   └── repository/    # 数据仓库
├── ui/
│   ├── home/          # 首页
│   ├── categories/    # 分类
│   ├── search/        # 搜索
│   ├── profile/       # 个人中心
│   ├── player/        # 播放器
│   └── auth/          # 认证
└── VideoApplication.kt
```

## 后端部署

后端项目：[videoWeb](https://github.com/YOUR_USERNAME/videoWeb)

部署到 Render:
1. Fork 后端仓库
2. 在 Render 创建新服务
3. 连接仓库并部署

## 截图

待添加

## 待办事项

- [ ] 完善用户认证
- [ ] 添加观看历史
- [ ] 收藏功能
- [ ] 推送通知
- [ ] 离线缓存

## 许可证

MIT License

## 免责声明

本项目仅供学习交流使用，请勿用于商业或非法用途。
