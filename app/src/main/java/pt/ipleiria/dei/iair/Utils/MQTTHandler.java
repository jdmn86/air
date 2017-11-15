package pt.ipleiria.dei.iair.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;



public class MQTTHandler extends MqttAndroidClient implements MqttCallback {
    private static final String TAG = "MQTT";

    private static final int ACTION_STATE_UNHANDLED = 0;
    private static final int ACTION_STATE_HANDLED_EVENT = 1;
    private static final int ACTION_STATE_HANDLED_OTHER = 2;

    private static final String serverUri = "tcp://mqtt.thingspeak.com:1883";
    private static final String clientId = MqttClient.generateClientId();

    private String topic;
    private final Context context;
    private final LinkedList<MQTTActionHandler> listeners;
    private MqttConnectOptions opts;

    private List<MQTTStatusListener> statusListeners;

    public MQTTHandler(Context context) {
        super(context, serverUri, clientId, new MemoryPersistence());

        this.context = context;
        this.listeners = new LinkedList<>();
        this.statusListeners = new LinkedList<>();

        setCallback(this);
    }

    public MQTTHandler(Context context, @NonNull String topic, @NonNull String username, @NonNull char[] password) {
        this(context);

        setAuth(topic, username, password);
    }

    public void setAuth(String topic, String username, char[] password) {
        this.topic = topic;

        this.opts = new MqttConnectOptions();
        this.opts.setAutomaticReconnect(true);
        this.opts.setCleanSession(true);
        this.opts.setConnectionTimeout(15);
        this.opts.setKeepAliveInterval(30);
        this.opts.setUserName(username);
        this.opts.setPassword(password);
    }

    @Override
    public IMqttToken connect() throws MqttException {
        if (this.topic != null && !isConnected()) {
            super.connect(opts, "Connect", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        subscribe();
                    } catch (Exception ignored) {
                    }

                    triggerStatusUpdate(1);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, exception.getLocalizedMessage());
                }
            });
        }

        return null;
    }

    private void subscribe() throws Exception, MqttException, NullPointerException {
        if (!isConnected())
            throw new Exception("Not connected");

        subscribe(this.topic, 0, "Subscribe", new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Connected " + topic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                exception.printStackTrace();
                Log.d(TAG, "Not connected: " + exception.getLocalizedMessage());
            }
        });
    }

    public void publishMessage(String publishMessage) throws MqttException, IllegalStateException {
        publishMessage(this.topic, publishMessage);
    }

    public void publishMessage(String publishTopic, String publishMessage) throws MqttException, IllegalStateException {
        if (!isConnected())
            throw new IllegalStateException("Not connected");

        MqttMessage message = new MqttMessage();
        message.setPayload(publishMessage.getBytes());

        publish(publishTopic, message);

        Log.d(TAG, "Sending: " + publishTopic + " : " + publishMessage);

        if (!isConnected()) {
            Log.d(TAG, getBufferedMessageCount() + " messages in buffer.");
        }
    }

    @Override
    public void close() {
        try {
            unsubscribe(this.topic);

            disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, e.getLocalizedMessage());
        }

        super.close();

        this.listeners.clear();
    }

    public void addActionListener(MQTTActionHandler listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeActionListener(MQTTActionHandler listener) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        try {
            JSONObject json = new JSONObject(new String(message.getPayload()));

            if (json.getString("status").contains("pending")) {
                Log.d(TAG, "Recieved: " + topic + " : " + new String(message.getPayload()));

                String action = json.getString("field1");
                int result = ACTION_STATE_UNHANDLED;
                for (MQTTActionHandler l : listeners) {
                    if (l.hasAction(action)) {
                        if (l.onAction(action, json))
                            result = ACTION_STATE_HANDLED_EVENT;
                        else
                            result = ACTION_STATE_HANDLED_OTHER;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("MQTT", e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, "Connection lost: " + (cause != null ? cause.getLocalizedMessage() : "NA"));

        triggerStatusUpdate(0);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private void triggerStatusUpdate(int state) {
        for (MQTTStatusListener l : statusListeners)
            triggerStatusUpdate(l, state);
    }

    private void triggerStatusUpdate(MQTTStatusListener listener, int state) {
        if (listener != null)
            listener.onMQTTStatusChanged(state);
    }

    public void addStateListener(MQTTStatusListener listener) {
        if (!statusListeners.contains(listener))
            statusListeners.add(listener);

        triggerStatusUpdate(listener, 0);
    }

    public void removeStateListener(MQTTStatusListener listener) {
        if (statusListeners.contains(listener))
            statusListeners.remove(listener);
    }

    public interface MQTTStatusListener {
        void onMQTTStatusChanged(int state);
    }
}