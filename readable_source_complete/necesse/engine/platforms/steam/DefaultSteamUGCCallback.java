/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamPublishedFileID
 *  com.codedisaster.steamworks.SteamResult
 *  com.codedisaster.steamworks.SteamUGCCallback
 *  com.codedisaster.steamworks.SteamUGCDetails
 *  com.codedisaster.steamworks.SteamUGCQuery
 */
package necesse.engine.platforms.steam;

import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.codedisaster.steamworks.SteamUGCQuery;

public class DefaultSteamUGCCallback
implements SteamUGCCallback {
    public void onUGCQueryCompleted(SteamUGCQuery steamUGCQuery, int returnedResults, int totalResults, boolean cached, SteamResult steamResult) {
    }

    public void onSubscribeItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {
    }

    public void onUnsubscribeItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {
    }

    public void onRequestUGCDetails(SteamUGCDetails steamUGCDetails, SteamResult steamResult) {
    }

    public void onCreateItem(SteamPublishedFileID steamPublishedFileID, boolean needToAcceptEULA, SteamResult steamResult) {
    }

    public void onSubmitItemUpdate(SteamPublishedFileID steamPublishedFileID, boolean needToAcceptEULA, SteamResult steamResult) {
    }

    public void onDownloadItemResult(int appID, SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {
    }

    public void onUserFavoriteItemsListChanged(SteamPublishedFileID steamPublishedFileID, boolean addedOrRemoved, SteamResult steamResult) {
    }

    public void onSetUserItemVote(SteamPublishedFileID steamPublishedFileID, boolean voteUp, SteamResult steamResult) {
    }

    public void onGetUserItemVote(SteamPublishedFileID steamPublishedFileID, boolean hasVotedUp, boolean hasVotedDown, boolean hasSkipped, SteamResult steamResult) {
    }

    public void onStartPlaytimeTracking(SteamResult steamResult) {
    }

    public void onStopPlaytimeTracking(SteamResult steamResult) {
    }

    public void onStopPlaytimeTrackingForAllItems(SteamResult steamResult) {
    }

    public void onDeleteItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult) {
    }
}

