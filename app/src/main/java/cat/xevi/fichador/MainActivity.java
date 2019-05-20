package cat.xevi.fichador;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {


    private Button buttonFichar;
    private Button buttonSalir;
    private Button buttonConfigurar;
    private WebView webView;
    private String m_Text_user = "";
    private String m_Text_password = "";
    private String PREFS_NAME = "FICHADOR_STORED_VARS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        m_Text_user = prefs.getString("m_Text_user", "");
        m_Text_password = prefs.getString("m_Text_password", "");

        setContentView(R.layout.activity_main);
        buttonFichar = findViewById(R.id.button_fichar);
        buttonSalir = findViewById(R.id.button_salir);
        buttonConfigurar = findViewById(R.id.button_configurar);
        //LOAD VIEW INIT
        webView = findViewById(R.id.webview);
        //webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        webView.getLayoutParams().height = height;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://atosspain.bodet-software.com/open/login");
        //LOAD VIEW FINAL


        buttonFichar.setOnClickListener(v -> fichadorWebView());
        buttonSalir.setOnClickListener(v -> salir());
        buttonConfigurar.setOnClickListener(v -> configurar());
    }

    private void salir(){
        this.finishAffinity();
    }

    private void configurar(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Credenciales");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Set up the input
        final EditText et_user = new EditText(this);
        et_user.setInputType(InputType.TYPE_CLASS_TEXT);
        et_user.setHint("DAS User");
        if(!"".equals(m_Text_user)){
            et_user.setText(m_Text_user);
        }
        alertDialog.setView(et_user);

        final EditText et_password = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        et_password.setHint("Password");
        if(!"".equals(m_Text_password)){
            et_password.setText(m_Text_password);
        }
        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertDialog.setView(et_password);
        layout.addView(et_user);
        layout.addView(et_password);
        alertDialog.setView(layout);

        // Set up the buttons
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text_user = et_user.getText().toString();
                m_Text_password = et_password.getText().toString();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void fichadorWebView() {
        String USER = "";
        String PASS = "";

        if(!"".equals(m_Text_user)){
            USER = m_Text_user;
        } else {
            return;
        }
        if(!"".equals(m_Text_password)){
            PASS = m_Text_password;
        } else {
            return;
        }

        //Login page
        webView.loadUrl("javascript:(function() {document.getElementById('username').value = '"+USER+"';}) ();");
        webView.loadUrl("javascript:(function() {document.getElementById('password').value = '"+PASS+"';}) ();");
        webView.loadUrl("javascript:(function() {fcLogin();}) ();");

        //Press button page.
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() {document.querySelector('.boutonAction').click();}) ();");
                webView.setWebViewClient(new WebViewClient() {});
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("m_Text_user", m_Text_user);
        editor.putString("m_Text_password", m_Text_password);
        editor.commit(); //important, otherwise it wouldn't save.
        webView.destroy();
    }
}