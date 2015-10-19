package models;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class Alert {
    public String id;
    public String info;

    public Alert() {
    }

    public Alert(String info) {
        this.info = info;
    }
}
