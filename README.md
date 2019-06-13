**Android Accessibility Demo**

Accessibility service can be used in APPs that collect features for digital phenotyping.
This demo is used to explore and test what information Android assessibility service API can capture on screen touches and events.
You need to look at the logcat for all message logs.

The UI uses accessibility overlay, it presents 5 buttons:
- SHOW/HIDE: toggles show/hide the panel
- -: decrement verbosity
- [0-3]: the current verbosity level, click to toggle message logging
- +: increment verbosity
- EXIT: disable the accessibility service
- long-hold [0-3] : toggles the individual event selection dropdown list
- long-hold + : enables showing details
- long-hold - : disables showing details

Notes:
- when "show-details" is enabled, the entire active window info will be printed out recursively
- when the middle button displays an asterisk(\*), only checkbox-enabled events are captured

