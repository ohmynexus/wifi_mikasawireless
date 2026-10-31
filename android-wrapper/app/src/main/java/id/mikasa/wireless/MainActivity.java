package id.mikasa.wireless;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 7101;
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private WebView printWebView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setDefaultTextEncodingName("UTF-8");
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme()) || "about".equals(uri.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (ActivityNotFoundException ignored) { Toast.makeText(MainActivity.this, "Tidak ada aplikasi untuk membuka tautan", Toast.LENGTH_SHORT).show(); }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                try { startActivityForResult(params.createIntent(), FILE_CHOOSER_REQUEST); }
                catch (ActivityNotFoundException e) { filePathCallback = null; Toast.makeText(MainActivity.this, "File picker tidak tersedia", Toast.LENGTH_SHORT).show(); return false; }
                return true;
            }
            @Override public void onPermissionRequest(android.webkit.PermissionRequest request) { request.deny(); }
        });
        if (savedInstanceState != null) webView.restoreState(savedInstanceState);
        else webView.loadUrl("file:///android_asset/index.html");
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && filePathCallback != null) {
            filePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            filePathCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) { webView.removeJavascriptInterface("AndroidBridge"); webView.destroy(); }
        if (printWebView != null) printWebView.destroy();
        super.onDestroy();
    }

    private class AndroidBridge {
        @JavascriptInterface public void saveTextFile(String fileName, String mimeType, String content) {
            runOnUiThread(() -> {
                try {
                    File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    if (dir == null) dir = getFilesDir();
                    if (!dir.exists() && !dir.mkdirs()) throw new IOException("Folder tidak bisa dibuat");
                    String safeName = fileName == null ? "mikasa-export.txt" : fileName.replaceAll("[^A-Za-z0-9._-]", "_");
                    File out = new File(dir, safeName);
                    try (FileOutputStream stream = new FileOutputStream(out)) { stream.write((content == null ? "" : content).getBytes(StandardCharsets.UTF_8)); }
                    Toast.makeText(MainActivity.this, "File tersimpan di folder aplikasi: Downloads/" + safeName, Toast.LENGTH_LONG).show();
                } catch (Exception e) { Toast.makeText(MainActivity.this, "Gagal menyimpan file: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
            });
        }
        @JavascriptInterface public void shareText(String title, String content) {
            runOnUiThread(() -> {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_SUBJECT, title == null ? "MIKASA WIRELESS" : title);
                send.putExtra(Intent.EXTRA_TEXT, content == null ? "" : content);
                startActivity(Intent.createChooser(send, "Bagikan"));
            });
        }
        @JavascriptInterface public void printHtml(String title, String html) {
            runOnUiThread(() -> {
                try {
                    if (printWebView != null) printWebView.destroy();
                    printWebView = new WebView(MainActivity.this);
                    printWebView.loadDataWithBaseURL(null, html == null ? "" : html, "text/HTML", "UTF-8", null);
                    printWebView.postDelayed(() -> {
                        try {
                            PrintManager manager = (PrintManager) getSystemService(PRINT_SERVICE);
                            PrintDocumentAdapter adapter = printWebView.createPrintDocumentAdapter(title == null ? "MIKASA" : title);
                            manager.print("MIKASA_" + System.currentTimeMillis(), adapter, new PrintAttributes.Builder().build());
                        } catch (Exception e) { Toast.makeText(MainActivity.this, "Fitur cetak gagal dibuka", Toast.LENGTH_SHORT).show(); }
                    }, 400);
                } catch (Exception e) { Toast.makeText(MainActivity.this, "Fitur cetak gagal dibuka", Toast.LENGTH_SHORT).show(); }
            });
        }
        @JavascriptInterface public void vibrate(int milliseconds) {
            try {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= 26) vibrator.vibrate(VibrationEffect.createOneShot(Math.max(1, Math.min(milliseconds, 1000)), VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(Math.max(1, Math.min(milliseconds, 1000)));
                }
            } catch (Exception ignored) { }
        }
    }
}
