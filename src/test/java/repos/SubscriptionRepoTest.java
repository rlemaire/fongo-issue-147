package repos;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.MongoClient;
import models.AlertSubscriptions;
import models.Alert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
/**
 *
 */
public class SubscriptionRepoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();

    private MongoOperations mongoOperations;
    private SubscriptionRepo subscriptionRepo = new SubscriptionRepo();

    @Before
    public void injectMongo() throws Exception {
        MongoClient mongo = (MongoClient) fongoRule.getMongo();
        mongoOperations = new MongoTemplate(mongo, fongoRule.getDB().getName());
        ReflectionTestUtils.setField(subscriptionRepo, "mongoOperations", mongoOperations);
    }

    @Test
    public void unsubscribe_shouldRemoveUserFromExistingSubscription() {
        Alert aLert = new Alert("A");

        // Both subscribed
        subscriptionRepo.subscribe("goku", aLert);
        subscriptionRepo.subscribe("vegeta", aLert);

        // Goku unsubscribes
        subscriptionRepo.unsubscribe("goku", aLert);

        List<AlertSubscriptions> subscriptions = mongoOperations.findAll(AlertSubscriptions.class);
        Assert.assertEquals(subscriptions.size(), 1);

        AlertSubscriptions subscription = subscriptions.get(0);

        // Vegeta is in the collection but not goku
        Assert.assertEquals(subscription.alertedUsers.size(), 1);
        Assert.assertEquals(subscription.alertedUsers.iterator().next().userId, "vegeta");
    }

    @Test
    public void unsubscribe_shouldDeleteEmptySubscription() {
        Alert aLert = new Alert("A");

        // Subscription
        subscriptionRepo.subscribe("goku", aLert);

        // Goku unsubscribes
        subscriptionRepo.unsubscribe("goku", aLert);

        List<AlertSubscriptions> subscriptions = mongoOperations.findAll(AlertSubscriptions.class);
        Assert.assertEquals(subscriptions.size(), 1);
    }

}