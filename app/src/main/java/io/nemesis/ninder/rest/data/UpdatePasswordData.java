package io.nemesis.ninder.rest.data;

/**
 * Created by hristo.stoyanov on 10-Jul-17.
 */

public class UpdatePasswordData {
    private String token;
    private String newPassword;

    public UpdatePasswordData(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }
}
