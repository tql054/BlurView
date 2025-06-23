[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner2-direct.svg)](https://vshymanskyy.github.io/StandWithUkraine)

# BlurView (Forked Version)

> ⚠️ This is a **fork** of [Dimezis/BlurView](https://github.com/Dimezis/BlurView), originally created and maintained by **Dmytro Saviuk**.\
> This version is maintained for **bug fixes, compatibility, and community use** only.

## ✅ What's Modified

- 🛡 Added `try-catch` block for safe RenderScript initialization to prevent runtime crashes on unsupported devices
- 🗁 Added optional fallback for `BlurAlgorithm` when RenderScript is unavailable
- 🔧 Minor refactors to improve stability

All changes are intended for safer usage in production environments while preserving the original library's performance.

---

## 💡 Usage

```xml
<eightbitlab.com.blurview.BlurView
    android:id="@+id/blurView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:blurOverlayColor="@color/colorOverlay">
    <!-- Any children here will NOT be blurred -->
</eightbitlab.com.blurview.BlurView>
```

```java
View decorView = getWindow().getDecorView();
ViewGroup rootView = decorView.findViewById(android.R.id.content);
Drawable windowBackground = decorView.getBackground();

blurView.setupWith(rootView, new RenderScriptBlur(this))
        .setFrameClearDrawable(windowBackground)
        .setBlurRadius(20f);
```

### 🔄 Optional Fallback

```java
@NonNull
private BlurAlgorithm getBlurAlgorithm() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return new RenderEffectBlur();
    } else {
        return new SafeRenderScriptBlur(this); // Forked logic to handle crashes
    }
}
```

---

## 🎯 Features

- Efficient iOS-style blur with minimal overdraw
- Optional RenderScript or RenderEffect support
- No redraw loops — updates only when needed
- Dialog and DialogFragment blur support
- Easy integration and setup
- Rounded corner support

---

## 🔥 Limitations

- Cannot blur `SurfaceView`, `VideoView`, `MapView`, or `GLSurfaceView`\
  (due to hardware-accelerated rendering constraints)

---

## 📦 Gradle

This fork is not published yet. You can include it manually or host via JitPack if needed.

```groovy
implementation 'com.github.tql054:BlurView:<tag>'
```

> Replace `<tag>` with the Git tag released (e.g., `v1.0.0`).\
---

## 📝 License

```
Copyright 2024 Dmytro Saviuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

   http://www.apache.org/licenses/LICENSE-2.0
```

---

## 🙏 Attribution

This library is based on excellent work by [Dimezis/BlurView](https://github.com/Dimezis/BlurView).\
All credit for the original idea and implementation belongs to the original author. This fork exists solely to improve safety and compatibility.

# BlurView
# BlurView
