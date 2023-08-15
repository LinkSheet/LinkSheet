package fe.linksheet.interconnect;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer a large list of Parcelable objects across an IPC.  Splits into
 * multiple transactions if needed.
 * Caveat: for efficiency and security, all elements must be the same concrete type.
 * In order to avoid writing the class name of each object, we must ensure that
 * each object is the same type, or else unparceling then reparceling the data may yield
 * a different result if the class name encoded in the Parcelable is a Base type.
 * See b/17671747.
 */
abstract class BaseParceledListSlice<T> implements Parcelable {
    private static final int MAX_IPC_SIZE;

    private List<T> mList;

    private int mInlineCountLimit = Integer.MAX_VALUE;

    private boolean mHasBeenParceled = false;

    public BaseParceledListSlice(List<T> list) {
        mList = list;
    }

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MAX_IPC_SIZE = IBinder.getSuggestedMaxIpcSizeBytes();
        } else {
            MAX_IPC_SIZE = 64 * 1024;
        }
    }

    BaseParceledListSlice(Parcel p, ClassLoader loader) {
        final int N = p.readInt();
        mList = new ArrayList<>(N);
        if (N <= 0) {
            return;
        }

        Parcelable.Creator<?> creator = readParcelableCreator(p, loader);
        Class<?> listElementClass = null;

        int i = 0;
        while (i < N) {
            if (p.readInt() == 0) {
                break;
            }
            listElementClass = readVerifyAndAddElement(creator, p, loader, listElementClass);
            i++;
        }
        if (i >= N) {
            return;
        }
        final IBinder retriever = p.readStrongBinder();
        while (i < N) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInt(i);
            try {
                retriever.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0);
            } catch (RemoteException e) {
                return;
            }
            while (i < N && reply.readInt() != 0) {
                listElementClass = readVerifyAndAddElement(creator, reply, loader,
                        listElementClass);
                i++;
            }
            reply.recycle();
            data.recycle();
        }
    }

    private Class<?> readVerifyAndAddElement(Parcelable.Creator<?> creator, Parcel p,
            ClassLoader loader, Class<?> listElementClass) {
        final T parcelable = readCreator(creator, p, loader);
        if (listElementClass == null) {
            listElementClass = parcelable.getClass();
        } else {
            verifySameType(listElementClass, parcelable.getClass());
        }
        mList.add(parcelable);
        return listElementClass;
    }

    private T readCreator(Parcelable.Creator<?> creator, Parcel p, ClassLoader loader) {
        if (creator instanceof Parcelable.ClassLoaderCreator<?>) {
            Parcelable.ClassLoaderCreator<?> classLoaderCreator =
                    (Parcelable.ClassLoaderCreator<?>) creator;
            return (T) classLoaderCreator.createFromParcel(p, loader);
        }
        return (T) creator.createFromParcel(p);
    }

    private static void verifySameType(final Class<?> expected, final Class<?> actual) {
        if (!actual.equals(expected)) {
            throw new IllegalArgumentException("Can't unparcel type "
                    + (actual == null ? null : actual.getName()) + " in list of type "
                    + (expected == null ? null : expected.getName()));
        }
    }

    public List<T> getList() {
        return mList;
    }

    /**
     * Set a limit on the maximum number of entries in the array that will be included
     * inline in the initial parcelling of this object.
     */
    public void setInlineCountLimit(int maxCount) {
        mInlineCountLimit = maxCount;
    }

    /**
     * Write this to another Parcel. Note that this discards the internal Parcel
     * and should not be used anymore. This is so we can pass this to a Binder
     * where we won't have a chance to call recycle on this.
     * This method can only be called once per BaseParceledListSlice to ensure that
     * the referenced list can be cleaned up before the recipient cleans up the
     * Binder reference.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mHasBeenParceled) {
            throw new IllegalStateException("Can't Parcel a ParceledListSlice more than once");
        }
        mHasBeenParceled = true;
        final int N = mList.size();
        final int callFlags = flags;
        dest.writeInt(N);
        if (N > 0) {
            final Class<?> listElementClass = mList.get(0).getClass();
            writeParcelableCreator(mList.get(0), dest);
            int i = 0;
            while (i < N && i < mInlineCountLimit && dest.dataSize() < MAX_IPC_SIZE) {
                dest.writeInt(1);

                final T parcelable = mList.get(i);
                verifySameType(listElementClass, parcelable.getClass());
                writeElement(parcelable, dest, callFlags);

                i++;
            }
            if (i < N) {
                dest.writeInt(0);
                Binder retriever = new Binder() {
                    @Override
                    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                            throws RemoteException {
                        if (code != FIRST_CALL_TRANSACTION) {
                            return super.onTransact(code, data, reply, flags);
                        } else if (mList == null) {
                            throw new IllegalArgumentException("Attempt to transfer null list, "
                                    + "did transfer finish?");
                        }
                        int i = data.readInt();

                        while (i < N && reply.dataSize() < MAX_IPC_SIZE) {
                            reply.writeInt(1);

                            final T parcelable = mList.get(i);
                            verifySameType(listElementClass, parcelable.getClass());
                            writeElement(parcelable, reply, callFlags);

                            i++;
                        }
                        if (i < N) {
                            reply.writeInt(0);
                        } else {
                            mList = null;
                        }
                        return true;
                    }
                };
                dest.writeStrongBinder(retriever);
            }
        }
    }

    protected abstract void writeElement(T parcelable, Parcel reply, int callFlags);

    protected abstract void writeParcelableCreator(T parcelable, Parcel dest);

    protected abstract Parcelable.Creator<?> readParcelableCreator(Parcel from, ClassLoader loader);
}