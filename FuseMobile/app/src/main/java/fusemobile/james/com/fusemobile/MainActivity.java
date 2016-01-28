package fusemobile.james.com.fusemobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import de.greenrobot.event.EventBus;
import fusemobile.james.com.fusemobile.enums.HTTPStatusEnum;
import fusemobile.james.com.fusemobile.events.GetCompanyFailure;
import fusemobile.james.com.fusemobile.events.GetCompanySuccess;
import fusemobile.james.com.fusemobile.retrofit.CompanyService;
import fusemobile.james.com.fusemobile.retrofit.models.CompanyResponseModel;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private ProgressBar mProgressSpinner;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.company_logo);
        mProgressSpinner = (ProgressBar) findViewById(R.id.waiting_for_company_progress);
        mEditText = (EditText) findViewById(R.id.company_edit_text);
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    resetActivity();
                }
            }
        });
        mEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.mainlayout).requestFocus();

                    String companyName = mEditText.getText().toString();
                    companyName = companyName.replaceAll("\\s", "");
                    if (TextUtils.isEmpty(companyName)) {
                        mEditText.setBackgroundColor(Color.RED);
                    } else {
                        mProgressSpinner.setVisibility(View.VISIBLE);
                        CompanyService companyService = new CompanyService();
                        try {
                            companyService.getCompanyAsync(companyName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Purely to show understanding of basic android threading
                            new GetCompanyTask().execute(companyName);
                        }
                    }
                }
                return false;
            }
        });
    }

    private void resetActivity() {
        mImageView.setVisibility(View.GONE);
        mEditText.setTextColor(Color.BLACK);
        mEditText.setText("");
    }

    //Better to use a job queue in this instance to allow for scalability
    class GetCompanyTask extends AsyncTask<String, Void, Response<CompanyResponseModel>> {
        protected Response<CompanyResponseModel> doInBackground(String... companyName) {

            CompanyService companyService = new CompanyService();
            try {
                return companyService.getCompanySync(companyName[0].toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Response<CompanyResponseModel> result) {
            if (result != null) {
                String status = result.headers().get("Status");
                status = status.replaceAll("[^\\d.]", "");
                if (HTTPStatusEnum.get(Integer.valueOf(status)) == HTTPStatusEnum.OK) {
                    mEditText.setText(result.body().name);
                    mEditText.setTextColor(Color.GREEN);
                    new DownloadFilesTask().execute(result.body().logoUrl);
                } else {
                    mEditText.setText(getString(R.string.company_failure));
                    mEditText.setTextColor(Color.RED);
                    mProgressSpinner.setVisibility(View.GONE);
                }
            }

        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            try {
                URL myUrl = new URL(urls[0]);
                return BitmapFactory.decodeStream(myUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            mProgressSpinner.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(result);
        }
    }

    public void onEventMainThread(GetCompanySuccess e) {
        if (e.getResponseModel() != null) {
            String status = e.getResponseModel().headers().get("Status");
            status = status.replaceAll("[^\\d.]", "");
            if (HTTPStatusEnum.get(Integer.valueOf(status)) == HTTPStatusEnum.OK) {
                mEditText.setText(e.getResponseModel().body().name);
                mEditText.setTextColor(Color.GREEN);
                new DownloadFilesTask().execute(e.getResponseModel().body().logoUrl);
            } else {
                mEditText.setText(getString(R.string.company_failure));
                mEditText.setTextColor(Color.RED);
                mProgressSpinner.setVisibility(View.GONE);
            }
        }
    }

    public void onEventMainThread(GetCompanyFailure e) {
        mProgressSpinner.setVisibility(View.GONE);
        mEditText.setText(getString(R.string.company_failure));
        mEditText.setTextColor(Color.RED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}
