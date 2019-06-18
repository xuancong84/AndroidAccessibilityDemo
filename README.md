**Android Accessibility Demo**

Accessibility service can be used in APPs that collect features for digital phenotyping.
This demo is used to explore and test what information Android assessibility service API can capture on screen touches and events.
You need to look at the logcat for all message logs.

The UI uses accessibility overlay, it presents 5 buttons:
- Button 1 (SHOW/HIDE)
  -  short-click: toggle show/hide the panel
  -  long-click: toggle on-screen logcat display
- Button 2 (-)
  -  short-click: decrement verbosity
  -  long-click: disable showing details
- Button 3 (#: the current verbosity level)
  -  short-click: toggle message logging
  -  long-click: toggle event-selection dropdown menu
- Button 4 (+)
  -  short-click: increment verbosity
  -  long-click: enable showing details
- Button 5 (EXIT)
  -  short-click: disable the accessibility service
  -  long-click: toggle the display of own events
- on-screen logcat display scroll TextView
  -  long-click: clear logcat content
- event-selection dropdown menu
  -  long-click: toggle all checkboxes

Notes:
- when "show-details" is enabled, the entire active window info will be printed out recursively
- when the middle button displays an asterisk(\*), only checkbox-enabled events are captured

