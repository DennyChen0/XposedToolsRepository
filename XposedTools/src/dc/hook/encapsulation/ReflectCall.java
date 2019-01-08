package dc.hook.encapsulation;

import org.json.JSONException;
import org.json.JSONObject;

import dc.reverse.mod.XposedModuleContext;
import dc.utils.PyLog;
import de.robv.android.xposed.XposedHelpers;

public class ReflectCall {
	
	public static void recvHongbao(String ContainHid, String remoteId) throws JSONException{
		if (ContainHid == null){
			return ;
		}
		PyLog.log("ContainHid " + ContainHid + " " + remoteId);
		int nIndexBegin = ContainHid.indexOf("&hid=") + "&hid=".length();
		ContainHid = ContainHid.substring(nIndexBegin);
		//System.out.println(ContainHid);
		int nIndexEnd = ContainHid.indexOf("]");
		String hid = ContainHid.substring(0, nIndexEnd);
		PyLog.log("hid :" + hid);

		ClassLoader classLoader = XposedModule.get().mClassLoader;
		Class clze = XposedHelpers.findClass(
				"immomo.com.mklibrary.core.base.ui.MKWebView", classLoader);
		PyLog.log("obj Instance " + clze + " mContext" + XposedModuleContext.mContext);
		//Object obj = XposedHelpers.newInstance(clze, XposedModuleContext.mContext);
		//PyLog.log("obj Instance " + obj);

		String strForm = "{\"url\":\"https://mk.immomo.com/hongbao/personal/grab\",\"data\":{\"hid\":\"%s\",\"sender\":\"%s\",\"desc\":\"鎭枩鍙戣储\"},\"dataType\":\"json\",\"type\":\"POST\",\"method\":\"post\",\"callback\":\"mm._callbacks.request.__BRIDGE_CALLBACK__4_%s\"}";
		Object[] Arys = {hid, remoteId, "" + System.currentTimeMillis()};
		String strIn = String.format(strForm, Arys);
		PyLog.log("String " + strIn);
//		XposedHelpers.callMethod(obj, "processMKBridge", "http", "request",
//				strIn);
	}
	
	public static String retHid(String ContainHid){
		if (ContainHid == null){
			return null;
		}
		PyLog.log("ContainHid " + ContainHid);
		int nIndexBegin = ContainHid.indexOf("&hid=") + "&hid=".length();
		ContainHid = ContainHid.substring(nIndexBegin);
		//System.out.println(ContainHid);
		int nIndexEnd = ContainHid.indexOf("]");
		String hid = ContainHid.substring(0, nIndexEnd);
		PyLog.log("hid :" + hid);
		return hid;
	}
}
