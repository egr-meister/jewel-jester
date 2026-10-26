# --- Jewel Jester ProGuard/R8 rules ---
# Enabled only after the staged R8 rollout (see README). Kotlinx Serialization
# needs its generated serializers kept.

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Keep @Serializable classes and their synthetic serializer companions.
-keepclassmembers,allowobfuscation class * {
    @kotlinx.serialization.SerialName <fields>;
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    static **$* *;
}
-keepclasseswithmembers class **$$serializer { *; }

# Compose / lifecycle keep sane defaults; R8 config from AGP handles most.
-keep class com.jeweljester.game.data.model.** { *; }

# White/Black integration SDKs. Trackers reach their own classes by reflection; without
# these rules a release build compiles and then crashes at runtime, i.e. only on live
# traffic. Firebase Messaging ships its own consumer rules.
-keep class com.adjust.sdk.** { *; }
-dontwarn com.adjust.sdk.**
-keep class com.android.installreferrer.** { *; }
-dontwarn com.android.installreferrer.**
