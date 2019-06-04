// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.android.globalactionbarservice;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Button;
import android.widget.FrameLayout;

public class GlobalActionBarService extends AccessibilityService
    implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d("Gesture2","onDown: " + event.toString());
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d("Gesture2", "onFling: " + event1.toString() + event2.toString());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d("Gesture2", "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d("Gesture2", "onScroll: " + event1.toString() + event2.toString());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d("Gesture2", "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d("Gesture2", "onSingleTapUp: " + event.toString());
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d("Gesture2", "onDoubleTap: " + event.toString());
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d("Gesture2", "onDoubleTapEvent: " + event.toString());
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d("Gesture2", "onSingleTapConfirmed: " + event.toString());
        return false;
    }

    public static String removeTrivialFields(String S){
        if(S==null)
            return "";
        String [] its = S.split(";");
        for(int x=0, X=its.length; x<X; ++x){
            String [] kv = its[x].split(":");
            if(kv.length != 2) continue;
            String kv1 = kv[1].trim();
            switch(kv1){
                case "[]":
                case "-1":
                case "null":
                    its[x] = "";
                    break;
            }
            if(kv1.startsWith("null"))
                its[x] = kv1.substring(4);
        }
        return String.join(";", its);
    }

    public static String CS2S(CharSequence seq){
        return (seq==null?"":(String)seq);
    }

    public static String traverseWindowInfo(AccessibilityWindowInfo info) {
        if(info==null)
            return "windowInfo=null";
        String ret = CS2S(info.getTitle());
        for(int x=0, X=info.getChildCount(); x<X; ++x)
            ret += (x==0?"=(":"+(")+ traverseWindowInfo(info.getChild(x))+")";
        return ret;
    }

    public String traverseNodeInfo(AccessibilityNodeInfo info) {
        if(info==null)
            return "nodeInfo=null";
        String ret = "CLS="+CS2S(info.getClassName())+",PKG="+CS2S(info.getPackageName())
            +",Text="+CS2S(info.getText())+",";
        for(int x=0, X=info.getChildCount(); x<X; ++x) {
            try {
                AccessibilityNodeInfo child = info.getChild(x);
                ret += (x == 0 ? "=(" : "+(") + traverseNodeInfo(child) + ")";
            }catch (Exception e){
                ret += (x == 0 ? "=(" : "+(") + "exception)";
            }
        }
        return ret;
    }

    public static AccessibilityNodeInfo getRoot(AccessibilityNodeInfo info){
        while(info.getParent()!=null)
            info = info.getParent();
        return info;
    }

    public static void logi(String tag, String s){
        for(int x=0, X=s.length()/4000; x<=X; ++x){
            if(x==X)
                Log.i(tag, s.substring(x*4000));
            else
                Log.i(tag, s.substring(x*4000, (x+1)*4000));
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(listen) {
            switch (event.getEventType()) {
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    if(level<2)break;
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    if(level<1)break;
                    Log.i("Gesture:" + AccessibilityEvent.eventTypeToString(event.getEventType()),
                        removeTrivialFields(event.toString()));
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    logi("Gesture:nodeTree", traverseNodeInfo(nodeInfo));
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                case AccessibilityEvent.TYPE_VIEW_SELECTED:
                case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                    if(level<3)break;
                default:
                    Log.i("Gesture:onAccessibilityEvent", removeTrivialFields(event.toString()));
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.i("Gesture","location03");
    }

    @Override
    protected boolean onGesture(int gestureId){
        Log.i("Gesture","location04");
        super.onGesture(gestureId);
        return true;
    }

    @Override
    protected boolean onKeyEvent (KeyEvent event){
        Log.i("Gesture","location05");
        return false;
    }

    FrameLayout mLayout;
    public int level;
    public boolean show, listen;

    protected void onServiceConnected() {
        super.onServiceConnected();
        level = 0;
        show = listen = true;

        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);

        // 1. show-hide button
        Button hideButton = (Button) mLayout.findViewById(R.id.hide);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show = !show;
                int visibility = (show?View.VISIBLE:View.GONE);
                ((Button)mLayout.findViewById(R.id.more)).setVisibility(visibility);
                ((Button)mLayout.findViewById(R.id.less)).setVisibility(visibility);
                ((Button)mLayout.findViewById(R.id.exit)).setVisibility(visibility);
                ((Button)mLayout.findViewById(R.id.level)).setVisibility(visibility);
                ((Button)mLayout.findViewById(R.id.hide)).setText(show?R.string.hide:R.string.show);
            }
        });

        // 2. less button
        Button lessButton = (Button) mLayout.findViewById(R.id.less);
        lessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                --level;
                level = (level<0?0:level);
                ((Button)mLayout.findViewById(R.id.level)).setText(""+level);
            }
        });

        // 3. level button
        Button levelButton = (Button) mLayout.findViewById(R.id.level);
        levelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen = !listen;
                ((Button)mLayout.findViewById(R.id.level)).setTextColor(listen?0xff000000:0xff7f7f7f);
            }
        });

        // 4. more button
        Button moreButton = (Button) mLayout.findViewById(R.id.more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++level;
                level = (level>3?3:level);
                ((Button)mLayout.findViewById(R.id.level)).setText(""+level);
            }
        });

        // 5. exit button
        Button exitButton = (Button) mLayout.findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableSelf();
            }
        });

//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.flags = AccessibilityServiceInfo.DEFAULT
//            | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
//            | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
//            | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
//            | AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
//            | AccessibilityServiceInfo.FLAG_REQUEST_FINGERPRINT_GESTURES
//            | AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
//            | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        setServiceInfo(info);
    }
}
