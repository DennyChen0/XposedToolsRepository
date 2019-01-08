package dc.reverse.mod;

import dc.utils.PyLog;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedEntry implements IXposedHookLoadPackage{

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.immomo.momo")) {
			PyLog.log("processName = " + lpparam.processName);
			PackageMetaInfo pminfo = PackageMetaInfo.fromXposed(lpparam);
			XposedModuleContext.getInstance().initAndHook(pminfo);	
		}
	}
}
