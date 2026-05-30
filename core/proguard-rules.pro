# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.example.sportshub.core.data.source.remote.response.** { *; }
-keep class com.example.sportshub.core.data.source.local.entity.** { *; }
-keep class com.example.sportshub.core.domain.model.** { *; }
-keep class com.example.sportshub.core.di.CoreModuleKt { *; }
-keep class com.example.sportshub.core.domain.common.Resource { *; }
-keep class com.example.sportshub.core.domain.common.Resource$* { *; }
-keep interface com.example.sportshub.core.domain.repository.ISportRepository { *; }
-keep interface com.example.sportshub.core.domain.usecase.SportUseCase { *; }
-keep class com.example.sportshub.core.domain.usecase.SportInteractor { *; }
-keep class com.example.sportshub.core.ui.SportAdapter { *; }
-keep interface com.example.sportshub.core.data.source.remote.network.ApiService { *; }
-keepattributes Signature,*Annotation*

-dontwarn net.sqlcipher.**
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.zetetic.database.**
-keep class net.zetetic.database.** { *; }
-keep class net.zetetic.database.sqlcipher.** { *; }

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

-dontwarn java.lang.invoke.StringConcatFactory
