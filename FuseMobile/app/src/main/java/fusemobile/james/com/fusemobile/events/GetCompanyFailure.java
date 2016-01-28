package fusemobile.james.com.fusemobile.events;

public class GetCompanyFailure {
    private String mFailureMessage;

    public GetCompanyFailure(String failureMessage) {
        this.mFailureMessage = failureMessage;
    }

    public String getFailureMessage() {
        return mFailureMessage;
    }
}
