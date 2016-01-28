package fusemobile.james.com.fusemobile.retrofit;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import fusemobile.james.com.fusemobile.events.GetCompanyFailure;
import fusemobile.james.com.fusemobile.events.GetCompanySuccess;
import fusemobile.james.com.fusemobile.retrofit.models.CompanyResponseModel;
import fusemobile.james.com.fusemobile.retrofit.models.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class CompanyService implements Service {

    private Retrofit buildRestAdapter(String companyName) {
        String apiUrl = String.format("https://%s.fusion-universal.com", companyName);
        return new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public void getCompanyAsync(String companyName) throws IOException {
        Company companyService = buildRestAdapter(companyName).create(Company.class);
        Call<CompanyResponseModel> call = companyService.getCompanySync();
        call.enqueue(new Callback<CompanyResponseModel>() {
            @Override
            public void onResponse(Response<CompanyResponseModel> response) {
                EventBus.getDefault().post(new GetCompanySuccess(response));
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new GetCompanyFailure(t.getMessage()));
            }
        });
    }

    @Override
    public Response<CompanyResponseModel> getCompanySync(String companyName) throws IOException {
        Company companyServie = buildRestAdapter(companyName).create(Company.class);
        Call<CompanyResponseModel> call = companyServie.getCompanySync();
        return call.execute();
    }

    private interface Company {
        @GET("/api/v1/company.json")
        void getCompany(Callback<CompanyResponseModel> callback);

        @GET("/api/v1/company.json")
        Call<CompanyResponseModel> getCompanySync();
    }
}
