package android.content.pm.verify.domain;

import android.annotation.UserIdInt;
import android.content.Context;
import android.content.Intent;
import android.os.*;

import androidx.annotation.*;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import fe.hidden.HiddenStub;

@RequiresApi(Build.VERSION_CODES.S)
public interface IDomainVerificationManager extends IInterface {
    /**
     * Retrieve the user state for the given package and the user.
     *
     * @param packageName The app to query state for.
     * @return The user selection verification data for the given package for the user, or null if
     * the package does not declare any HTTP/HTTPS domains.
     */
    @Nullable
    DomainVerificationUserState getDomainVerificationUserState(String packageName, @UserIdInt int userId);

    /**
     * Change whether the given packageName is allowed to handle BROWSABLE and DEFAULT category web
     * (HTTP/HTTPS) {@link Intent} Activity open requests. The final state is determined along with
     * the verification status for the specific domain being opened and other system state. An app
     * with this enabled is not guaranteed to be the sole link handler for its domains.
     * <p>
     * By default, all apps are allowed to open links. Users must disable them explicitly.
     */
    void setDomainVerificationLinkHandlingAllowed(String packageName, boolean allowed, @UserIdInt int userId);

    /**
     * Update the recorded user selection for the given {@param domains} for the given {@param
     * domainSetId}. This state is recorded for the lifetime of a domain for a package on device,
     * and will never be reset by the system short of an app data clear.
     * <p>
     * This state is stored per device user. If another user needs to be changed, the appropriate
     * permissions must be acquired and {@link Context#createContextAsUser(UserHandle, int)} should
     * be used.
     * <p>
     * Enabling an unverified domain will allow an application to open it, but this can only occur
     * if no other app on the device is approved for a higher approval level. This can queried
     * using {@link #getOwnersForDomain(String)}.
     *
     * If all owners for a domain are {@link DomainOwner#isOverrideable()}, then calling this to
     * enable that domain will disable all other owners.
     *
     * On the other hand, if any of the owners are non-overrideable, then this must be called with
     * false for all of the other owners to disable them before the domain can be taken by a new
     * owner.
     *
     * @param domainSetId See {@link DomainVerificationInfo#getIdentifier()}.
     * @param domains     The domains to toggle the state of.
     * @param enabled     Whether or not the app should automatically open the domains specified.
     * @return error code or {@link #STATUS_OK} if successful
     */
    int setDomainVerificationUserSelection(String domainSetId, @NonNull DomainSet domains, boolean enabled, @UserIdInt int userId);


    /**
     * Change the verification status of the {@param domains} of the package associated with {@param
     * domainSetId}.
     *
     * @param domainSetId See {@link DomainVerificationInfo#getIdentifier()}.
     * @param domains     List of host names to change the state of.
     * @param state       See {@link DomainVerificationInfo#getHostToStateMap()}.
     * @return error code or {@link #STATUS_OK} if successful
     * @throws NameNotFoundException If the ID is known to be good, but the package is
     *                               unavailable. This may be because the package is installed on
     *                               a volume that is no longer mounted. This error is
     *                               unrecoverable until the package is available again, and
     *                               should not be re-tried except on a time scheduled basis.
     */
    int setDomainVerificationStatus(@NonNull UUID domainSetId, @NonNull Set<String> domains, int state);


    /**
     * For the given domain, return all apps which are approved to open it in a
     * greater than 0 priority. This does not mean that all apps can actually open
     * an Intent with that domain. That will be decided by the set of apps which
     * are the highest priority level, ignoring all lower priority levels.
     *
     * The set will be ordered from lowest to highest priority.
     *
     * @param domain The host to query for. An invalid domain will result in an empty set.
     */
    SortedSet<DomainOwner> getOwnersForDomain(@NonNull String domain);

    /**
     * Retrieves the domain verification state for a given package.
     *
     * @return the data for the package, or null if it does not declare any autoVerify domains
     * @throws NameNotFoundException If the package is unavailable. This is an unrecoverable error
     *                               and should not be re-tried except on a time scheduled basis.
     */
    DomainVerificationInfo getDomainVerificationInfo(@NonNull String packageName);

    abstract class Stub extends Binder implements IDomainVerificationManager {
        public static IDomainVerificationManager asInterface(IBinder obj) {
            return HiddenStub.throwException();
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }
    }
}
