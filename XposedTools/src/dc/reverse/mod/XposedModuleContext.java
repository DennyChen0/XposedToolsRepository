package dc.reverse.mod;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebResourceResponse;
import dalvik.system.DexClassLoader;
import dc.hook.encapsulation.XposedModule;
import dc.utils.PyLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import dc.utils.CommandBroadcastReceiver;
public class XposedModuleContext {

	private PackageMetaInfo metaInfo;
	private int apiLevel;
	private boolean HAS_REGISTER_LISENER = false;
	private Application fristApplication;
	public static Context mContext;
	
	private XposedModuleContext() {
	}
	public void initAndHook(PackageMetaInfo info) {
		metaInfo = info;
		hook_Application_attach();
	}
	
	void hook_Application_attach() {
		XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Context context = (Context) param.args[0];
				ClassLoader classLoader = context.getClassLoader();
				try {
					XposedModule.get().initAndHook(context, classLoader);
					//mContext = context;
					// 注册广播
					if (getProcssName().equals(getPackageName())) {
						registBroadcast((Application) param.thisObject);
						mContext = context;
					}
				} catch (Exception e) {
					PyLog.log(Log.getStackTraceString(e));
				}
			}
		});
	}
	
	private void registBroadcast(Application application) {

		if (!HAS_REGISTER_LISENER) {
			fristApplication = application;
			IntentFilter filter = new IntentFilter(CommandBroadcastReceiver.INTENT_ACTION);
			fristApplication.registerReceiver(new CommandBroadcastReceiver(), filter);
			HAS_REGISTER_LISENER = true;
		}
	}

	public String getPackageName() {
		return metaInfo.getPackageName();
	}

	public String getProcssName() {
		return metaInfo.getProcessName();
	}

	public ApplicationInfo getAppInfo() {
		return metaInfo.getAppInfo();
	}

	public Application getAppContext() {
		return this.fristApplication;
	}

	public int getApiLevel() {
		return this.apiLevel;
	}

	public ClassLoader getBaseClassLoader() {
		return this.metaInfo.getClassLoader();
	}
	
	private final static XposedModuleContext sInsatnce = new XposedModuleContext();

	public static XposedModuleContext getInstance() {
		return sInsatnce;
	}
}
