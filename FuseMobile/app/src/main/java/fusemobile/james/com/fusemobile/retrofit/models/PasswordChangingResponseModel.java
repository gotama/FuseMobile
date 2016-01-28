package fusemobile.james.com.fusemobile.retrofit.models;

import com.google.gson.annotations.SerializedName;

public class PasswordChangingResponseModel {
    @SerializedName("enabled")
    public boolean enabled;
    @SerializedName("secure_field")
    public String secureField;
}
