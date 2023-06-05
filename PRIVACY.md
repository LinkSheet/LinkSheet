# Privacy

LinkSheet does not track the user in any way. LinkSheet always uses common browser headers and never
includes device information when sending web-requests, but due to the nature of the internet, your
public IP address is not hidden.

## Features

### Follow redirects

When the "Follow redirects" feature is enabled, LinkSheet attempts to follow redirects before
showing the app chooser by sending a `HEAD` request to the opened URL (which means a connection to
the website is always made, regardless of whether the user actually opens the URL in any app
afterwards).

When "Follow redirects" and "Follow redirects via external service" are enabled, URLs are not
resolved on device, but rather sent to
a [Supabase edge-function](https://github.com/1fexd/supabase-redirect-edge-function/) which then
attempts to follow the redirects and returns the destination URL. The edge function itself does not
log any request information (it only stores the timestamp, the initial URL and its destination URL),
but [Supabase](https://supabase.com) may log request
information ([Privacy Policy](https://supabase.com/privacy))

### Downloader

When the "Enable downloader" feature is enabled, LinkSheet will send a `HEAD` request to the opened
URL (again, a connection is always made, even if the user does not actually open the URL in any app
afterwards) and check if the `Content-Type` header of the response is not `text/html`.

Enabling "Use mime type from URL" will still send a request if no mime type could be read from the
opened URL

## Logs

### Crash log viewer

The crash log viewer displays exceptions which could not be handled by the application. Usually,
exception messages do not contain any personal data and can safely be shared with others.

### Internal log viewer

The internal log viewer (available from version `0.0.32` onwards) will default to exporting a
redacted version of the log. In the redacted version, personal identifiers like package names or
hosts are hashed with `HmacSHA256` (a random key is generated when the app is launched for the first
time). This approach ensures privacy while still allowing debugging. 

### ADB logcat

ADB logcats of LinkSheet may include personal identifiable information like installed packages or
visited hosts as well as preferences. Logcats should only be shared in rare cases where a full log
is required to debug an issue. Logcats should not be published publically.