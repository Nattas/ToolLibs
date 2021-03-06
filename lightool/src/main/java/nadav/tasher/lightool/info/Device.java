package nadav.tasher.lightool.info;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Build;
import android.view.WindowManager;

public class Device {
    public static boolean isOnline(Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager != null) && manager.getActiveNetworkInfo() != null;
    }

    public static boolean isInstalled(Context con, String packageName) {
        try {
            con.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static int getVersionCode(Context con, String packagename) {
        try {
            return con.getPackageManager().getPackageInfo(packagename, PackageManager.GET_ACTIVITIES).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context con, String packagename) {
        try {
            return con.getPackageManager().getPackageInfo(packagename, PackageManager.GET_ACTIVITIES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isJobServiceScheduled(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            JobScheduler scheduler = context.getSystemService(JobScheduler.class);
            boolean hasBeenScheduled = false;
            if (scheduler != null) {
                for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                    if (jobInfo.getId() == id) {
                        hasBeenScheduled = true;
                        break;
                    }
                }
            }
            return hasBeenScheduled;
        } else {
            return false;
        }
    }

    public static int screenX(Context con) {
        WindowManager windowManager = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);
            return p.x;
        }
        return 0;
    }

    public static int screenY(Context con) {
        WindowManager windowManager = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);
            return p.y;
        }
        return 0;
    }
}
