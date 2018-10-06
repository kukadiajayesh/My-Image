# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Software\android\sdk6/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class title to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file title.
#-renamesourcefileattribute SourceFile



#Support v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

#Support Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


# Retrofit 2.X
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}


## Square Picasso specific rules ##
-dontwarn com.squareup.okhttp.**

## GSON 2.2.4 specific rules ##
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
-keepattributes EnclosingMethod
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#Butterknife
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }



# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Custom
-keep class com.photobook.glimpse.model.** { *; }
-keep class com.photobook.model.** { *; }
-keep class com.photobook.om.multi.vision.** { *; }
-keep class org.apache.** { *; }
-dontwarn rx.internal.util.unsafe.**
-dontwarn okio.**
-dontwarn org.apache.http.**
-dontwarn simplesound.pcm.**
-dontwarn simplesound.system.**
-dontwarn okhttp3.**
-dontwarn com.github.siyamed.shapeimageview.path.**
-dontwarn com.google.android.gms.internal.**
-dontwarn android.support.v7.media.**