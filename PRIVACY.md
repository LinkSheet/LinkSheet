# Privacy

LinkSheet does not track the user in any way. LinkSheet always uses common browser headers and never
includes device information when sending web-requests, but due to the nature of the internet, your
public IP address is not hidden.

## Features connecting to the internet locally

### Follow redirects

When the "Follow redirects" feature is enabled, LinkSheet attempts to follow redirects before
showing the app chooser by sending a `HEAD` request to the opened URL (which means a connection to
the website is always made, regardless of whether the user actually opens the URL in any app
afterwards).

### Amp2Html

When "Enable Amp2Html" is enabled, LinkSheet will send a `GET` request to the opened URL (again, a
connection is always made, even if the user does not actually open the URL in any app afterwards) to
attempt to obtain the non-AMP version of the page. If the page is not an AMP page, or if no non-AMP
version could be found, the original URL will be opened when the user clicks an app in the bottom
sheet.

### Downloader

When the "Enable downloader" feature is enabled, LinkSheet will send a `HEAD` request to the opened
URL (again, a connection is always made, even if the user does not actually open the URL in any app
afterwards) and check if the `Content-Type` header of the response is not `text/html`.

Enabling "Use mime type from URL" will still send a request if no mime type could be read from the
opened URL

## External services

Both "Follow redirects" and "Amp2Html" are also available as external services, meaning that,
when the corresponding option is enabled ("Follow redirects via external service" and
"Attempt to obtain non-AMP page version via external service" respectively),
a [Supabase edge-function](https://github.com/1fexd/linksheet-supabase-functions/) is used to follow
redirects or convert an AMP page to the normal HTML page. The functions only cache the timestamp,
input and output link.

When "Follow redirects" and "Follow redirects via external service" are enabled, only
links [known to be trackers (checkout the *.txt files)](https://github.com/1fexd/fastforward-ext/releases/latest)
will be sent to the edge function.

If "Enable Amp2Html" and "Attempt to obtain non-AMP page version via external service", ALL links
are sent to the edge function.

## Logs

### Crash log viewer

The crash log viewer displays exceptions which could not be handled by the application. Usually,
exception messages do not contain any personal data and can safely be shared with others.

### Internal log viewer

The internal log viewer (available from version `0.0.32` onwards) will default to exporting a
redacted version of the log. In the redacted version, personal identifiers like package names or
hosts are hashed with `HmacSHA256` (a random key is generated when the app is launched for the first
time). This approach ensures privacy while still allowing debugging.

### Other log content

The log will contain the LinkSheet version.

Additionally, both logs will include the device fingerprint by default. This fingerprint
contains the
device brand, device
name, Android version, Android build id, Android version incremental id, user type (e.g. user) and
tags (e.g. release-keys). While this fingerprint may be considered personal data, it may be
incredibly useful while debugging issues.

Settings are also included in both logs by default since they may also help a lot while debugging
issues. While they do not include any personal data (settings containing package names are obscured
when the log is exported in redacted mode), exporting them can still be disabled.

## ADB logcat

ADB logcats of LinkSheet may include personal identifiable information like installed packages or
visited hosts as well as preferences. Logcats should only be shared in rare cases where a full log
is required to debug an issue. Logcats should not be published publicly.
