package models;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class AlertSubscriptions {
    public String id;
    public String info;
    public Set<String> userIds = new HashSet<>();
    public Set<AlertedUser> alertedUsers = new HashSet<>();

    public AlertSubscriptions() {
    }

    public AlertSubscriptions(String info) {
        this.info = info;
    }
}
