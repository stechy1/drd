package cz.stechy.drd.net;

import cz.stechy.drd.net.message.IMessage;
import java.util.concurrent.CompletableFuture;

class Request {

    private final Object lock = new Object();

    private boolean waiting = true;
    private IMessage responce;

    CompletableFuture<IMessage> getFuture() {
        return CompletableFuture.supplyAsync(() -> {
            while(waiting) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {}
                }
            }
            return responce;
        });
    }

    void onResponce(IMessage message) {
        this.responce = message;
        waiting = false;
        synchronized (lock) {
            lock.notify();
        }
    }

}
