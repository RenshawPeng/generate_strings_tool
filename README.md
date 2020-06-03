#### 工具起源

最近在做国际化多语言适配，暂时有 18 种语言，pc 站有 30 多种好像，平常所有显示的文字交于专人负责整理和翻译，翻译完成后把整理好的Excel交给开发人员进行适配。然而并没有这样简单。。。
各种修改、调整、新增文字，每次修改后开发人员都得核对一次，然后各个在strings.xml中修改。

可想而知这是一件多么烦锁的事情，为了从这样一个重复、毫无意义的工作中解脱出来，我花了半天的时间撸了一个工具。有了工具后拿到翻译好的Excel，用工具来一键生成各国语言的资源文件。整理资源也是一样的选择strings.xml一键生成Excel。。这样是不是爽多了？？

#### [](#%E5%B7%A5%E5%85%B7%E7%9A%84%E4%BD%BF%E7%94%A8)工具的使用

![多语言工具](https://upload-images.jianshu.io/upload_images/1432234-dd22e47b7fd63cb1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![image.png](https://upload-images.jianshu.io/upload_images/1432234-73f64169761a671f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![Android](https://upload-images.jianshu.io/upload_images/1432234-96e71bfa21f12c84.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![iOS](https://upload-images.jianshu.io/upload_images/1432234-7c9e15d88edbf2ae.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


写了一个简单的界面，导入项目后运行UIMain 就能愉快的玩耍了！
表格格式：
![image.png](https://upload-images.jianshu.io/upload_images/1432234-53e1eb8e54ac4979.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



*   注意事项
    key：固定标识，这里 Android 和 iOS 未做区分（可自行拓展后徐芬）
    支持注释：key列可以使用注释（直接在Excel中写入注释）

生成结果：

![Android](https://upload-images.jianshu.io/upload_images/1432234-96e71bfa21f12c84.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![iOS](https://upload-images.jianshu.io/upload_images/1432234-7c9e15d88edbf2ae.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### [](#%E9%80%82%E9%85%8D%E4%B8%AD%E7%9A%84%E4%B8%80%E7%82%B9%E5%B0%8F%E5%BB%BA%E8%AE%AE)拓展
- 每次都会读取原本的 xml 文件或者 strings 文件，然后再写进去所以既可以支持新建 key，也可以支持在原本的 key 上修改后，会将修改后的文案直接覆盖。

-源码中也包含 strings 转 Excel，xml 转 Excel，工具入口就不放出来了。


