package kidnox.particles.util;

import android.util.Log;

import kidnox.particles.BuildConfig;

public class DebugUtil {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static void verbose(String message) {
        if(DEBUG) Log.v("GL", message);
    }

    public static void verbose(String message, Object... args) {
        if(DEBUG) Log.v("GL", String.format(message, args));
    }

    public static void log(String message) {
        if(DEBUG) Log.d("GL", message);
    }

    public static void warn(String message) {
        if(DEBUG) Log.w("GL", message);
    }
}
