package com.xstd.pirvatephone.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtils {

	public static List<PackageInfo> getAllInstallApp(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm
				.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		List<PackageInfo> datas = new ArrayList<PackageInfo>();
		for (PackageInfo packageInfo : packages) {
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					|| (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				datas.add(packageInfo);
			}
		}
		return datas;
	}
}
