package com.klinker.android.twitter;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Named;

import twitter4j.DirectMessage;
import twitter4j.SiteStreamsListener;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserMentionEntity;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

import static com.klinker.android.twitter.OfyService.ofy;

@Api(name = "stream", version = "v1", namespace = @ApiNamespace(ownerDomain = "twitter.android.klinker.com", ownerName = "twitter.android.klinker.com", packagePath = ""))
public class StreamService {

    @ApiMethod(name = "startStream")
    public void start(@Named("auth") final String auth, @Named("authSecret") final String secret, @Named("screenName") final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TwitterStream stream = getStreamingTwitter(auth, secret);

                stream.addListener(userStream);
                stream.user(new String[]{name});
            }
        }).start();
    }

    public static TwitterStream getStreamingTwitter(String auth, String secret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(APIKeys.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(APIKeys.TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(auth)
                .setOAuthAccessTokenSecret(secret);
        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
        return tf.getInstance();
    }

    private RegistrationRecord findRecord(long userId) {
        return ofy().load().type(RegistrationRecord.class).filter("userId", userId).first().now();
    }

    public UserStreamListener userStream = new UserStreamListener() {
        @Override
        public void onStatus(final Status status) {

            /*if (!thisInstanceOn) {
                return;
            }

            UserMentionEntity[] entities = status.getUserMentionEntities();
            ArrayList<String> names = new ArrayList<String>();
            for (UserMentionEntity e : entities) {
                names.add(e.getScreenName());
            }
            if(names.contains(settings.myScreenName)) {
                Log.v("twitter_stream_push", "onStatus @" + status.getUser().getScreenName() + " - " + status.getText());

                if (!status.isRetweet()) { // it is a normal mention

                    MentionsDataSource mentions = MentionsDataSource.getInstance(mContext);

                    if (!mentions.tweetExists(status.getId(), sharedPreferences.getInt("current_account", 1))) {
                        mentions.createTweet(status, sharedPreferences.getInt("current_account", 1));
                    }
                    InteractionsDataSource.getInstance(mContext).createMention(mContext, status, sharedPreferences.getInt("current_account", 1));
                    sharedPreferences.edit().putBoolean("new_notification", true).commit();
                    sharedPreferences.edit().putBoolean("refresh_me_mentions", true).commit();

                    if(settings.notifications && settings.mentionsNot && !sharedPreferences.getString("muted_users", "").contains(status.getUser().getScreenName())) {
                        NotificationUtils.refreshNotification(mContext);
                    }

                    mContext.sendBroadcast(new Intent("com.klinker.android.twitter.NEW_MENTION"));

                } else { // it is a retweet
                    if (!status.getUser().getScreenName().equals(settings.myScreenName) && status.getRetweetedStatus().getUser().getScreenName().equals(settings.myScreenName)) {
                        if (settings.retweetNot) {
                            int newRetweets = sharedPreferences.getInt("new_retweets", 0);
                            newRetweets++;
                            sharedPreferences.edit().putInt("new_retweets", newRetweets).commit();
                        }

                        InteractionsDataSource.getInstance(mContext).updateInteraction(mContext, status.getUser(), status, sharedPreferences.getInt("current_account", 1), InteractionsDataSource.TYPE_RETWEET);
                        sharedPreferences.edit().putBoolean("new_notification", true).commit();

                        if(settings.notifications && settings.retweetNot) {
                            NotificationUtils.newInteractions(status.getUser(), mContext, sharedPreferences, " " + getResources().getString(R.string.retweeted));
                        }
                    }
                }
            }

            if (settings.liveStreaming && idsLoaded) {
                Long mId = status.getUser().getId();
                if (ids.contains(mId)) {
                    int currentAccount = sharedPreferences.getInt("current_account", 1);
                    HomeDataSource home = HomeDataSource.getInstance(mContext);
                    if (!home.tweetExists(status.getId(), currentAccount)) {
                        //HomeContentProvider.insertTweet(status, currentAccount, mContext);
                        home.createTweet(status, currentAccount);
                        sharedPreferences.edit().putLong("account_" + currentAccount + "_lastid", status.getId()).commit();
                        getContentResolver().notifyChange(HomeContentProvider.STREAM_NOTI, null);
                        getContentResolver().notifyChange(HomeContentProvider.CONTENT_URI, null);
                    }

                    pullUnread++;
                    sharedPreferences.edit().putInt("pull_unread", pullUnread).commit();
                    mContext.sendBroadcast(new Intent("com.klinker.android.twitter.NEW_TWEET"));
                    mContext.sendBroadcast(new Intent("com.klinker.android.twitter.UPDATE_NOTIF"));
                    mContext.sendBroadcast(new Intent("com.klinker.android.talon.UPDATE_WIDGET"));

                    sharedPreferences.edit().putBoolean("refresh_me", true).commit();

                    boolean favUser = FavoriteUsersDataSource.getInstance(mContext).isFavUser(sharedPreferences.getInt("current_account", 1), status.getUser().getScreenName());
                    if (favUser && settings.favoriteUserNotifications && settings.notifications) {
                        NotificationUtils.favUsersNotification(sharedPreferences.getInt("current_account", 1), mContext);
                    }

                    if (favUser) {
                        InteractionsDataSource.getInstance(mContext).createFavoriteUserInter(mContext, status, sharedPreferences.getInt("current_account", 1));
                        sharedPreferences.edit().putBoolean("new_notification", true).commit();
                    }

                    if (settings.preCacheImages) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadImages(status);
                            }
                        }).start();
                    }
                }
            }*/
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {

        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {

        }

        @Override
        public void onStallWarning(StallWarning warning) {

        }

        @Override
        public void onFriendList(long[] friendIds) {

        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {

            RegistrationRecord record = findRecord(target.getId());
            if (record != null) {
                MessagingEndpoint messagingEndpoint = new MessagingEndpoint();
                try {
                    messagingEndpoint.sendMessageToUser(record.getRegId(),
                            "@" + source.getScreenName() + "favorited your tweet");
                } catch (IOException e) {
                    // not sure what to do here. Try again?
                }
            }
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

        }

        @Override
        public void onFollow(User source, User followedUser) {

            /*if (followedUser.getScreenName().equals(settings.myScreenName)) {

                AppSettings settings = new AppSettings(mContext);

                InteractionsDataSource.getInstance(mContext).createInteraction(mContext,
                        source,
                        null,
                        sharedPreferences.getInt("current_account", 1),
                        InteractionsDataSource.TYPE_FOLLOWER);

                sharedPreferences.edit().putBoolean("new_notification", true).commit();

                if (settings.followersNot) {
                    int newFollows = sharedPreferences.getInt("new_follows", 0);
                    newFollows++;
                    sharedPreferences.edit().putInt("new_follows", newFollows).commit();

                    if (settings.notifications) {
                        NotificationUtils.newInteractions(source, mContext, sharedPreferences, " " + getResources().getString(R.string.followed));
                    }
                }
            }*/
        }

        @Override
        public void onUnfollow(User user, User user2) {

        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {

        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {

        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {

        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {

        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {

        }

        @Override
        public void onBlock(User source, User blockedUser) {

        }

        @Override
        public void onUnblock(User source, User unblockedUser) {

        }

        @Override
        public void onException(Exception ex) {

        }
    };

    public SiteStreamsListener siteStream = new SiteStreamsListener() {

        @Override
        public void onStatus(long forUser, Status status) {

        }

        @Override
        public void onDeletionNotice(long forUser, StatusDeletionNotice statusDeletionNotice) {

        }

        @Override
        public void onFriendList(long forUser, long[] friendIds) {

        }

        @Override
        public void onFavorite(long forUser, User source, User target, Status favoritedStatus) {
            RegistrationRecord record = findRecord(forUser);
            if (record != null) {
                MessagingEndpoint messagingEndpoint = new MessagingEndpoint();
                try {
                    messagingEndpoint.sendMessageToUser(record.getRegId(),
                            "@" + source.getScreenName() + "favorited your tweet");
                } catch (IOException e) {
                    // not sure what to do here. Try again?
                }
            }
        }

        @Override
        public void onUnfavorite(long forUser, User source, User target, Status unfavoritedStatus) {

        }

        @Override
        public void onFollow(long forUser, User source, User followedUser) {

        }

        @Override
        public void onUnfollow(long forUser, User source, User unfollowedUser) {

        }

        @Override
        public void onDirectMessage(long forUser, DirectMessage directMessage) {

        }

        @Override
        public void onDeletionNotice(long forUser, long directMessageId, long userId) {

        }

        @Override
        public void onUserListMemberAddition(long forUser, User addedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListMemberDeletion(long forUser, User deletedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListSubscription(long forUser, User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListUnsubscription(long forUser, User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListCreation(long forUser, User listOwner, UserList list) {

        }

        @Override
        public void onUserListUpdate(long forUser, User listOwner, UserList list) {

        }

        @Override
        public void onUserListDeletion(long forUser, User listOwner, UserList list) {

        }

        @Override
        public void onUserProfileUpdate(long forUser, User updatedUser) {

        }

        @Override
        public void onBlock(long forUser, User source, User blockedUser) {

        }

        @Override
        public void onUnblock(long forUser, User source, User unblockedUser) {

        }

        @Override
        public void onDisconnectionNotice(String line) {

        }

        @Override
        public void onException(Exception ex) {

        }
    };
}
