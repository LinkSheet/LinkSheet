package dev.zwander.shared;

import dev.zwander.shared.data.VerifyResult;

interface IShizukuService {
    void destroy() = 16777114;
    int setDomainState(String packageName, String domain, int state) = 1;
}
