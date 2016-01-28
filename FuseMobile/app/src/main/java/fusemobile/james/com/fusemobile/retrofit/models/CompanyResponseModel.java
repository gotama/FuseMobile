package fusemobile.james.com.fusemobile.retrofit.models;

import com.google.gson.annotations.SerializedName;

public class CompanyResponseModel {
    @SerializedName("name")
    public String name;
    @SerializedName("logo")
    public String logoUrl;
    @SerializedName("custom_color")
    public String customColor;
    @SerializedName("password_changing")
    public PasswordChangingResponseModel passwordChanging;
}
