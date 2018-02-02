package teampanther.developers.easyextractor.UtilsHelper;

import android.util.Log;

import java.io.Closeable;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by luffynando on 28/01/2018.
 */

public class Closer {
    // closeAll()
    public static void closeSilently(Object... xs) {
        // Note: on Android API levels prior to 19 Socket does not implement Closeable
        for (Object x : xs) {
            if (x != null) {
                try {
                    Log.d("closing: ",x.toString());
                    if (x instanceof Closeable) {
                        ((Closeable)x).close();
                    } else if (x instanceof Socket) {
                        ((Socket)x).close();
                    } else if (x instanceof DatagramSocket) {
                        ((DatagramSocket)x).close();
                    } else {
                        Log.d("cannot close: ",x.toString());
                        throw new RuntimeException("cannot close "+x);
                    }
                } catch (Throwable e) {
                    Log.getStackTraceString(e);
                }
            }
        }
    }
}
