package fusemobile.james.com.fusemobile.retrofit.models;

import java.io.IOException;

import retrofit2.Response;

public interface Service {
    void getCompanyAsync(String companyName) throws IOException;
    Response<CompanyResponseModel> getCompanySync(String companyName) throws IOException;
}
