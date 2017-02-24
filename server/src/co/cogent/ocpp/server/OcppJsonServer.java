package co.cogent.ocpp.server;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.core.*;

import java.util.UUID;

/**
 * Created by marty on 24/2/17.
 */
public class OcppJsonServer {
    private JSONServer server;

    public void started() throws Exception {
        if (server != null)
            return;

        // The core profile is mandatory
        ServerCoreProfile core = new ServerCoreProfile(new ServerCoreEventHandler() {
            @Override
            public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {

                System.out.println(request);
                // ... handle event

                return new AuthorizeConfirmation();
            }

            @Override
            public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex, StartTransactionRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex, StatusNotificationRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }
        });

        server = new JSONServer(core);
        server.open("localhost", 8887, new ServerEvents() {

            @Override
            public void newSession(UUID sessionIndex, String identifier) {

                // sessionIndex is used to send messages.
                System.out.println("New session " + sessionIndex + " with identifier " + identifier);
            }

            @Override
            public void lostSession(UUID sessionIndex) {

                System.out.println("Session " + sessionIndex + " lost connection");
            }
        });
    }

    public static void main(String[] args) throws Exception {
        OcppJsonServer server = new OcppJsonServer();
        server.started();
    }

}
