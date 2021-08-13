package com.ramymokako.plugin.ussd.android;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class USSDController implements USSDInterface, USSDApi {
    protected static final String KEY_ERROR = "KEY_ERROR";
    protected static final String KEY_LOGIN = "KEY_LOGIN";
    private static String TAG = "USSDService";
    protected static USSDController instance;
    protected CallbackInvoke callbackInvoke;
    protected CallbackMessage callbackMessage;
    protected Context context;
    protected Boolean isRunning = false;
    protected HashMap<String, HashSet<String>> map;
    private USSDInterface ussdInterface = this;

    public interface CallbackInvoke {
        void over(String str);

        void responseInvoke(String str);
    }

    public interface CallbackMessage {
        void responseMessage(String str);
    }

    public static USSDController getInstance(Context context2) {
        if (instance == null) {
            instance = new USSDController(context2);
        }
        return instance;
    }

    private USSDController(Context context2) {
        this.context = context2;
    }

    public void callUSSDInvoke(String str, HashMap<String, HashSet<String>> hashMap, CallbackInvoke callbackInvoke2) {
        callUSSDInvoke(str, 0, hashMap, callbackInvoke2);
    }

    public void callUSSDOverlayInvoke(String str, HashMap<String, HashSet<String>> hashMap, CallbackInvoke callbackInvoke2) {
        callUSSDOverlayInvoke(str, 0, hashMap, callbackInvoke2);
    }

    public void callUSSDInvoke(String str, int i, HashMap<String, HashSet<String>> hashMap, CallbackInvoke callbackInvoke2) {
        this.callbackInvoke = callbackInvoke2;
        this.map = hashMap;
        if (verifyAccesibilityAccess(this.context)) {
            dialUp(str, i);
        } else {
            this.callbackInvoke.over("Check your accessibility");
        }
    }

    public void callUSSDOverlayInvoke(String str, int i, HashMap<String, HashSet<String>> hashMap, CallbackInvoke callbackInvoke2) {
        this.callbackInvoke = callbackInvoke2;
        this.map = hashMap;
        if (!verifyAccesibilityAccess(this.context) || !verifyOverLay(this.context)) {
            this.callbackInvoke.over("Check your accessibility | overlay permission");
        } else {
            dialUp(str, i);
        }
    }

    private void dialUp(String str, int i) {
        HashMap<String, HashSet<String>> hashMap = this.map;
        if (hashMap == null || !hashMap.containsKey(KEY_ERROR) || !this.map.containsKey(KEY_LOGIN)) {
            this.callbackInvoke.over("Bad Mapping structure");
        } else if (str.isEmpty()) {
            this.callbackInvoke.over("Bad ussd number");
        } else {
            String encode = Uri.encode("#");
            if (encode != null) {
                str = str.replace("#", encode);
            }
            Uri parse = Uri.parse("tel:" + str);
            if (parse != null) {
                this.isRunning = true;
            }
            this.context.startActivity(getActionCallIntent(parse, i));
        }
    }

    private Intent getActionCallIntent(Uri uri, int i) {
        List<PhoneAccountHandle> callCapablePhoneAccounts;
        int i2 = i;
        String[] strArr = {"extra_asus_dial_use_dualsim", "com.android.phone.extra.slot", "slot", "simslot", "sim_slot", "subscription", "Subscription", "phone", "com.android.phone.DialingMode", "simSlot", "slot_id", "simId", "simnum", "phone_type", "slotId", "slotIdx"};
        Intent intent = new Intent("android.intent.action.CALL", uri);
        intent.setFlags(268435456);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        for (int i3 = 0; i3 < 16; i3++) {
            intent.putExtra(strArr[i3], i2);
        }
        TelecomManager telecomManager = (TelecomManager) this.context.getSystemService("telecom");
        if (!(telecomManager == null || (callCapablePhoneAccounts = telecomManager.getCallCapablePhoneAccounts()) == null || callCapablePhoneAccounts.size() <= i2)) {
            intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", callCapablePhoneAccounts.get(i2));
        }
        return intent;
    }

    public void sendData(String str) {
        USSDService.send(str);
    }

    public void send(String str, CallbackMessage callbackMessage2) {
        this.callbackMessage = callbackMessage2;
        this.ussdInterface.sendData(str);
    }

    public void cancel() {
        USSDService.cancel();
    }

    public static boolean verifyAccesibilityAccess(Context context2) {
        boolean isAccessiblityServicesEnable = isAccessiblityServicesEnable(context2);
        if (!isAccessiblityServicesEnable) {
            if (context2 instanceof Activity) {
                openSettingsAccessibility((Activity) context2);
            } else {
                Toast.makeText(context2, "voipUSSD accessibility service is not enabled", 1).show();
            }
        }
        return isAccessiblityServicesEnable;
    }

    public static boolean verifyOverLay(Context context2) {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context2)) {
            return true;
        }
        if (context2 instanceof Activity) {
            openSettingsOverlay((Activity) context2);
            return false;
        }
        Toast.makeText(context2, "Overlay permission have not grant permission.", 1).show();
        return false;
    }

    private static void openSettingsAccessibility(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("USSD Accessibility permission");
        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        String charSequence = applicationInfo.labelRes == 0 ? applicationInfo.nonLocalizedLabel.toString() : activity.getString(applicationInfo.labelRes);
        builder.setMessage("You must enable accessibility permissions for the app '" + charSequence + "'");
        builder.setCancelable(true);
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.startActivityForResult(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 1);
            }
        });
        AlertDialog create = builder.create();
        if (create != null) {
            create.show();
        }
    }

    private static void openSettingsOverlay(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("USSD Overlay permission");
        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        String charSequence = applicationInfo.labelRes == 0 ? applicationInfo.nonLocalizedLabel.toString() : activity.getString(applicationInfo.labelRes);
        builder.setMessage("You must allow for the app to appear '" + charSequence + "' on top of other apps.");
        builder.setCancelable(true);
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Activity activity = activity;
                activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + activity.getPackageName())));
            }
        });
        AlertDialog create = builder.create();
        if (create != null) {
            create.show();
        }
    }

    protected static boolean isAccessiblityServicesEnable(Context context2) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context2.getSystemService("accessibility");
        if (accessibilityManager == null) {
            return false;
        }
        for (AccessibilityServiceInfo next : accessibilityManager.getInstalledAccessibilityServiceList()) {
            if (next.getId().contains(context2.getPackageName())) {
                return isAccessibilitySettingsOn(context2, next.getId());
            }
        }
        return false;
    }

    protected static boolean isAccessibilitySettingsOn(Context context2, String str) {
        int i;
        String string;
        try {
            i = Settings.Secure.getInt(context2.getApplicationContext().getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException unused) {
            i = 0;
        }
        if (i == 1 && (string = Settings.Secure.getString(context2.getApplicationContext().getContentResolver(), "enabled_accessibility_services")) != null) {
            TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
            simpleStringSplitter.setString(string);
            while (simpleStringSplitter.hasNext()) {
                if (simpleStringSplitter.next().equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        return false;
    }
}

// //package com.romellfudi.ussdlibrary;

// package com.ramymokako.plugin.ussd.android;

// import android.accessibilityservice.AccessibilityServiceInfo;
// import android.annotation.SuppressLint;
// import android.app.Activity;
// import android.app.AlertDialog;
// import android.content.Context;
// import android.content.DialogInterface;
// import android.content.Intent;
// import android.content.pm.ApplicationInfo;
// import android.net.Uri;
// import android.os.Build;
// import android.provider.Settings;
// import android.telecom.PhoneAccountHandle;
// import android.telecom.TelecomManager;
// import android.text.TextUtils;
// import android.view.accessibility.AccessibilityManager;
// import android.widget.Toast;

// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;

// /**
//  * @author Romell Dominguez
//  * @version 1.1.c 27/09/2018
//  * @since 1.0.a
//  */
// public class USSDController implements USSDInterface, USSDApi {

//     protected static USSDController instance;

//     protected Context context;

//     protected HashMap<String, HashSet<String>> map;

//     protected CallbackInvoke callbackInvoke;

//     protected CallbackMessage callbackMessage;

//     protected static final String KEY_LOGIN = "KEY_LOGIN";

//     protected static final String KEY_ERROR = "KEY_ERROR";

//     protected Boolean isRunning = false;

//     private USSDInterface ussdInterface;

//     /**
//      * The Singleton building method
//      *
//      * @param context An activity that could call
//      * @return An instance of USSDController
//      */
//     public static USSDController getInstance(Context context) {
//         if (instance == null)
//             instance = new USSDController(context);
//         return instance;
//     }

//     private USSDController(Context context) {
//         ussdInterface = this;
//         this.context = context;
//     }

//     /**
//      * Invoke a dial-up calling a ussd number
//      *
//      * @param ussdPhoneNumber ussd number
//      * @param map             Map of Login and problem messages
//      * @param callbackInvoke  a callback object from return answer
//      */
//     public void callUSSDInvoke(String ussdPhoneNumber, HashMap<String, HashSet<String>> map, CallbackInvoke callbackInvoke) {
//         callUSSDInvoke(ussdPhoneNumber, 0, map, callbackInvoke);
//     }

//     /**
//      * Invoke a dial-up calling a ussd number and
//      * you had a overlay progress widget
//      *
//      * @param ussdPhoneNumber ussd number
//      * @param map             Map of Login and problem messages
//      * @param callbackInvoke  a callback object from return answer
//      */
//     public void callUSSDOverlayInvoke(String ussdPhoneNumber, HashMap<String, HashSet<String>> map, CallbackInvoke callbackInvoke) {
//         callUSSDOverlayInvoke(ussdPhoneNumber, 0, map, callbackInvoke);
//     }

//     /**
//      * Invoke a dial-up calling a ussd number
//      *
//      * @param ussdPhoneNumber ussd number
//      * @param simSlot         simSlot number to use
//      * @param map             Map of Login and problem messages
//      * @param callbackInvoke  a callback object from return answer
//      */
//     @SuppressLint("MissingPermission")
//     public void callUSSDInvoke(String ussdPhoneNumber, int simSlot, HashMap<String, HashSet<String>> map, CallbackInvoke callbackInvoke) {
//         this.callbackInvoke = callbackInvoke;
//         this.map = map;
//         if (verifyAccesibilityAccess(context)) {
//             dialUp(ussdPhoneNumber, simSlot);
//         } else {
//             this.callbackInvoke.over("Check your accessibility");
//         }
//     }

//     /**
//      * Invoke a dial-up calling a ussd number and
//      * you had a overlay progress widget
//      *
//      * @param ussdPhoneNumber ussd number
//      * @param simSlot         simSlot number to use
//      * @param map             Map of Login and problem messages
//      * @param callbackInvoke  a callback object from return answer
//      */
//     @SuppressLint("MissingPermission")
//     public void callUSSDOverlayInvoke(String ussdPhoneNumber, int simSlot, HashMap<String, HashSet<String>> map, CallbackInvoke callbackInvoke) {
//         this.callbackInvoke = callbackInvoke;
//         this.map = map;
//         if (verifyAccesibilityAccess(context) && verifyOverLay(context)) {
//             dialUp(ussdPhoneNumber, simSlot);
//         } else {
//             this.callbackInvoke.over("Check your accessibility | overlay permission");
//         }
//     }

//     private void dialUp(String ussdPhoneNumber, int simSlot) {
//         if (map == null || (!map.containsKey(KEY_ERROR) || !map.containsKey(KEY_LOGIN))) {
//             this.callbackInvoke.over("Bad Mapping structure");
//             return;
//         }
//         if (ussdPhoneNumber.isEmpty()) {
//             this.callbackInvoke.over("Bad ussd number");
//             return;
//         }
//         String uri = Uri.encode("#");
//         if (uri != null)
//             ussdPhoneNumber = ussdPhoneNumber.replace("#", uri);
//         Uri uriPhone = Uri.parse("tel:" + ussdPhoneNumber);
//         if (uriPhone != null)
//             isRunning = true;
//         this.context.startActivity(getActionCallIntent(uriPhone, simSlot));
//     }

//     /**
//      * get action call Intent
//      *
//      * @param uri     parsed uri to call
//      * @param simSlot simSlot number to use
//      */
//     @SuppressLint("MissingPermission")
//     private Intent getActionCallIntent(Uri uri, int simSlot) {
//         // https://stackoverflow.com/questions/25524476/make-call-using-a-specified-sim-in-a-dual-sim-device
//         final String simSlotName[] = {
//                 "extra_asus_dial_use_dualsim",
//                 "com.android.phone.extra.slot",
//                 "slot",
//                 "simslot",
//                 "sim_slot",
//                 "subscription",
//                 "Subscription",
//                 "phone",
//                 "com.android.phone.DialingMode",
//                 "simSlot",
//                 "slot_id",
//                 "simId",
//                 "simnum",
//                 "phone_type",
//                 "slotId",
//                 "slotIdx"
//         };

//         Intent intent = new Intent(Intent.ACTION_CALL, uri);
//         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//         intent.putExtra("com.android.phone.force.slot", true);
//         intent.putExtra("Cdma_Supp", true);

//         for (String s : simSlotName)
//             intent.putExtra(s, simSlot);

//         TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
//         if (telecomManager != null) {
//             List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
//             if (phoneAccountHandleList != null && phoneAccountHandleList.size() > simSlot)
//                 intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(simSlot));
//         }

//         return intent;
//     }

//     public void sendData(String text) {
//         USSDService.send(text);
//     }

//     public void send(String text, CallbackMessage callbackMessage) {
//         this.callbackMessage = callbackMessage;
//         ussdInterface.sendData(text);
//     }

//     public static boolean verifyAccesibilityAccess(Context context) {
//         boolean isEnabled = USSDController.isAccessiblityServicesEnable(context);
//         if (!isEnabled) {
//             if (context instanceof Activity) {
//                 openSettingsAccessibility((Activity) context);
//             } else {
//                 Toast.makeText(
//                         context,
//                         "voipUSSD accessibility service is not enabled",
//                         Toast.LENGTH_LONG
//                 ).show();
//             }
//         }
//         return isEnabled;
//     }

//     public static boolean verifyOverLay(Context context) {
//         boolean m_android_doesnt_grant = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                 && !Settings.canDrawOverlays(context);
//         if (m_android_doesnt_grant) {
//             if (context instanceof Activity) {
//                 openSettingsOverlay((Activity) context);
//             } else {
//                 Toast.makeText(context,
//                         "Overlay permission have not grant permission.",
//                         Toast.LENGTH_LONG).show();
//             }
//             return false;
//         } else
//             return true;
//     }

//     private static void openSettingsAccessibility(final Activity activity) {
//         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
//         alertDialogBuilder.setTitle("USSD Accessibility permission");
//         ApplicationInfo applicationInfo = activity.getApplicationInfo();
//         int stringId = applicationInfo.labelRes;
//         String name = applicationInfo.labelRes == 0 ?
//                 applicationInfo.nonLocalizedLabel.toString() : activity.getString(stringId);
//         alertDialogBuilder
//                 .setMessage("You must enable accessibility permissions for the app '" + name + "'");
//         alertDialogBuilder.setCancelable(true);
//         alertDialogBuilder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int id) {
//                 activity.startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 1);
//             }
//         });
//         AlertDialog alertDialog = alertDialogBuilder.create();
//         if (alertDialog != null) {
//             alertDialog.show();
//         }
//     }

//     private static void openSettingsOverlay(final Activity activity) {
//         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
//         alertDialogBuilder.setTitle("USSD Overlay permission");
//         ApplicationInfo applicationInfo = activity.getApplicationInfo();
//         int stringId = applicationInfo.labelRes;
//         String name = applicationInfo.labelRes == 0 ?
//                 applicationInfo.nonLocalizedLabel.toString() : activity.getString(stringId);
//         alertDialogBuilder
//                 .setMessage("You must allow for the app to appear '" + name + "' on top of other apps.");
//         alertDialogBuilder.setCancelable(true);
//         alertDialogBuilder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int id) {
//                 Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                         Uri.parse("package:" + activity.getPackageName()));
//                 activity.startActivity(intent);
//             }
//         });
//         AlertDialog alertDialog = alertDialogBuilder.create();
//         if (alertDialog != null) {
//             alertDialog.show();
//         }
//     }


//     protected static boolean isAccessiblityServicesEnable(Context context) {
//         AccessibilityManager am = (AccessibilityManager) context
//                 .getSystemService(Context.ACCESSIBILITY_SERVICE);
//         if (am != null) {
//             for (AccessibilityServiceInfo service : am.getInstalledAccessibilityServiceList()) {
//                 if (service.getId().contains(context.getPackageName())) {
//                     return USSDController.isAccessibilitySettingsOn(context, service.getId());
//                 }
//             }
//         }
//         return false;
//     }

//     protected static boolean isAccessibilitySettingsOn(Context context, final String service) {
//         int accessibilityEnabled = 0;
//         try {
//             accessibilityEnabled = Settings.Secure.getInt(
//                     context.getApplicationContext().getContentResolver(),
//                     android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
//         } catch (Settings.SettingNotFoundException e) {
//             //
//         }
//         if (accessibilityEnabled == 1) {
//             String settingValue = Settings.Secure.getString(
//                     context.getApplicationContext().getContentResolver(),
//                     Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//             if (settingValue != null) {
//                 TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
//                 splitter.setString(settingValue);
//                 while (splitter.hasNext()) {
//                     String accessabilityService = splitter.next();
//                     if (accessabilityService.equalsIgnoreCase(service)) {
//                         return true;
//                     }
//                 }
//             }
//         }
//         return false;
//     }

//     public interface CallbackInvoke {
//         void responseInvoke(String message);

//         void over(String message);
//     }

//     public interface CallbackMessage {
//         void responseMessage(String message);
//     }
// }
