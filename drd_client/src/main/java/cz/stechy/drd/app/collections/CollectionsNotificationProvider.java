package cz.stechy.drd.app.collections;

import cz.stechy.screens.Notification;

@FunctionalInterface
public interface CollectionsNotificationProvider {

    void showNotification(Notification notification);

}
