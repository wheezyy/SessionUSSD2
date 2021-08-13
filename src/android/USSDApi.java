package com.ramymokako.plugin.ussd.android;

import com.ramymokako.plugin.ussd.android.USSDController;
import java.util.HashMap;
import java.util.HashSet;

public interface USSDApi {
    void callUSSDInvoke(String str, int i, HashMap<String, HashSet<String>> hashMap, USSDController.CallbackInvoke callbackInvoke);

    void callUSSDInvoke(String str, HashMap<String, HashSet<String>> hashMap, USSDController.CallbackInvoke callbackInvoke);

    void callUSSDOverlayInvoke(String str, int i, HashMap<String, HashSet<String>> hashMap, USSDController.CallbackInvoke callbackInvoke);

    void callUSSDOverlayInvoke(String str, HashMap<String, HashSet<String>> hashMap, USSDController.CallbackInvoke callbackInvoke);

    void cancel();

    void send(String str, USSDController.CallbackMessage callbackMessage);
}

// //package com.romellfudi.ussdlibrary;

// package com.ramymokako.plugin.ussd.android;

// import java.util.HashMap;
// import java.util.HashSet;

// /**
//  *
//  * @author Romell Dominguez
//  * @version 1.1.c 13/02/2018
//  * @since 1.0.a
//  */
// public interface USSDApi {
//     void send(String text, USSDController.CallbackMessage callbackMessage);
//     void callUSSDInvoke(String ussdPhoneNumber, HashMap<String,HashSet<String>> map,
//                         USSDController.CallbackInvoke callbackInvoke);
//     void callUSSDInvoke(String ussdPhoneNumber, int simSlot, HashMap<String,HashSet<String>> map,
//                         USSDController.CallbackInvoke callbackInvoke);
//     void callUSSDOverlayInvoke(String ussdPhoneNumber, HashMap<String,HashSet<String>> map,
//                                USSDController.CallbackInvoke callbackInvoke);
//     void callUSSDOverlayInvoke(String ussdPhoneNumber, int simSlot, HashMap<String,HashSet<String>> map,
//                                USSDController.CallbackInvoke callbackInvoke);
// }
