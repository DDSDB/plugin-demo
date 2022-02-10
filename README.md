# plugin-demo

java插件化开发的实现demo, 该demo中实现的接口为common模块下的Greeting接口

通过加载plugins/plugin/target目录下的plugin-${vesion}.jar启动插件，多个插件通过指定目录启动，后续可以考虑开发基于jar包名拉起插件的功能方便新增插件

想自己实现插件，可以在任意java项目中的通过引入`org.example.common依赖（编译项目可在本地库中产生该依赖，后续即可用maven坐标引入依赖）`，然后在resource目录下创建META-INF/services/com.bonree.dataview.demo.Greeting文件，文件内容中放入Greeting实现类的全类名即可，可以通过config指明jar包的位置找到该插件，可以同时加载多个插件；

通过累加器实现java的插件化开发， 还需要完成的功能有 插件的启动与停止，运行时新增插件功能验证， 

后续功能点：
- 插件的停起，方便运行时动态加载和移除
- 验证运行时新增插件是否会影响已经加载的插件的运行
- 考虑是否需要加入基于jar包名导入插件的逻辑
