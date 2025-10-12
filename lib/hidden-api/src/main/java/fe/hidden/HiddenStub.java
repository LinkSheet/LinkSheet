package fe.hidden;

import android.os.Parcelable;

public class HiddenStub {
    public static <T> T throwException() {
        throw new UnsupportedOperationException();
    }
    public static <T> Parcelable.Creator<T> creator() {
        return throwException();
    }
}
