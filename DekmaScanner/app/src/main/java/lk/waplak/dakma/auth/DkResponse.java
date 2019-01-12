package lk.waplak.dakma.auth;

import com.google.gson.annotations.SerializedName;

public class DkResponse {
    @SerializedName("key")
    private String id;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SerializedName("value")
    private String name;
}
