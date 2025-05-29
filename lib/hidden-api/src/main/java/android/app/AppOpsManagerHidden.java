package android.app;

import dev.rikka.tools.refine.RefineAs;
import fe.hidden.Stub;

@RefineAs(AppOpsManager.class)
public class AppOpsManagerHidden {

    public int checkOp(int op, int uid, String packageName) {
        return Stub.throwException();
    }
}
