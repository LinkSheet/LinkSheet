/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.pm.verify.domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import fe.hidden.HiddenStub;

import java.util.Set;

/**
 * Wraps an input set of domains from the client process, to be sent to the server. Handles cases
 * where the data size is too large by writing data using {@link Parcel#writeBlob(byte[])}.
 */
public class DomainSet implements Parcelable {
    public DomainSet(@NonNull Set<String> domains) {
        HiddenStub.throwException();
    }

    public @NonNull Set<String> getDomains() {
        return HiddenStub.throwException();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        HiddenStub.throwException();
    }
    @Override
    public int describeContents() {
        return HiddenStub.throwException();
    }

    public static final Creator<DomainSet> CREATOR = HiddenStub.creator();
}
