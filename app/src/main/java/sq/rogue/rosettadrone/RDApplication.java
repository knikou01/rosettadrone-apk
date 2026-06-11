package sq.rogue.rosettadrone;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.secneo.sdk.Helper;

import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.camera.Camera;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;

public class RDApplication extends Application {

    public static boolean useMavLink2 = true; // MAVSDK only speaks MAVLink 2
    private static DJISimulatorApplication simulatorApplication;
    private static boolean isSimulator = false;
    public static boolean isTestMode = false;

    public static boolean getSim() {
        return isSimulator;
    }

    public static void setSim(boolean sim) {
        isSimulator = sim;
    }

    public static synchronized BaseProduct getProductOrDummy() {
        if (isTestMode) {
            return MainActivity.createDummyProduct();
        } else {
            return DJISDKManager.getInstance().getProduct();
        }
    }

    public static void startLoginApplication() {
        simulatorApplication.onCreate();
    }

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        // Use early (plain) key read — EncryptedSharedPreferences not available yet here
        injectDjiKeyEarly(paramContext);
        Helper.install(RDApplication.this);
        if (simulatorApplication == null) {
            simulatorApplication = new DJISimulatorApplication();
            simulatorApplication.setContext(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isSimulator = false;
        injectGoogleKey(this);
    }

    private static void injectDjiKeyEarly(Context context) {
        String key = KeyStore.INSTANCE.getDjiKeyEarly(context);
        Log.d("RDApplication", "Injecting DJI key (early): " + (key != null ? "found" : "null"));
        if (key == null) return;
        try {
            android.content.pm.ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            android.content.pm.PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                appInfo.metaData.putString("com.dji.sdk.API_KEY", key);
                Log.d("RDApplication", "DJI key injected successfully");
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            Log.e("RDApplication", "Failed to inject DJI key", e);
        }
    }

    private static void injectGoogleKey(Context context) {
        String key = KeyStore.INSTANCE.getGoogleKey(context);
        Log.d("RDApplication", "Injecting Google key: " + (key != null ? "found" : "null"));
        if (key == null) return;
        try {
            android.content.pm.ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            android.content.pm.PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                appInfo.metaData.putString("com.google.android.geo.API_KEY", key);
                Log.d("RDApplication", "Google key injected successfully");
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            Log.e("RDApplication", "Failed to inject Google key", e);
        }
    }

    public static synchronized Camera getCameraInstance() {
        if (isTestMode) return null;
        BaseProduct product = getProductOrDummy();
        if (product == null) return null;
        Camera camera = null;
        if (product instanceof Aircraft) {
            camera = product.getCamera();
        } else if (product instanceof HandHeld) {
            camera = product.getCamera();
        }
        return camera;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return super.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            return super.registerReceiver(receiver, filter);
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((flags & (Context.RECEIVER_EXPORTED | Context.RECEIVER_NOT_EXPORTED)) == 0) {
                flags |= Context.RECEIVER_NOT_EXPORTED;
            }
        }
        return super.registerReceiver(receiver, filter, flags);
    }
}