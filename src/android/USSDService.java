package com.ramymokako.plugin.ussd.android;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;

public class USSDService extends AccessibilityService {
    private static String TAG = USSDService.class.getSimpleName();
    private static AccessibilityEvent event;

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        event = accessibilityEvent;
        Log.d(TAG, String.format("onAccessibilityEvent:  [class] %s  [text] %s", new Object[]{accessibilityEvent.getClassName(), accessibilityEvent.getText()}));
        if (USSDController.instance != null && USSDController.instance.isRunning.booleanValue()) {
            if (LoginView(accessibilityEvent) && notInputText(accessibilityEvent)) {
                clickOnButton(accessibilityEvent, 0);
                USSDController.instance.isRunning = false;
                USSDController.instance.callbackInvoke.over(((CharSequence) accessibilityEvent.getText().get(0)).toString());
            } else if (problemView(accessibilityEvent) || LoginView(accessibilityEvent)) {
                clickOnButton(accessibilityEvent, 1);
                USSDController.instance.callbackInvoke.over(((CharSequence) accessibilityEvent.getText().get(0)).toString());
            } else if (isUSSDWidget(accessibilityEvent)) {
                try {
                    String charSequence = ((CharSequence) accessibilityEvent.getText().get(0)).toString();
                    if (notInputText(accessibilityEvent)) {
                        Log.d(TAG, "1. if (notInputText(event))");
                        clickOnButton(accessibilityEvent, 0);
                        USSDController.instance.isRunning = false;
                        try {
                            if (USSDController.instance.callbackInvoke != null) {
                                USSDController.instance.callbackInvoke.over(charSequence);
                            } else {
                                USSDController.instance.callbackMessage.responseMessage(charSequence);
                            }
                        } catch (Exception e) {
                            String str = TAG;
                            Log.d(str, "1.E catch (notInputText(event))" + e.toString());
                        }
                    } else if (USSDController.instance.callbackInvoke != null) {
                        Log.d(TAG, "2. USSDController.instance.callbackInvoke != null");
                        USSDController.instance.callbackInvoke.responseInvoke(charSequence);
                        USSDController.instance.callbackInvoke = null;
                    } else {
                        Log.d(TAG, "3. USSDController.instance.callbackInvoke == null");
                        USSDController.instance.callbackMessage.responseMessage(charSequence);
                    }
                } catch (Exception e2) {
                    USSDController.instance.callbackMessage = null;
                    String str2 = TAG;
                    Log.d(str2, "0. isUSSDWidget(event) catch: " + e2.toString());
                }
            }
        }
    }

    public static void send(String str) {
        setTextIntoField(event, str);
        clickOnButton(event, 1);
    }

    public static void cancel() {
        clickOnButton(event, 0);
    }

    private static void setTextIntoField(AccessibilityEvent accessibilityEvent, String str) {
        USSDController uSSDController = USSDController.instance;
        Bundle bundle = new Bundle();
        bundle.putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", str);
        for (AccessibilityNodeInfo next : getLeaves(accessibilityEvent)) {
            if (next.getClassName().equals("android.widget.EditText") && !next.performAction(2097152, bundle)) {
                ClipboardManager clipboardManager = (ClipboardManager) uSSDController.context.getSystemService("clipboard");
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("text", str));
                }
                next.performAction(32768);
            }
        }
    }

    protected static boolean notInputText(AccessibilityEvent accessibilityEvent) {
        boolean z = true;
        for (AccessibilityNodeInfo className : getLeaves(accessibilityEvent)) {
            if (className.getClassName().equals("android.widget.EditText")) {
                z = false;
            }
        }
        return z;
    }

    private boolean isUSSDWidget(AccessibilityEvent accessibilityEvent) {
        return accessibilityEvent.getClassName().equals("amigo.app.AmigoAlertDialog") || accessibilityEvent.getClassName().equals("android.app.AlertDialog");
    }

    private boolean LoginView(AccessibilityEvent accessibilityEvent) {
        return isUSSDWidget(accessibilityEvent) && USSDController.instance.map.get("KEY_LOGIN").contains(((CharSequence) accessibilityEvent.getText().get(0)).toString());
    }

    public boolean problemView(AccessibilityEvent accessibilityEvent) {
        return isUSSDWidget(accessibilityEvent) && USSDController.instance.map.get("KEY_ERROR").contains(((CharSequence) accessibilityEvent.getText().get(0)).toString());
    }

    protected static void clickOnButton(AccessibilityEvent accessibilityEvent, int i) {
        int i2 = -1;
        for (AccessibilityNodeInfo next : getLeaves(accessibilityEvent)) {
            if (next.getClassName().toString().toLowerCase().contains("button") && (i2 = i2 + 1) == i) {
                next.performAction(16);
            }
        }
    }

    private static List<AccessibilityNodeInfo> getLeaves(AccessibilityEvent accessibilityEvent) {
        ArrayList arrayList = new ArrayList();
        if (accessibilityEvent.getSource() != null) {
            getLeaves(arrayList, accessibilityEvent.getSource());
        }
        return arrayList;
    }

    private static void getLeaves(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo.getChildCount() == 0) {
            list.add(accessibilityNodeInfo);
            return;
        }
        for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
            getLeaves(list, accessibilityNodeInfo.getChild(i));
        }
    }

    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
    }
}


// //package com.romellfudi.ussdlibrary;

// package com.ramymokako.plugin.ussd.android;

// import android.accessibilityservice.AccessibilityService;
// import android.content.ClipData;
// import android.content.ClipboardManager;
// import android.content.Context;
// import android.os.Bundle;
// import android.util.Log;
// import android.view.accessibility.AccessibilityEvent;
// import android.view.accessibility.AccessibilityNodeInfo;

// import java.util.ArrayList;
// import java.util.List;

// /**
//  * AccessibilityService for ussd windows on Android mobile Telcom
//  *
//  * @author Romell Dominguez
//  * @version 1.1.c 27/09/2018
//  * @since 1.0.a
//  */
// public class USSDService extends AccessibilityService {

//     private static String TAG = USSDService.class.getSimpleName();

//     private static AccessibilityEvent event;

//     /**
//      * Catch widget by Accessibility, when is showing at mobile display
//      * @param event AccessibilityEvent
//      */
//     @Override
//     public void onAccessibilityEvent(AccessibilityEvent event) {
//         this.event=event;

//         Log.d(TAG, "onAccessibilityEvent");

//         Log.d(TAG, String.format(
//                 "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
//                 event.getEventType(), event.getClassName(), event.getPackageName(),
//                 event.getEventTime(), event.getText()));

//         if(USSDController.instance  == null || !USSDController.instance.isRunning) { return; }

//         if (LoginView(event) && notInputText(event)) {
//             // first view or logView, do nothing, pass / FIRST MESSAGE
//             clickOnButton(event, 0);
//             USSDController.instance.isRunning = false;
//             USSDController.instance.callbackInvoke.over(event.getText().get(0).toString());
//         }else if (problemView(event) || LoginView(event)) {
//             // deal down
//             clickOnButton(event, 1);
//             USSDController.instance.callbackInvoke.over(event.getText().get(0).toString());
//         }else if (isUSSDWidget(event)) {
//             // ready for work
//             String response = event.getText().get(0).toString();
//             if (response.contains("\n")) {
//                 response = response.substring(response.indexOf('\n') + 1);
//             }
//             if (notInputText(event)) {
//                 // not more input panels / LAST MESSAGE
//                 // sent 'OK' button
//                 clickOnButton(event, 0);
//                 USSDController.instance.isRunning = false;
//                 USSDController.instance.callbackInvoke.over(response);
//             } else {
//                 // sent option 1
//                 if (USSDController.instance.callbackMessage == null)
//                     USSDController.instance.callbackInvoke.responseInvoke(response);
//                 else {
//                     USSDController.instance.callbackMessage.responseMessage(response);
//                     USSDController.instance.callbackMessage = null;
//                 }
//             }
//         }

//     }

//     /**
//      * Send whatever you want via USSD
//      * @param text any string
//      */
//     public static void send(String text) {
//         setTextIntoField(event, text);
//         clickOnButton(event, 1);
//     }

//     /**
//      * set text into input text at USSD widget
//      * @param event AccessibilityEvent
//      * @param data Any String
//      */
//     private static void setTextIntoField(AccessibilityEvent event, String data) {
//         USSDController ussdController = USSDController.instance;
//         Bundle arguments = new Bundle();
//         arguments.putCharSequence(
//                 AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, data);

//         for (AccessibilityNodeInfo leaf : getLeaves(event)) {
//             if (leaf.getClassName().equals("android.widget.EditText")
//                     && !leaf.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)) {
//                 ClipboardManager clipboardManager = ((ClipboardManager) ussdController.context
//                         .getSystemService(Context.CLIPBOARD_SERVICE));
//                 if(clipboardManager != null) {
//                     clipboardManager.setPrimaryClip(ClipData.newPlainText("text", data));
//                 }

//                 leaf.performAction(AccessibilityNodeInfo.ACTION_PASTE);
//             }
//         }
//     }

//     /**
//      * Method evaluate if USSD widget has input text
//      * @param event AccessibilityEvent
//      * @return boolean has or not input text
//      */
//     protected static boolean notInputText(AccessibilityEvent event) {
//         boolean flag = true;
//         for (AccessibilityNodeInfo leaf : getLeaves(event)) {
//             if (leaf.getClassName().equals("android.widget.EditText")) flag = false;
//         }
//         return flag;
//     }

//     /**
//      * The AccessibilityEvent is instance of USSD Widget class
//      * @param event AccessibilityEvent
//      * @return boolean AccessibilityEvent is USSD
//      */
//     private boolean isUSSDWidget(AccessibilityEvent event) {
//         return (event.getClassName().equals("amigo.app.AmigoAlertDialog")
//                 || event.getClassName().equals("android.app.AlertDialog"));
//     }

//     /**
//      * The View has a login message into USSD Widget
//      * @param event AccessibilityEvent
//      * @return boolean USSD Widget has login message
//      */
//     private boolean LoginView(AccessibilityEvent event) {
//         return isUSSDWidget(event)
//                 && USSDController.instance.map.get(USSDController.KEY_LOGIN)
//                 .contains(event.getText().get(0).toString());
//     }

//     /**
//      * The View has a problem message into USSD Widget
//      * @param event AccessibilityEvent
//      * @return boolean USSD Widget has problem message
//      */
//     protected boolean problemView(AccessibilityEvent event) {
//         return isUSSDWidget(event)
//                 && USSDController.instance.map.get(USSDController.KEY_ERROR)
//                 .contains(event.getText().get(0).toString());
//     }

//     /**
//      * click a button using the index
//      * @param event AccessibilityEvent
//      * @param index button's index
//      */
//     protected static void clickOnButton(AccessibilityEvent event,int index) {
//         int count = -1;
//         for (AccessibilityNodeInfo leaf : getLeaves(event)) {
//             if (leaf.getClassName().toString().toLowerCase().contains("button")) {
//                 count++;
//                 if (count == index) {
//                     leaf.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                 }
//             }
//         }
//     }

//     private static List<AccessibilityNodeInfo> getLeaves(AccessibilityEvent event) {
//         List<AccessibilityNodeInfo> leaves = new ArrayList<>();
//         if (event.getSource() != null) {
//             getLeaves(leaves, event.getSource());
//         }

//         return leaves;
//     }

//     private static void getLeaves(List<AccessibilityNodeInfo> leaves, AccessibilityNodeInfo node) {
//         if (node.getChildCount() == 0) {
//             leaves.add(node);
//             return;
//         }

//         for (int i = 0; i < node.getChildCount(); i++) {
//             getLeaves(leaves, node.getChild(i));
//         }
//     }

//     /**
//      * Active when SO interrupt the application
//      */
//     @Override
//     public void onInterrupt() {
//         Log.d(TAG, "onInterrupt");
//     }

//     /**
//      * Configure accessibility server from Android Operative System
//      */
//     @Override
//     protected void onServiceConnected() {
//         super.onServiceConnected();
//         Log.d(TAG, "onServiceConnected");
//     }
// }
