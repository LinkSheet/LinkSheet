package dev.zwander.shared;

import dev.zwander.shared.data.VerifyResult;

interface IShizukuService {
    void destroy() = 16777114;
    int disableLinkHandling(String packageName, boolean enabled) = 1;
}