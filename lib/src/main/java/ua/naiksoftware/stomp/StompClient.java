package ua.naiksoftware.stomp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;
import ua.naiksoftware.stomp.pathmatcher.PathMatcher;
import ua.naiksoftware.stomp.pathmatcher.SimplePathMatcher;
import ua.naiksoftware.stomp.provider.ConnectionProvider;

/**
 * Created by naik on 05.05.16.
 */
public class StompClient {

    private static final String TAG = StompClient.class.getSimpleName();

    public static final String SUPPORTED_VERSIONS = "1.1,1.2";
    public static final String DEFAULT_ACK = "auto";

    private final ConnectionProvider connectionProvider;
    private ConcurrentHashMap<String, String> topics;
    private boolean legacyWhitespace;

    private PublishSubject<StompMessage> messageStream;
    private BehaviorSubject<Boolean> connectionStream;
    private ConcurrentHashMap<String, Flowable<StompMessage>> streamMap;
    private PathMatcher pathMatcher;
    private Disposable lifecycleDisposable;
    private Disposable messagesDisposable;
    private PublishSubject<LifecycleEvent> lifecyclePublishSubject;
    private List<StompHeader> headers;
    private HeartBeatTask heartBeatTask;

    public StompClient(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        streamMap = new ConcurrentHashMap<>();
        lifecyclePublishSubject = PublishSubject.create();
        pathMatcher = new SimplePathMatcher();
        heartBeatTask = new HeartBeatTask(new HeartBeatTask.SendCallback() {
            @Override
            public void sendClientHeartBeat(String pingMessage) {
            }
        }, this::reconnect);

    }

    /**
     * Sets the heartbeat interval to request from the server.
     * <p>
     * Not very useful yet, because we don't have any heartbeat logic on our side.
     *
     * @param ms heartbeat time in milliseconds
     */
    public StompClient withServerHeartbeat(int ms) {
        heartBeatTask.setServerHeartbeat(ms);
        return this;
    }

    /**
     * Sets the heartbeat interval that client propose to send.
     * <p>
     * Not very useful yet, because we don't have any heartbeat logic on our side.
     *
     * @param ms heartbeat time in milliseconds
     */
    public StompClient withClientHeartbeat(int ms) {
        heartBeatTask.setClientHeartbeat(ms);
        return this;
    }

    /**
     * Connect without reconnect if connected
     */
    public void connect(Activity activity) {
        connect(null, null, activity);
    }

    /**
     * Connect to websocket. If already connected, this will silently fail.
     *
     * @param _headers HTTP headers to send in the INITIAL REQUEST, i.e. during the protocol upgrade
     */
    public void connect(@Nullable List<StompHeader> _headers) {
        connect(_headers,null, null);
    }
    public void connect(@Nullable List<StompHeader> _headers, ActionI connectWS,Activity activity) {

        if ( connectWS != null ){
            connectWS.sendInfoMessage("Connecting Web Socket");
        }
        Log.d(TAG, "Connect");

        this.headers = _headers;

        if (isConnected()) {
            if ( connectWS != null ){
                connectWS.actionSucess("Web Socket: " + "Already connected, ignore");
            }

            Log.d(TAG, "Already connected, ignore");
            return;
        }
        lifecycleDisposable = connectionProvider.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            List<StompHeader> headers = new ArrayList<>();
                            headers.add(new StompHeader(StompHeader.VERSION, SUPPORTED_VERSIONS));
                            headers.add(new StompHeader(StompHeader.HEART_BEAT,
                                    heartBeatTask.getClientHeartbeat() + "," + heartBeatTask.getServerHeartbeat()));


                            if (_headers != null) headers.addAll(_headers);

                            connectionProvider.send(new StompMessage(StompCommand.CONNECT, headers, null).compile(legacyWhitespace))
                                    .subscribe(() -> {
                                        Log.d(TAG, "Publish open StompClient");
                                        lifecyclePublishSubject.onNext(lifecycleEvent);
                                    });
                            break;

                        case CLOSED:
                            Log.d(TAG, "Socket closed StompClient");
                            connectWS.actionFail("Socket closed StompClient");
                            disconnect();
                            LineStatus.statusOffLine(activity);
                            break;

                        case ERROR:
                            Log.d(TAG, "Socket closed with error StompClient");
                            connectWS.actionFail("Socket closed with error StompClient");

                            lifecyclePublishSubject.onNext(lifecycleEvent);
                            LineStatus.statusOffLine(activity);
                            break;
                    }
                });

        messagesDisposable = connectionProvider.messages()
                .map(StompMessage::from)
                .filter(heartBeatTask::consumeHeartBeat)
                .doOnNext(getMessageStream()::onNext)
                .filter(msg -> msg.getStompCommand().equals(StompCommand.CONNECTED))
                .subscribe(stompMessage -> {
                    if ( connectWS != null ){
                        connectWS.actionSucess("Web Socket StompClient: " + "Sucess");
                    }
                    getConnectionStream().onNext(true);
                }, onError -> {
                    if ( connectWS != null ){
                        connectWS.actionFail("Web Socket StompClient: " + "Error parsing message");
                    }
                    Log.e(TAG, "Error parsing message StompClient", onError);
                    LineStatus.statusOffLine(activity);
                });
    }

    synchronized private BehaviorSubject<Boolean> getConnectionStream() {
        if (connectionStream == null || connectionStream.hasComplete()) {
            connectionStream = BehaviorSubject.createDefault(false);
        }
        return connectionStream;
    }

    synchronized private PublishSubject<StompMessage> getMessageStream() {
        if (messageStream == null || messageStream.hasComplete()) {
            messageStream = PublishSubject.create();
        }
        return messageStream;
    }

    public Completable send(String destination) {
        return send(destination, null);
    }

    public Completable send(String destination, String data) {
        return send(new StompMessage(
                StompCommand.SEND,
                Collections.singletonList(new StompHeader(StompHeader.DESTINATION, destination)),
                data));
    }

    public Completable send(@NonNull StompMessage stompMessage) {
        Completable completable = connectionProvider.send(stompMessage.compile(legacyWhitespace));
        CompletableSource connectionComplete = getConnectionStream()
                .filter(isConnected -> isConnected)
                .firstElement().ignoreElement();
        return completable
                .startWith(connectionComplete);
    }

    @SuppressLint("CheckResult")
    private void sendHeartBeat(@NonNull String pingMessage) {
        Completable completable = connectionProvider.send(pingMessage);
        CompletableSource connectionComplete = getConnectionStream()
                .filter(isConnected -> isConnected)
                .firstElement().ignoreElement();
        completable.startWith(connectionComplete)
                .onErrorComplete()
                .subscribe();
    }

    public Flowable<LifecycleEvent> lifecycle() {
        return lifecyclePublishSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Disconnect from server, and then reconnect with the last-used headers
     */
    @SuppressLint("CheckResult")
    public void reconnect() {

        if (!this.isConnected()) {
            disconnectCompletable()
                    .subscribe(() -> connect(headers),
                            e -> Log.e(TAG, "Disconnect error StompClient", e));
        }
    }

    @SuppressLint("CheckResult")
    public void disconnect() {
        disconnectCompletable().subscribe(() -> {
        }, e -> Log.e(TAG, "Disconnect error StompClient", e));
    }

    public Completable disconnectCompletable() {

        heartBeatTask.shutdown();

        if (lifecycleDisposable != null) {
            lifecycleDisposable.dispose();
        }
        if (messagesDisposable != null) {
            messagesDisposable.dispose();
        }

        return connectionProvider.disconnect()
                .doFinally(() -> {
                    Log.d(TAG, "Stomp disconnected StompClient");
                    getConnectionStream().onComplete();
                    getMessageStream().onComplete();
                    lifecyclePublishSubject.onNext(new LifecycleEvent(LifecycleEvent.Type.CLOSED));
                });
    }

    public Flowable<StompMessage> topic(String destinationPath) {
        return topic(destinationPath, null);
    }

    public Flowable<StompMessage> topic(@NonNull String destPath, List<StompHeader> headerList) {
        if (destPath == null)
            return Flowable.error(new IllegalArgumentException("Topic path cannot be null"));
        else if (!streamMap.containsKey(destPath))
            streamMap.put(destPath,
                    Completable.defer(() -> subscribePath(destPath, headerList)).andThen(
                    getMessageStream()
                            .filter(msg -> pathMatcher.matches(destPath, msg))
                            .toFlowable(BackpressureStrategy.BUFFER)
                            .doFinally(() -> unsubscribePath(destPath).subscribe())
                            .share())
            );
        return streamMap.get(destPath);
    }

    private Completable subscribePath(String destinationPath, @Nullable List<StompHeader> headerList) {
        String topicId = UUID.randomUUID().toString();

        if (topics == null) topics = new ConcurrentHashMap<>();

        // Only continue if we don't already have a subscription to the topic
        if (topics.containsKey(destinationPath)) {
            Log.d(TAG, "Attempted to subscribe to already-subscribed path!");
            return Completable.complete();
        }

        topics.put(destinationPath, topicId);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(StompHeader.ID, topicId));
        headers.add(new StompHeader(StompHeader.DESTINATION, destinationPath));
        headers.add(new StompHeader(StompHeader.ACK, DEFAULT_ACK));
        if (headerList != null) headers.addAll(headerList);
        return send(new StompMessage(StompCommand.SUBSCRIBE,
                headers, null))
                .doOnError(throwable -> unsubscribePath(destinationPath).subscribe());
    }


    private Completable unsubscribePath(String dest) {
        streamMap.remove(dest);

        String topicId = topics.get(dest);

        if (topicId == null) {
            return Completable.complete();
        }

        topics.remove(dest);

        Log.d(TAG, "Unsubscribe path: " + dest + " id: " + topicId);

        return send(new StompMessage(StompCommand.UNSUBSCRIBE,
                Collections.singletonList(new StompHeader(StompHeader.ID, topicId)), null)).onErrorComplete();
    }

    /**
     * Set the wildcard or other matcher for Topic subscription.
     * <p>
     * Right now, the only options are simple, rmq supported.
     * But you can write you own matcher by implementing {@link PathMatcher}
     * <p>
     * When set to {@link ua.naiksoftware.stomp.pathmatcher.RabbitPathMatcher}, topic subscription allows for RMQ-style wildcards.
     * <p>
     *
     * @param pathMatcher Set to {@link SimplePathMatcher} by default
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public boolean isConnected() {
        return getConnectionStream().getValue();
    }

    /**
     * Reverts to the old frame formatting, which included two newlines between the message body
     * and the end-of-frame marker.
     * <p>
     * Legacy: Body\n\n^@
     * <p>
     * Default: Body^@
     *
     * @param legacyWhitespace whether to append an extra two newlines
     * @see <a href="http://stomp.github.io/stomp-specification-1.2.html#STOMP_Frames">The STOMP spec</a>
     */
    public void setLegacyWhitespace(boolean legacyWhitespace) {
        this.legacyWhitespace = legacyWhitespace;
    }
    
    /** returns the to topic (subscription id) corresponding to a given destination  
     * @param dest the destination
     * @return the topic (subscription id) or null if no topic corresponds to the destination */
    public String getTopicId(String dest) {
        return topics.get(dest);
    }
}
