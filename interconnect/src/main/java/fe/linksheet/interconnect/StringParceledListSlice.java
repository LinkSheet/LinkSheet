package fe.linksheet.interconnect;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

/**
 * Transfer a large list of Parcelable objects across an IPC.  Splits into
 * multiple transactions if needed.
 *
 * @see BaseParceledListSlice
 */
public class StringParceledListSlice extends BaseParceledListSlice<String> {
    public StringParceledListSlice(List<String> list) {
        super(list);
    }

    private StringParceledListSlice(Parcel in, ClassLoader loader) {
        super(in, loader);
    }

    public static StringParceledListSlice emptyList() {
        return new StringParceledListSlice(Collections.emptyList());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    protected void writeElement(String parcelable, Parcel reply, int callFlags) {
        reply.writeString(parcelable);
    }

    @Override
    protected void writeParcelableCreator(String parcelable, Parcel dest) {}

    @Override
    protected Parcelable.Creator<?> readParcelableCreator(Parcel from, ClassLoader loader) {
        return Parcel.STRING_CREATOR;
    }

    public static final Parcelable.ClassLoaderCreator<StringParceledListSlice> CREATOR =
            new Parcelable.ClassLoaderCreator<StringParceledListSlice>() {
        public StringParceledListSlice createFromParcel(Parcel in) {
            return new StringParceledListSlice(in, null);
        }

        @Override
        public StringParceledListSlice createFromParcel(Parcel in, ClassLoader loader) {
            return new StringParceledListSlice(in, loader);
        }

        @Override
        public StringParceledListSlice[] newArray(int size) {
            return new StringParceledListSlice[size];
        }
    };
}