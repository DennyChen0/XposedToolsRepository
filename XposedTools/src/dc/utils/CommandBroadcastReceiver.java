package dc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import dc.hook.encapsulation.XposedModule;
import dc.reverse.mod.XposedModuleContext;
import de.robv.android.xposed.XposedHelpers;

public class CommandBroadcastReceiver extends BroadcastReceiver {

	public static String INTENT_ACTION = "py.invoke";
	public static String TASK_NAME_KEY = "task";
	public static String PID = "pid";
	public static String momoId = "senderId";
	public static String discussIdKey = "discussId";
	public static String groupIdKey = "groupId";
	
	@Override
	public void onReceive(final Context arg0, Intent arg1) {
		if (INTENT_ACTION.equals(arg1.getAction())) {
			String hid = arg1.getStringExtra(PID);
			String senderId = arg1.getStringExtra(momoId);
			String discussId = arg1.getStringExtra(discussIdKey);
			String groupId = arg1.getStringExtra(groupIdKey);
			PyLog.log("pid = " + hid + " SenderId = " + senderId + " discussId=" + discussId + " groupId=" + groupId);
		
			ClassLoader classLoader = XposedModule.get().mClassLoader;
			Class clze = XposedHelpers.findClass(
					"immomo.com.mklibrary.core.base.ui.MKWebView", classLoader);
			// XposedHelpers.
			Object obj;
			obj = XposedHelpers.newInstance(clze, XposedModuleContext.mContext);
			PyLog.log("obj Instance " + obj);
			String strIn = null;
			if (senderId != null){
				String strPersonForm = "{\"url\":\"https://mk.immomo.com/hongbao/personal/grab\",\"data\":{\"hid\":\"%s\",\"sender\":\"%s\",\"desc\":\"鎭枩鍙戣储\"},\"dataType\":\"json\",\"type\":\"POST\",\"method\":\"post\",\"callback\":\"mm._callbacks.request.__BRIDGE_CALLBACK__4_%s\"}";
				Object[] Arys = {hid, senderId, "" + System.currentTimeMillis()};
				strIn = String.format(strPersonForm, Arys);
			}
			else if(discussId != null){
				String strDiscussForm = "{\"url\":\"https://mk.immomo.com/hongbao/hongbao/grab\",\"data\":{\"hid\":\"%s\",\"gid\":\"%s\",\"type\":36},\"dataType\":\"json\",\"type\":\"POST\",\"method\":\"post\",\"callback\":\"mm._callbacks.request.__BRIDGE_CALLBACK__4_%s\"}";
				Object[] Arys = {hid, discussId, "" + System.currentTimeMillis()};
				strIn = String.format(strDiscussForm, Arys);
			}
			else if(groupId != null){
				String strGroupForm = "{\"url\":\"https://mk.immomo.com/hongbao/hongbao/grab\",\"data\":{\"hid\":\"%s\",\"gid\":\"%s\",\"type\":3},\"dataType\":\"json\",\"type\":\"POST\",\"method\":\"post\",\"callback\":\"mm._callbacks.request.__BRIDGE_CALLBACK__4_%s\"}";
				Object[] Arys = {hid, groupId, "" + System.currentTimeMillis()};
				strIn = String.format(strGroupForm, Arys);
			}
			PyLog.log("String " + strIn);
			XposedHelpers.callMethod(obj, "processMKBridge", "http", "request",
					strIn);
			return;
		}
	}
}
// if (taskKey.equals("shoukuanma")) {
// Object objHomePageFragment =
// XposedModule.get().objHomePageFragment;
// ClassLoader classLoader = XposedModule.get().mClassLoader;
// if (objHomePageFragment == null || classLoader == null) {
// PyLog.log("objHomePageFragment or classLoader is null");
// return;
// }
//
// // Class clzHomePageFragment =
// //
// XposedHelpers.findClass("com.lakala.qcodeapp.module.home.fragment.HomePageFragment",
// // classLoader);
// Fragment fragment = (Fragment) objHomePageFragment;