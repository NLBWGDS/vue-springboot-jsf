# 瑜伽馆会员管理系统

基于 Vue 2、Spring Boot、MyBatis 和 MySQL 的瑜伽馆管理项目。

## 主要功能

- 管理员、教练和会员账号管理
- 课程管理、教练预约
- 课程订单、余额和退款
- 器材管理、器材预约、百度 OCR
- 公告、菜谱、经验分享和评论
- 业务数据统计

## 环境准备

- JDK 11
- Maven
- Node.js 和 npm
- MySQL

## 数据库配置

创建数据库：

```sql
CREATE DATABASE manager
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

在新建的空数据库中执行 `database/schema.sql`。

注意：脚本包含删除并重建表的语句，请勿直接用于已有业务数据的数据库。

脚本仅包含表结构，不提供真实用户数据或预设登录账号。

## 后端配置

将：

`manager/springboot/src/main/resources/application-local.yml.example`

复制为同目录下的：

`application-local.yml`

填写自己的数据库账号、密码，以及百度 OCR 应用凭据。

本地配置文件已被 Git 忽略，请勿提交真实凭据。

## 启动后端

在项目根目录打开 PowerShell：

```powershell
cd manager/springboot
mvn.cmd spring-boot:run
```

后端地址：`http://localhost:9090`

## 启动前端

在项目根目录另开一个 PowerShell：

```powershell
cd manager/vue
npm.cmd install
npm.cmd run serve
```

登录页面：`http://localhost:8080/login`

## 说明

充值为模拟余额操作，没有接入真实支付平台。
用户上传文件、本地私密配置和构建产物不包含在仓库中。