package fusemobile.james.com.fusemobile.events;

import fusemobile.james.com.fusemobile.retrofit.models.CompanyResponseModel;
import retrofit2.Response;

public class GetCompanySuccess {
    private Response<CompanyResponseModel> mResponseModel;

    public GetCompanySuccess(Response<CompanyResponseModel> responseModel) {
        mResponseModel = responseModel;
    }

    public Response<CompanyResponseModel> getResponseModel() {
        return mResponseModel;
    }
}
