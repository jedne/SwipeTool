# FanMenu
一个扇形菜单，可以滑动切换，长按编辑，删除和拖动排序等
效果图：
![image](https://github.com/jedne/FanMenu/blob/master/pic/fanmenu.gif)

使用方法：
File->new->Import Module,选择FanMenu，导入，在gradle中添加依赖compile project(':fanmenu')

代码中添加：

1 application的onCreate中初始化

	FanMenuSDK.initSDK(this);
	
2 在需要显示浮标的地方调用

	FanMenuSDK.showFlowing();
	
3 关闭浮标

	FanMenuSDK.hideFlowing();
	
4 打开设置页面

	FanMenuSettingActivity.showSetting(mContext);


项目介绍：
http://blog.csdn.net/jeden/article/details/70049448