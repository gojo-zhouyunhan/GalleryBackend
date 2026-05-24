# galleryBackend

`galleryBackend` 是为前端项目 `gallery` 准备的 Spring Boot + Maven 后端。  
它把原本保存在前端 [index.js](</D:/前端作业/gallery/src/store/index.js>) 里的用户、画作、评论、好友、个人画廊墙面布局等数据迁移到 MySQL，帮助当前项目从纯前端演示改造成前后端分离的全栈项目。

## 项目结构

- `controller`：对外提供 REST 接口
- `service`：定义业务接口
- `service/impl`：实现具体业务逻辑
- `common`：统一返回体
- `config`：跨域、全局异常处理
- `src/main/resources/sql/init_gallery.sql`：数据库建表和初始化数据脚本
- `src/main/resources/application.yml`：数据库连接和服务端口配置

## 技术栈

- Java 17
- Spring Boot 3.2.5
- Maven
- Spring Web
- Spring JDBC
- MySQL 8.x

## 启动前准备

1. 安装 JDK 17，并确保 `java -version` 可用。
2. 安装 Maven，并确保 `mvn -v` 可用。
3. 安装 MySQL，建议使用 MySQL 8.x。
4. 确认本机可以创建数据库，并且你知道 MySQL 的用户名和密码。

## 数据库初始化

数据库脚本路径：

- [init_gallery.sql](</你的路径/galleryBackend/src/main/resources/sql/init_gallery.sql>)

执行方式：

1. 打开 MySQL 客户端。
2. 执行 `init_gallery.sql`。
3. 该脚本会自动：
   - 创建数据库 `gallery_db`
   - 创建 `users`、`paintings`、`painting_comments`、`music`、`friendships`、`wall_placements` 六张表
   - 插入初始用户、画作、音乐数据

可直接执行的 SQL 示例：

```sql
SOURCE 你的路径/galleryBackend/src/main/resources/sql/init_gallery.sql;
```

如果你的 MySQL 客户端不支持 `SOURCE`，就直接把脚本内容复制进去执行。

## 配置数据库连接

配置文件路径：

- [application.yml](</D:/前端作业/galleryBackend/src/main/resources/application.yml>)

默认配置如下：

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gallery_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456
```

你至少要改这两项：

- `username`
- `password`

如果你的 MySQL 端口不是 `3306`，也要同步修改 `url`。

## 怎么启用项目

项目根目录：

- [galleryBackend](</你的路径/galleryBackend>)

推荐启动步骤：

1. 打开终端进入项目目录：

```powershell
cd D:\前端作业\galleryBackend
```

2. 先编译一次，确认依赖和代码都正常：

```powershell
mvn -q -DskipTests compile
```

3. 启动 Spring Boot：

```powershell
mvn spring-boot:run
```

4. 启动成功后，默认访问地址：

```text
http://localhost:8081
```

如果你看到 Spring Boot 正常输出并监听 `8081` 端口，就说明后端已经启用成功。

## 和前端怎么联动

前端接口封装文件在：

- [src/api/index.js](</D:/前端作业/gallery/src/api/index.js>)

默认请求后端地址：

```js
const BASE_URL = 'http://localhost:8081/api'
```

也就是说：

1. 先启动 MySQL
2. 再启动 `galleryBackend`
3. 最后启动前端 `gallery`

这样前端就能访问后端接口。

## 数据表说明

### `users`

作用：保存用户基础信息。

主要字段：

- `id`：用户主键
- `username`：用户名
- `password`：密码
- `status`：登录状态，`0` 未登录，`1` 已登录
- `gallery_permission`：画廊权限，`public` / `friends` / `private`

### `paintings`

作用：保存画作基础信息。

主要字段：

- `id`：画作主键
- `name`：画名
- `img_url`：图片路径
- `author`：作者
- `create_time`：创作时间
- `type`：分类，如 `modern`、`classical`、`impression`
- `status`：是否已被挂到某个画廊墙面，`0` 未挂，`1` 已挂
- `like_count`：点赞数
- `comment_count`：评论数

### `painting_comments`

作用：保存画作评论。

主要字段：

- `painting_id`：所属画作
- `commentator`：评论人
- `content`：评论内容
- `create_time`：评论时间

### `music`

作用：保存配乐信息。

### `friendships`

作用：保存好友关系。  
这里采用双向存储，也就是 A 加 B 好友时，会同时插入两条记录。

### `wall_placements`

作用：保存某个用户画廊里，某幅画挂在哪面墙上、位置在哪、缩放是多少。

主要字段：

- `user_id`
- `painting_id`
- `wall_name`
- `position_x`
- `position_y`
- `position_z`
- `scale_x`
- `scale_y`
- `scale_z`
- `size_width`
- `size_height`

## 接口总览

接口统一前缀：

```text
http://localhost:8081/api
```

接口统一返回格式：

```json
{
  "success": true,
  "message": "ok",
  "data": {}
}
```

请求失败时一般会返回：

```json
{
  "success": false,
  "message": "错误原因",
  "data": null
}
```

## 1. 认证接口

### `POST /api/auth/login`

作用：用户登录，并把数据库中的用户状态改成已登录。

请求体：

```json
{
  "username": "admin1",
  "password": "123456"
}
```

返回数据说明：

- 返回当前用户完整信息
- 包含好友列表
- 包含 `Wall` 墙面布局结构

### `POST /api/auth/register`

作用：注册新用户。

请求体：

```json
{
  "username": "newUser",
  "password": "123456",
  "galleryPermission": "public"
}
```

说明：

- `galleryPermission` 可省略，默认 `public`

### `POST /api/auth/logout/{username}`

作用：退出登录，并把数据库中的用户状态改成未登录。

路径参数：

- `username`：用户名

示例：

```text
POST /api/auth/logout/admin1
```

## 2. 用户接口

### `GET /api/users`

作用：查询所有用户。

返回内容：

- 用户基础信息
- 好友列表
- 画廊权限
- 墙面数据

### `GET /api/users/{username}`

作用：按用户名查询某个用户详情。

典型用途：

- 进入某个用户的个人画廊前，先读取他的完整资料

### `PATCH /api/users/{username}/permission`

作用：修改某个用户画廊访问权限。

请求体：

```json
{
  "permission": "friends"
}
```

可选值：

- `public`
- `friends`
- `private`

### `POST /api/users/{username}/friends`

作用：给当前用户添加好友，并自动建立双向好友关系。

请求体：

```json
{
  "friendUsername": "admin2"
}
```

说明：

- A 加 B 后，B 的好友列表里也会出现 A

### `DELETE /api/users/{username}/friends/{friendUsername}`

作用：删除好友，并同时删除双向好友关系。

示例：

```text
DELETE /api/users/admin1/friends/admin2
```

## 3. 画作接口

### `GET /api/paintings`

作用：获取全部画作。

返回内容包含：

- 画作基础信息
- 点赞数
- 评论数
- 评论列表
- 最新一条评论摘要 `commentContent`

### `GET /api/paintings/type/{type}`

作用：按分类查询画作。

示例：

```text
GET /api/paintings/type/modern
```

常见分类：

- `modern`
- `classical`
- `impression`

### `GET /api/paintings/{paintingId}`

作用：查询单幅画作详情。

### `POST /api/paintings/{paintingId}/like`

作用：给某幅画点赞，点赞数加 1。

示例：

```text
POST /api/paintings/1/like
```

### `GET /api/paintings/{paintingId}/comments`

作用：获取某幅画的全部评论。

### `POST /api/paintings/{paintingId}/comments`

作用：给某幅画新增评论，并同步更新 `paintings.comment_count`。

请求体：

```json
{
  "commentator": "admin1",
  "content": "这幅画很喜欢"
}
```

## 4. 音乐接口

### `GET /api/music`

作用：获取全部配乐列表。

返回内容：

- `id`
- `name`
- `singer`
- `url`

## 5. 个人画廊接口

### `GET /api/galleries/{username}`

作用：获取某个用户的个人画廊信息。

返回内容：

- `user`：用户完整信息
- `walls`：四面墙的挂画布局

### `POST /api/galleries/{username}/wall-paintings`

作用：把某幅画挂到某个用户的某一面墙上。

请求体示例：

```json
{
  "wallName": "Front",
  "paintingId": 1,
  "position": {
    "x": -15,
    "y": 13,
    "z": -24.9
  },
  "scale": {
    "x": 1,
    "y": 1,
    "z": 1
  },
  "size": {
    "width": 4,
    "height": 3
  }
}
```

说明：

- 会向 `wall_placements` 写入挂画记录
- 会把 `paintings.status` 更新为 `1`
- 如果该画已经挂在别人的画廊里，会拒绝挂载

### `PUT /api/galleries/{username}/wall-paintings/{paintingId}`

作用：更新某幅已挂画作的位置、缩放、尺寸，或者换墙面。

请求体示例：

```json
{
  "wallName": "Right",
  "position": {
    "x": 24.9,
    "y": 11,
    "z": 6
  },
  "scale": {
    "x": 1.2,
    "y": 1.2,
    "z": 1
  },
  "size": {
    "width": 4,
    "height": 3
  }
}
```

说明：

- 适合 2D/3D 画廊里拖拽、缩放后回写数据库

### `DELETE /api/galleries/{username}/walls/{wallName}/paintings/{paintingId}`

作用：把某幅画从某面墙上移除。

示例：

```text
DELETE /api/galleries/admin1/walls/Front/paintings/1
```

说明：

- 会删除 `wall_placements` 对应记录
- 会把 `paintings.status` 重置为 `0`

## 接口和前端功能的对应关系

- 登录弹窗：`POST /api/auth/login`
- 注册弹窗：`POST /api/auth/register`
- 退出登录：`POST /api/auth/logout/{username}`
- 设置画廊权限：`PATCH /api/users/{username}/permission`
- 好友搜索和好友列表：`GET /api/users`
- 添加好友：`POST /api/users/{username}/friends`
- 删除好友：`DELETE /api/users/{username}/friends/{friendUsername}`
- 画作展示页：`GET /api/paintings`
- 分类画廊页：`GET /api/paintings/type/{type}`
- 点赞：`POST /api/paintings/{paintingId}/like`
- 评论：`GET /api/paintings/{paintingId}/comments`、`POST /api/paintings/{paintingId}/comments`
- 个人画廊读取：`GET /api/galleries/{username}`
- 挂画：`POST /api/galleries/{username}/wall-paintings`
- 拖拽和缩放落库：`PUT /api/galleries/{username}/wall-paintings/{paintingId}`
- 删除挂画：`DELETE /api/galleries/{username}/walls/{wallName}/paintings/{paintingId}`
- 音乐列表：`GET /api/music`

## 已知说明

1. 当前后端没有做 JWT、Session、Spring Security。
2. 登录状态是直接写数据库字段 `status`，适合课程作业和本地项目演示，不适合生产环境。
3. 当前图片和音乐路径是按前端静态资源路径设计的，后续如果你要做真正的文件上传，可以再扩展上传接口。
4. `index.js` 原始数据里有部分文本存在编码混乱，因此 SQL 初始化数据里做了可入库的整理版；如果你想完全按你本地素材名称对齐，可以继续微调脚本里的 `img_url` 和 `url`。

## 建议的联调顺序

1. 执行数据库脚本
2. 修改 `application.yml` 里的数据库账号密码
3. 启动后端 `mvn spring-boot:run`
4. 启动前端项目
5. 先用登录、用户列表、画作列表接口做基础验证
6. 再联调挂画、评论、好友关系这些交互接口

如果你下一步要我继续，我可以直接帮你把 [index.js](</D:/前端作业/gallery/src/store/index.js>) 里的 Vuex action 改成调用这些后端接口。  
