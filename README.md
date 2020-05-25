# Excel生成Android Stings.xml工具

![WX20180427-134909.png](https://upload-images.jianshu.io/upload_images/5488544-e1594caa69c3184b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![WX20180427-134918.png](https://upload-images.jianshu.io/upload_images/5488544-c5ce4feebbce4ff8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![WX20180427-134930.png](https://upload-images.jianshu.io/upload_images/5488544-17b25d8d4466ef60.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### Excel 表格格式
key | cn | en |ja
---|---|---|---
login |	登录	| Login	 | 登録
name		| 姓名	| 	name		| ユーザーネーム
mail_address		| 邮箱	| 	Mail address		| メールアドレス
password	| 	密码		| Password		| パスワード

#### 支持注释

![WX20180427-150935.png](https://upload-images.jianshu.io/upload_images/5488544-f19fafad39a0b45b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 生成结果
```
<?xml version="1.0" encoding="UTF-8"?>
<resources>
  <!--test-->
  <string name="login">登录</string>
  <string name="name">姓名</string>
  <string name="mail_address">邮箱</string>
  <string name="password">密码</string>
</resources>
```


#### *注意事项
    key：固定标识
    支持注释：key列可以使用注释（直接在Excel中写入注释）
    完善表格 别出现空行，不会报错但是会写空字符串
