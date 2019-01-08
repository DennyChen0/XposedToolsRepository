package dc.hook.encapsulation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import dc.reverse.mod.XposedModuleContext;
import dc.utils.PyLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class XposedModule {
	private Object g_Object = null;
	private Context mContext = null;
	private String mStrRemoteId;
	private String mStrDiscussId;
	private String mStrGroupId;
	
	public ClassLoader mClassLoader = null;
	public Object objHomePageFragment = null;

	public void initAndHook(Context context, ClassLoader classLoader) {
		mContext = context;
		mClassLoader = classLoader;
		//hook_processMKBridge();
		hook_eMessage();
		hook_JsonToString();
		//hook_fMessage();
	}

	// com.immomo.momo.service.d.a.e
	void hook_eMessage() {
		// PyLog.log("come 0 " + "d");
		Class clz = XposedHelpers.findClass("com.immomo.momo.service.d.a",
				mClassLoader);
		// PyLog.log("come 1 " + clz);
		Class clz2 = XposedHelpers.findClass(
				"com.immomo.momo.service.bean.Message", mClassLoader);
		// PyLog.log("come 2 " + clz2);
		XposedHelpers.findAndHookMethod(clz, "e", clz2, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param)
					throws Throwable {
				String str = "" + param.args[0];
				PyLog.log("Message " + str);
				
				String discussId = (String) XposedHelpers.getObjectField(param.args[0], "discussId");
				String msgId = (String) XposedHelpers.getObjectField(param.args[0], "msgId");
				String remoteId = (String) XposedHelpers.getObjectField(param.args[0], "remoteId");
				String groupId = (String) XposedHelpers.getObjectField(param.args[0], "groupId");
				if (msgId.contains("HB_")){
					mStrRemoteId = (String) XposedHelpers.getObjectField(param.args[0], "remoteId");
				}
				else{
					mStrRemoteId = null;
				}
				mStrDiscussId = discussId;
				mStrGroupId = groupId;
				PyLog.log("Message remoteId msgId groupId discussId " + remoteId + " " + msgId + " " + groupId + " " + discussId);
			}
		});
	}

	
	void hook_JsonToString() {
		XposedHelpers.findAndHookMethod(JSONObject.class, "toString",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						String strContainHid = (String) param.getResult();
						PyLog.log("strContainHid test" + strContainHid);
						if (!isHongbaoRecv(strContainHid)){
							return;
						}
						
					}
				});
	
	}

	boolean isHongbaoRecv(String strContainHid){
		if (strContainHid.contains("session_text") && 
				strContainHid.contains("hongbao") && 
				strContainHid.contains("&hid=")){
			
			String hid = ReflectCall.retHid(strContainHid);	
			if (strContainHid.contains("personal")){
				 Intent intent = new Intent(); 
				    // ActionUtils.ACTION_EQUES_UPDATE_IP 与注册时保持一致
				    intent.setAction("py.invoke");  
				    intent.putExtra("pid", hid);
				    intent.putExtra("senderId", mStrRemoteId);
				    XposedModuleContext.getInstance().getAppContext().
				    sendBroadcast(intent);
			}
			else if(strContainHid.contains("discussion")){
				Intent intent = new Intent(); 
			    // ActionUtils.ACTION_EQUES_UPDATE_IP 与注册时保持一致
			    intent.setAction("py.invoke");  
			    intent.putExtra("pid", hid);
			    intent.putExtra("discussId", mStrDiscussId);
			    XposedModuleContext.getInstance().getAppContext().
			    sendBroadcast(intent);
			}
			else if(strContainHid.contains("group")){
				Intent intent = new Intent(); 
			    // ActionUtils.ACTION_EQUES_UPDATE_IP 与注册时保持一致
			    intent.setAction("py.invoke");  
			    intent.putExtra("pid", hid);
			    intent.putExtra("groupId", mStrGroupId);
			    XposedModuleContext.getInstance().getAppContext().
			    sendBroadcast(intent);
			}
			//groupId
			return true;
		}
		return false;
	}
	
	public void hook_OpenDatabase() {
		Class clz = XposedHelpers.findClass("com.d.m.a", mClassLoader);
		XposedHelpers.findAndHookMethod(clz, "openOrCreateDatabase",
				String.class, int.class, SQLiteDatabase.CursorFactory.class,
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						PyLog.log("openOrCreateDatabase 0 come");
					}
				});

		XposedHelpers.findAndHookMethod(clz, "openOrCreateDatabase",
				String.class, int.class, SQLiteDatabase.CursorFactory.class,
				DatabaseErrorHandler.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						PyLog.log("openOrCreateDatabase 1 come");
					}
				});
	}

	// private SQLiteDatabase(String arg2, int arg3, CursorFactory arg4,
	// DatabaseErrorHandler arg5)
	public void hook_Construstor() {
		// Class clz = XposedHelpers
		// .findClass(
		// "com.tencent.wcdb.database.SQLiteDatabase",
		// mClassLoader);
		XposedHelpers.findAndHookConstructor(
				"com.tencent.wcdb.database.SQLiteDatabase", mClassLoader,
				String.class, int.class, CursorFactory.class,
				DatabaseErrorHandler.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						// Object obj = param.args[0];
						PyLog.log(" Is use com.tencent.wcdb.database.SQLiteDatabase");
					}
				});

	}

	// processMKBridge(String arg3, String arg4, String arg5)
	public void hook_processMKBridge() {
		Class clz = XposedHelpers.findClass(
				"immomo.com.mklibrary.core.base.ui.MKWebView", mClassLoader);
		XposedHelpers.findAndHookMethod(clz, "processMKBridge", String.class,
				String.class, String.class, new XC_MethodHook() {
					// 修改参数3
					protected void beforeHookedMethod(MethodHookParam param)
							throws JSONException {
						String arg0 = (String) param.args[0];
						String arg1 = (String) param.args[1];
						String arg2 = (String) param.args[2];
						PyLog.log(arg0 + "  " + arg1 + "  " + arg2);
						// "{\"url\":\"https:";
//						if (arg2.contains("https://mk.immomo.com/hongbao/personal/grab")) {
//							JSONObject v0_1 = new JSONObject(arg2);
//							v0_1.put("callback",
//									"mm._callbacks.request.__BRIDGE_CALLBACK__3_1543851748048");
//							param.args[2] = v0_1.toString();
//							long l = System.currentTimeMillis();
//							// "{\"url\":\"https://mk.immomo.com/hongbao/personal/grab\",\"data\":{\"hid\":\"CWIMTRVTKPTYULGB2LJYH2XVKD3SKLUWULIKQL2RZNWWUN6ER4XQ\",\"sender\":\"663649121\",\"desc\":\"鎭枩鍙戣储\"},\"dataType\":\"json\",\"type\":\"POST\",\"method\":\"post\",\"callback\":\"mm._callbacks.request.__BRIDGE_CALLBACK__4_1543765253066\"}";
//							PyLog.log(arg2 + "Alter" + param.args[2]);
//						}
					};
				});
	}

	// arg2, "mk---" + arg3 immomo.com.mklibrary.core.utils
	// public static void b(String arg2, String arg3)
	public void hook_LogFun_b() {
		Class clz = XposedHelpers.findClass(
				"immomo.com.mklibrary.core.utils.e", mClassLoader);
		XposedHelpers.findAndHookMethod(clz, "b", String.class, String.class,
				new XC_MethodHook() {
					protected void beforeHookedMethod(MethodHookParam param) {
						String arg0 = (String) param.args[0];
						String arg1 = (String) param.args[1];
						PyLog.log(arg0 + "mk---" + arg1);
					};
				});
	}

	public void hook_setWebViewClient() {
		XposedHelpers.findAndHookMethod(WebView.class, "setWebViewClient",
				WebViewClient.class, new XC_MethodHook() {
					protected void beforeHookedMethod(MethodHookParam param) {
						String TAG = "setWebClientTrace";
						Log.d(TAG, Log.getStackTraceString(new Throwable()));
					};
				});
	}

	public void hook_setOnClickListener() {
		XposedHelpers.findAndHookMethod(View.class, "setOnClickListener",
				View.OnClickListener.class, new XC_MethodHook() {
					protected void beforeHookedMethod(MethodHookParam param) {
						View view = (View) param.thisObject;
						if (!(view instanceof Button)) {
							return;
						}
						int btnId = view.getId();
						String btnName = ((Button) view).getText().toString();
						PyLog.log(btnId + " " + btnName);
						PyLog.log(Log.getStackTraceString(new Throwable()));
					};
				});
	}

	// Class clz = XposedHelpers
	// .findClass(
	// "com.lakala.qcodeapp.module.home.fragment.HomePageFragment",
	// mClassLoader);
	// WebView obj;
	// obj.setWebViewClient(client)

	public Context getContext() {
		return mContext;
	}

	private XposedModule() {

	}

	private static XposedModule sInstance = new XposedModule();

	public static XposedModule get() {
		return sInstance;
	}

	private void hook_HomePageFragment() {
		Class clz = XposedHelpers.findClass(
				"com.lakala.qcodeapp.module.home.fragment.HomePageFragment",
				mClassLoader);

		XposedHelpers.findAndHookConstructor(clz, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				if (param.thisObject != null) {
					objHomePageFragment = param.thisObject;
					PyLog.log("objHomePageFragment = " + objHomePageFragment);
				}

			}
		});

	}

	private void hook_LKLTradeResult() {
		Class clz = XposedHelpers.findClass(
				"com.lakala.qcodeapp.module.trade.model.Imp.LKLTradeResult",
				mClassLoader);

		// final String strArrMethodName[] =
		// {"setCode","setTradeNo","setAmount","setCodeImage","setExpiresIn","setTradeTime",
		// "setOrderId","setPayAmt","setPayMode","setPayTime","setPayNo","setBatchNo","setSeqNo","setSrefno","setWeOrderNo",
		// "setLklOrderNo","setRetAmt","setRetTime","setNeedQuery"};
		final String strArrMethodName[] = { "setOrderId" };

		for (int i = 0; i < strArrMethodName.length; i++) {
			XposedHelpers.findAndHookMethod(clz, strArrMethodName[i],
					String.class, new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							PyLog.log(param.method + " = " + param.args[0]);
							PyLog.log_callback();
						}
					});
		}

		XposedHelpers.findAndHookMethod(clz, "getOrderId", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				PyLog.log(param.method + " = " + param.getResult());
				PyLog.log_callback();
			}
		});

	}

	private void hook_JSONObject() {
		PyLog.log("hook_JSONObject");

		XposedHelpers.findAndHookConstructor(JSONObject.class, String.class,
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {

						String jsonObject = "" + param.args[0];

						if (jsonObject.contains("orderId")) {
							PyLog.log("HookJSONObject.findAndHookConstructor");
							PyLog.log("HookJSONObject = " + jsonObject);
							PyLog.log_callback();
						}

					}
				});
	}

}
