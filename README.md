### 背景
目前市面上有很多的 SDK,开发者开发 App 时可以集成各种 SDK 来方便快捷的实现某一部分功能

最近做了 Andorid 和 iOS 的 SDK 测试,做了个小框架,共享出来,和大家一起探讨学习

### SDK 的测试方法
目前大部分的 SDK 测试都是开发一个demo,集成 SDK,然后针对这个 demo 测试以此传参到 SDK 的 api 中.<br>

寻找其他的测试手段,先将 SDK 集成到 demo 中,demo 内置一个 HTTP server,demo 启动时反射扫描被测的 SDK 类,启动 HTTP server,测试人员以约定的参数格式发送 HTTP 请求到 demo 的 server 中,server 反射调用 SDK 对应的接口,返回 同步/异步 接口的返回值,有了返回值就可以用来做断言啦.

### AshenAndroid
· 反射扫描被测类 <br>
· 根据 http 请求来自动映射对应的接口 <br>
· 支持测试同步/异步接口

#### 接入要求
1. 需要有 JDK 1.8 编译的 SDK, Android Studio  编译时,在项目的 `build.gradle` 文件最后增加 
```
tasks.withType(JavaCompile) {
    options.compilerArgs += ["-parameters"]
}
```
这样生成的 jar 包,接口中会保留参数形参,反射获取到的为 `String name`,否则获取的参数为 `String arg0`,参数名会影响到测试用例的参数,所以显示出真实的参数名会提升用例的维护难易度,可想而知一个多参数的接口,用例写成`{"arg0":"testerhome","arg1":"testerName"}`,维护起来多棘手.

#### 接入方法
1. 在项目中引入 SDK.
2. 注册被测类, 在 `utils/Init.java` 文件中的 `initClassRegister` 函数注册:
```
@RequiresApi(api = Build.VERSION_CODES.P)
    public void initClassRegister() {
        // 只需给 classRegistered put 就可以,key 为包路径,value 为调用 api 的 SDK 实例,单例类为 instance,例如 Gson
        classRegistered.put("com.google.gson.Gson", new Gson());

        for (Map.Entry classEntry : classRegistered.entrySet()) {
            Object o = classEntry.getValue();
            classObjectCallBack.putAll(ClassUtil.getClassObjectNotCallBack(o));
            classObject.putAll(ClassUtil.getClassObject(o));
            classTypeObject.putAll(ClassUtil.getClassTypeObject(o));
        }
    }
```
此时可以打包安装 APP, app 上会显示手机的host,例如 `192.168.1.100` , 使用浏览器访问 `http://192.168.1.100:9999/getAllInterface`,就会显示出已注册类的所有函数,以上 Gson 的例子会返回
```
{"com.google.gson.Gson":[{"equals":{"arg0":"java.lang.Object"}},{"excluder":{}},{"fieldNamingStrategy":{}},{"fromJson":{"arg0":"com.google.gson.JsonElement","arg1":"java.lang.Class<T>"}},{"fromJson":{"arg0":"com.google.gson.JsonElement","arg1":"java.lang.reflect.Type"}},{"fromJson":{"arg0":"com.google.gson.stream.JsonReader","arg1":"java.lang.reflect.Type"}},{"fromJson":{"arg0":"java.io.Reader","arg1":"java.lang.Class<T>"}},{"fromJson":{"arg0":"java.io.Reader","arg1":"java.lang.reflect.Type"}},{"fromJson":{"arg0":"java.lang.String","arg1":"java.lang.Class<T>"}},{"fromJson":{"arg0":"java.lang.String","arg1":"java.lang.reflect.Type"}},{"getAdapter":{"arg0":"com.google.gson.reflect.TypeToken<T>"}},{"getAdapter":{"arg0":"java.lang.Class<T>"}},{"getClass":{}},{"getDelegateAdapter":{"arg0":"com.google.gson.TypeAdapterFactory","arg1":"com.google.gson.reflect.TypeToken<T>"}},{"hashCode":{}},{"htmlSafe":{}},{"newBuilder":{}},{"newJsonReader":{"arg0":"java.io.Reader"}},{"newJsonWriter":{"arg0":"java.io.Writer"}},{"notify":{}},{"notifyAll":{}},{"serializeNulls":{}},{"toJson":{"arg0":"com.google.gson.JsonElement"}},{"toJson":{"arg0":"java.lang.Object"}},{"toJson":{"arg0":"com.google.gson.JsonElement","arg1":"com.google.gson.stream.JsonWriter"}},{"toJson":{"arg0":"com.google.gson.JsonElement","arg1":"java.lang.Appendable"}},{"toJson":{"arg0":"java.lang.Object","arg1":"java.lang.Appendable"}},{"toJson":{"arg0":"java.lang.Object","arg1":"java.lang.reflect.Type"}},{"toJson":{"arg0":"java.lang.Object","arg1":"java.lang.reflect.Type","arg2":"com.google.gson.stream.JsonWriter"}},{"toJson":{"arg0":"java.lang.Object","arg1":"java.lang.reflect.Type","arg2":"java.lang.Appendable"}},{"toJsonTree":{"arg0":"java.lang.Object"}},{"toJsonTree":{"arg0":"java.lang.Object","arg1":"java.lang.reflect.Type"}},{"toString":{}},{"wait":{}},{"wait":{"arg0":"long"}},{"wait":{"arg0":"long","arg1":"int"}}]}
```

2. 实现异步回调接口的 callback. SDK 中可能含有大量的异步回调接口,回调的 callback 可能有很多的中间状态,框架没有办法判断触发了哪一个判断代表了本次接口调用完毕,所以如果是异步接口的话,回调需要用户自己实现.回调在 `utils/CallBackImpl.java`中实现,在`utils/Init.java`文件中的 `initCallback` 方法中注册.
```
// callback 的定义
public static Callback callback = new Callback() {
        @Override
        public void onSuccess(String t) {
            // 成功,只需要设置返回值
            responseMap.put("token", t);
            test_done = true;
        }

        @Override
        public void onError(ErrorCode e) {
            // 失败,需要设置失败的 code
            responseMap.put(AshenConst.errorCode, e.getValue());
            responseMap.put(AshenConst.code, AshenConst.ERROR);
            test_done = true;
        }

        @Override
        public void onProgress(Code code) {
            // 中间状态,可以什么都不做,或者给结果的 map 中自定义设置任何属性
        }
    };

```
3. 使用 HTTP 调用 Gson,假设我们测试 Gson 的 equals 接口,我们使用浏览器访问 <br>
`http://192.168.1.100:9999/getInterfaceParams?name=equals`<br>浏览器返回 <br> 
`{"message":[{"com.google.gson.Gson":{"equals":{"arg0":"java.lang.Object"}}}]}`<br>
使用 Python 测试这个接口

```
import requests

r = requests.post("http://192.168.1.100:9999/equals",json={"arg0":{"a":"b"}})
print(r.text)
```
返回数据中的 `{"message":false}` 就是实际接口的返回值

`{"code":200,"message":{"message":false},"type":"success","time_diff(ms)":"0"}`

### 说明
1. demo 默认的端口设置的为 9999,这个是可以在源码中修改的.
2. 启动的为 HTTP 服务,目前只内置了两个接口,一个是 `/getAllInterface` 获取所有接口,一个是 `/getInterfaceParams?name=methodName` 获取被测接口
3. 本质上是一个 HTTP server,所以可以二次开发给 app 增加更多的接口,也可以直接返回 html,便于用户操作
4. 如果不同的类具有同名同参的接口,则请求增加 `testClassName` 的 key,value 写类名可区分,如果是不同包下的类名也一致,则可以连包名都写上,是一个 contains 的概念,如果匹配到多个则默认使用第一个
5. 对于 json 转对象使用的是 Gson 库,如果默认转的不符合需求,可以注册 `JsonDeserializer`,项目中默认增加了对于 `android.net.Uri` 的 decode 和 encode
6. 项目中关于 HTTP 请求处理的主文件是 `src/main/java/com/android/AshenAndroid/server/impl/AshenHTTPServlet.java`
7. 需要更多内置文件:将文件添加到 `src/main/assets` 目录中,再配置到 `AshenConst.java` 的 `fileNameList` 中
