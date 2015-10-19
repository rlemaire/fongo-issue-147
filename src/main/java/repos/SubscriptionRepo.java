package repos;

import models.AlertedUser;
import models.AlertSubscriptions;
import models.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 */
@Repository
public class SubscriptionRepo {

    @Autowired
    private MongoOperations mongoOperations;

    public void subscribe(String userId, Alert alert) {
        subscribe(userId, alert.info, alert.id);
    }


    public void unsubscribe(String userId, Alert alert) {
        unsubscribe(userId, alert.info, alert.id);
    }

    private void subscribe(String userId, String info, String userAlertId) {
        AlertSubscriptions subscriptions = findSubscriptions(info);
        if (subscriptions == null) {
            subscriptions = new AlertSubscriptions(info);
            mongoOperations.save(subscriptions);
        }
        Optional<AlertedUser> optAlertedUser = subscriptions.alertedUsers.stream()
                .filter(alertedUser -> userId.equals(alertedUser.userId))
                .findFirst();

        if (optAlertedUser.isPresent()) {
            AlertedUser currentAlertedUser = optAlertedUser.get();
            if (currentAlertedUser.userAlertIds.add(userAlertId)) {

                mongoOperations.updateFirst(
                        query(where("_id").is(subscriptions.id).and("alertedUsers").elemMatch(where("userId").is(userId))),
                        new Update().addToSet("alertedUsers.$.userAlertIds", userAlertId),
                        AlertSubscriptions.class);
            }
        } else {
            AlertedUser newAlertedUser = new AlertedUser(userId, userAlertId);
            subscriptions.alertedUsers.add(newAlertedUser);
            mongoOperations.findAndModify(
                    query(where("_id").is(subscriptions.id)),
                    new Update().addToSet("alertedUsers", newAlertedUser),
                    AlertSubscriptions.class);
        }
    }

    private AlertSubscriptions findSubscriptions(String info) {
        return mongoOperations.findOne(query(where("info").is(info)), AlertSubscriptions.class);
    }


    private void unsubscribe(String userId, String info, String userAlertId) {
        AlertSubscriptions subscriptions = findSubscriptions(info);

        Optional<AlertedUser> optAlertedUser = subscriptions.alertedUsers.stream()
                .filter(alertedUser -> userId.equals(alertedUser.userId))
                .findFirst();


        AlertedUser alertedUser = optAlertedUser.get();
        if (alertedUser.userAlertIds.remove(userAlertId)) {
            mongoOperations.updateFirst(
                    query(where("_id").is(subscriptions.id).and("alertedUsers").elemMatch(where("userId").is(userId))),
                    new Update().pull("alertedUsers.$.userAlertIds", userAlertId),
                    AlertSubscriptions.class);

            if (alertedUser.userAlertIds.isEmpty()) {
                subscriptions.alertedUsers.remove(alertedUser);
                mongoOperations.findAndModify(
                        query(where("_id").is(subscriptions.id)),
                        new Update().pull("alertedUsers", alertedUser),
                        AlertSubscriptions.class);
            }
        }
        if (subscriptions.alertedUsers.isEmpty()) {
            mongoOperations.remove(subscriptions);
        }
    }
}
