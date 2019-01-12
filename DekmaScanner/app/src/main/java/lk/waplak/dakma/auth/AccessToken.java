package lk.waplak.dakma.auth;

public class AccessToken {
    private String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    private String token_type;
}
