package eightbitlab.com.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Blur using RenderScript, processed on GPU when device drivers support it.
 * Requires API 17+
 *
 * @deprecated because RenderScript is deprecated and its hardware acceleration is not guaranteed.
 * RenderEffectBlur is the best alternative at the moment.
 */
@Deprecated
public class RenderScriptBlur implements BlurAlgorithm {

    private static final String TAG = "UnifiedRSBlur";

    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Context context;

    private RenderScript renderScript;
    private ScriptIntrinsicBlur blurScript;
    private Allocation outAllocation;

    private int lastBitmapWidth = -1;
    private int lastBitmapHeight = -1;

    public RenderScriptBlur(@NonNull Context context) {
        this.context = context.getApplicationContext();
        recreateRenderScript();
    }

    private void recreateRenderScript() {
        try {
            renderScript = RenderScript.create(context);
            blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        } catch (Exception e) {
            Log.e(TAG, "Failed to create RenderScript or BlurScript", e);
            renderScript = null;
            blurScript = null;
        }
    }

    private boolean canReuseAllocation(@NonNull Bitmap bitmap) {
        return bitmap.getHeight() == lastBitmapHeight && bitmap.getWidth() == lastBitmapWidth;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Bitmap blur(@NonNull Bitmap bitmap, float blurRadius) {
        if (renderScript == null || blurScript == null) {
            recreateRenderScript();
        }

        try {
            Allocation inAllocation = Allocation.createFromBitmap(renderScript, bitmap);

            if (!canReuseAllocation(bitmap)) {
                if (outAllocation != null) {
                    outAllocation.destroy();
                }
                outAllocation = Allocation.createTyped(renderScript, inAllocation.getType());
                lastBitmapWidth = bitmap.getWidth();
                lastBitmapHeight = bitmap.getHeight();
            }

            blurScript.setRadius(blurRadius);
            blurScript.setInput(inAllocation);
            blurScript.forEach(outAllocation);
            outAllocation.copyTo(bitmap);

            inAllocation.destroy();
            return bitmap;

        } catch (Exception e) {
            Log.e(TAG, "Blur failed", e);
            recreateRenderScript(); // Try to recover next time
            return bitmap;
        }
    }

    @Override
    public void destroy() {
        if (blurScript != null) {
            blurScript.destroy();
        }
        if (renderScript != null) {
            renderScript.destroy();
        }
        if (outAllocation != null) {
            outAllocation.destroy();
        }
    }

    @Override
    public boolean canModifyBitmap() {
        return true;
    }

    @NonNull
    @Override
    public Bitmap.Config getSupportedBitmapConfig() {
        return Bitmap.Config.ARGB_8888;
    }

    @Override
    public float scaleFactor() {
        return BlurController.DEFAULT_SCALE_FACTOR;
    }

    @Override
    public void render(@NonNull Canvas canvas, @NonNull Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0f, 0f, paint);
    }
}
