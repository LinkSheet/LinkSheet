package dev.zwander.shared;

import dev.zwander.shared.data.VerifyResult;

interface IShizukuService {
    void destroy() = 16777114;
    VerifyResult verifyLinks(int sdk, String packageName) = 1;
}