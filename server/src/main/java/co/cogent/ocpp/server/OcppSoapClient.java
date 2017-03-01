package co.cogent.ocpp.server;

import eu.chargetime.ocpp.Client;
import eu.chargetime.ocpp.ClientEvents;
import eu.chargetime.ocpp.SOAPClient;
import eu.chargetime.ocpp.feature.profile.ClientCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ClientCoreProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.*;
import java.net.URL;

/**
 * Created by marty on 24/2/17.
 */
public class OcppSoapClient {
    private Client client;
    private ClientCoreProfile core;

    public void connect() throws Exception {

        // The core profile is mandatory
        core = new ClientCoreProfile(new ClientCoreEventHandler() {
            @Override
            public ChangeAvailabilityConfirmation handleChangeAvailabilityRequest(ChangeAvailabilityRequest request) {

                System.out.println(request);
                // ... handle event

                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Accepted);
            }

            @Override
            public GetConfigurationConfirmation handleGetConfigurationRequest(GetConfigurationRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public ChangeConfigurationConfirmation handleChangeConfigurationRequest(ChangeConfigurationRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public ClearCacheConfirmation handleClearCacheRequest(ClearCacheRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(DataTransferRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public RemoteStartTransactionConfirmation handleRemoteStartTransactionRequest(RemoteStartTransactionRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public RemoteStopTransactionConfirmation handleRemoteStopTransactionRequest(RemoteStopTransactionRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public ResetConfirmation handleResetRequest(ResetRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public UnlockConnectorConfirmation handleUnlockConnectorRequest(UnlockConnectorRequest request) {

                System.out.println(request);
                // ... handle event

                return null; // returning null means unsupported feature
            }
        });
        URL callbackUrl = new URL("http://localhost");
        client = new SOAPClient("Sample OCPP SOAP Client", callbackUrl, core);
        ClientEvents events = new ClientEvents() {
            @Override
            public void connectionOpened() {
                System.out.println("Client Events: connection opened");
            }

            @Override
            public void connectionClosed() {
                System.out.println("Client Events: connection closed");
            }
        };
        client.connect("http://localhost:5000", events);
    }

    public void sendBootNotification() throws Exception {

        // Use the feature profile to help create event
        Request request = core.createBootNotificationRequest("some vendor", "some model");

        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> System.out.println(s));
    }

    public void disconnect() {
        client.disconnect();
    }

    public static void main(String[] args) throws Exception {
        OcppSoapClient client = new OcppSoapClient();
        client.connect();
        client.sendBootNotification();
        client.disconnect();
    }
}
