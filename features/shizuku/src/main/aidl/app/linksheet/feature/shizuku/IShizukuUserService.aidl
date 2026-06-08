package app.linksheet.feature.shizuku;

interface IShizukuUserService {
    void destroy() = 16777114;
    int reset(in @nullable String packageName) = 0;
    int verify(in @nullable String packageName) = 1;
}
