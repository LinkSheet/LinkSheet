package fe.linksheet.interconnect;

import fe.linksheet.interconnect.StringParceledListSlice;

interface ILinkSheetService {
    StringParceledListSlice getSelectedDomains(String packageName) = 1;
}