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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

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
        if(!listen)
            return;
        if( level > 0 ){
            switch (event.getEventType()) {
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    if(level<2)break;
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                    if(level<3)break;
                    Log.i("Gesture:" + AccessibilityEvent.eventTypeToString(event.getEventType()),
                        removeTrivialFields(event.toString()));
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    logi("Gesture:nodeTree", traverseNodeInfo(nodeInfo));
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                case AccessibilityEvent.TYPE_VIEW_SELECTED:
                case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                    if(level<4)break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                default:
                    Log.i("Gesture:onAccessibilityEvent", removeTrivialFields(event.toString()));
            }
        } else {
            int event_type = event.getEventType();
            for( int x=0; x<event_type_list.length; ++x ){
                if( event_state_list[x] && event_type==event_type_list[x] ){
                    logi("Gesture:onAccessibilityEvent", event.toString());
                    if(show_details) {
                        AccessibilityNodeInfo nodeInfo = event.getSource();
                        logi("Gesture:nodeInfo", nodeInfo!=null?"null":nodeInfo.toString());
                        logi("Gesture:nodeTree", traverseNodeInfo(getRootInActiveWindow()));
                    }
                }
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

    public static int [] event_type_list = {
        AccessibilityEvent.TYPE_VIEW_CLICKED,
        AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
        AccessibilityEvent.TYPE_VIEW_SELECTED,
        AccessibilityEvent.TYPE_VIEW_FOCUSED,
        AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
        AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED,
        AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY,
        AccessibilityEvent.TYPE_VIEW_SCROLLED,
        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
        AccessibilityEvent.TYPE_WINDOWS_CHANGED,
        AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED,
        AccessibilityEvent.TYPE_VIEW_HOVER_ENTER,
        AccessibilityEvent.TYPE_VIEW_HOVER_EXIT,
        AccessibilityEvent.TYPE_ANNOUNCEMENT,
        AccessibilityEvent.TYPE_TOUCH_INTERACTION_START,
        AccessibilityEvent.TYPE_TOUCH_INTERACTION_END,
        AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START,
        AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END,
        AccessibilityEvent.TYPE_GESTURE_DETECTION_START,
        AccessibilityEvent.TYPE_GESTURE_DETECTION_END
    };
    public boolean event_state_list [];

    FrameLayout mLayout;
    public int level;
    public boolean show, listen, show_list, show_details;
    private LinearLayout layout;

    private void setLevelButton(){
        ((Button)mLayout.findViewById(R.id.level)).setText(level<0?"*":(""+level));
        ((Button)mLayout.findViewById(R.id.level)).setTextColor(listen?(show_details?0xffff0000:0xff000000):0xff7f7f7f);
    }

    protected void onServiceConnected() {
        super.onServiceConnected();
        level = 1;
        show = listen = true;
        show_list = show_details = false;
        event_state_list = new boolean [event_type_list.length];
        Arrays.fill(event_state_list, false);

        // Part 1: Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService( WINDOW_SERVICE );
        mLayout = new FrameLayout(this );
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

        // Part 2: Create invisible linear layout for checkbox
        layout = new LinearLayout(this);
        layout.setOrientation( LinearLayout.VERTICAL );
        layout.setVisibility( show_list ?LinearLayout.VISIBLE:LinearLayout.GONE );
        layout.setBackgroundColor(0xeeffffff);
        lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layout.setLayoutParams(lp);

        for ( int x=0; x<event_type_list.length; ++x ){
            int event_type = event_type_list[x];
            CheckBox cb = new CheckBox(this );
            cb.setText( AccessibilityEvent.eventTypeToString( event_type ) );
            cb.setScaleX( 0.8f );
            cb.setScaleY( 0.8f );
            cb.setTextScaleX( 0.8f );
            cb.setId( x );
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    event_state_list[buttonView.getId()] = isChecked;
                    level = (level>0?-level:level);
                    setLevelButton();
                }
            });
            layout.addView( cb );
        }
        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.addView(layout);
        ((LinearLayout)mLayout.getChildAt(0 )).addView(scrollView);


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
                layout.setVisibility(show_list ?visibility:View.GONE);
            }
        });

        // 2. less button
        final Button lessButton = (Button) mLayout.findViewById(R.id.less);
        lessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( level < 0 )
                    level = -level;
                else {
                    --level;
                    level = (level < 1 ? 1 : level);
                }
                show_list = false;
                setLevelButton();
                layout.setVisibility(View.GONE);
            }
        });
        lessButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(),"Details will NOT be shown",Toast.LENGTH_SHORT).show();
                show_details = false;
                setLevelButton();
                return true;
            }
        });

        // 3. level button
        final Button levelButton = (Button) mLayout.findViewById(R.id.level);
        levelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen = !listen;
                setLevelButton();
            }
        });
        levelButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                show_list = !show_list;
                layout.setVisibility(show_list?View.VISIBLE:View.GONE);
                return true;
            }
        });

        // 4. more button
        Button moreButton = (Button) mLayout.findViewById(R.id.more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( level < 0 )
                    level = -level;
                else {
                    ++level;
                    level = (level > 4 ? 4 : level);
                }
                show_list = false;
                setLevelButton();
                layout.setVisibility(View.GONE);
            }
        });
        moreButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(),"Details will be shown",Toast.LENGTH_SHORT).show();
                show_details = true;
                setLevelButton();
                return true;
            }
        });

        // 5. exit button
        Button exitButton = (Button) mLayout.findViewById( R.id.exit );
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableSelf();
            }
        });

        // initialize UI
        ((Button)mLayout.findViewById(R.id.level)).setText(""+level);
    }
}
