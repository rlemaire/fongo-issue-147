package models;

import java.util.HashSet;
import java.util.Set;

public class AlertedUser {
    public String userId;
    public Set<String> userAlertIds = new HashSet<>();

    public AlertedUser() {}

    public AlertedUser(String userId, String userAlertId) {
        this.userId = userId;
        this.userAlertIds.add(userAlertId);
    }
}
