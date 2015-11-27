package dheeraj.mathjaxoffline;

import android.app.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.content.res.*;
import android.webkit.*;
import android.text.method.*;
import android.text.*;

import dheeraj.mathjaxoffline.R;

public class MainActivity extends Activity
        implements View.OnClickListener {
    long time1, time2;
    private ProgressBar progressBar;
    private MyWebView webView;

    private String doubleEscapeTeX(String s) {
        String t = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t += '\\';
            if (s.charAt(i) != '\n') t += s.charAt(i);
            if (s.charAt(i) == '\\') t += "\\";
        }
        return t;
    }

    private int exampleIndex = 0;

    private String getExample(int index) {
        return getResources().getStringArray(R.array.tex_examples)[index];
    }

    public void onClick(View v) {
        if (v == findViewById(R.id.button2)) {
            EditText e = (EditText) findViewById(R.id.edit);
            webView.evaluateJavascript("javascript:document.getElementById('math').innerHTML='\\\\["
                    + doubleEscapeTeX(e.getText().toString()) + "\\\\]';", null);
            webView.evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);
        } else if (v == findViewById(R.id.button3)) {
            EditText e = (EditText) findViewById(R.id.edit);
            e.setText("");
            webView.evaluateJavascript("javascript:document.getElementById('math').innerHTML='';", null);
            webView.evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);
        } else if (v == findViewById(R.id.button4)) {
            progressBar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            EditText e = (EditText) findViewById(R.id.edit);
            e.setText(getExample(exampleIndex++));
            if (exampleIndex > getResources().getStringArray(R.array.tex_examples).length - 1)
                exampleIndex = 0;
/*
            webView.evaluateJavascript("MathJax.Hub.Config({messageStyle: 'none'});",null);
*/
            webView.evaluateJavascript("MathJax.Hub.Config({messageStyle: 'none',tex2jax: {preview: 'none'}});",null);
            webView.evaluateJavascript("javascript:document.getElementById('math').innerHTML='\\\\["
                    + doubleEscapeTeX(e.getText().toString())
                    + "\\\\]';", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    time1 = System.currentTimeMillis();
                }
            });
            webView.evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
            webView.evaluateJavascript("javascript:MathJax.Hub.Queue(Print);", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    time2 = System.currentTimeMillis();
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (MyWebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadDataWithBaseURL("http://bar", "<script type='text/x-mathjax-config'>"
                + "MathJax.Hub.Config({ "
                + "showMathMenu: false, "
                + "jax: ['input/TeX','output/HTML-CSS'], "
                + "extensions: ['tex2jax.js'], "
                + "TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
                + "'noErrors.js','noUndefined.js'] } "
                + "});</script>"
                + "<script type='text/javascript' "
                + "src='file:///android_asset/MathJax/MathJax.js'"
                + "></script><span id='math'></span>", "text/html", "utf-8", "");
        EditText e = (EditText) findViewById(R.id.edit);
        e.setBackgroundColor(Color.LTGRAY);
        e.setTextColor(Color.BLACK);
        e.setText("");
        Button b = (Button) findViewById(R.id.button2);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button3);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.button4);
        b.setOnClickListener(this);
        TextView t = (TextView) findViewById(R.id.textview3);
        t.setMovementMethod(LinkMovementMethod.getInstance());
        t.setText(Html.fromHtml(t.getText().toString()));
        progressBar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }
}

