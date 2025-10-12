package android.app;

import dev.rikka.tools.refine.RefineAs;
import fe.hidden.HiddenStub;

@RefineAs(AppOpsManager.class)
public class AppOpsManagerHidden {

    public int checkOp(int op, int uid, String packageName) {
        return HiddenStub.throwException();
    }
}
