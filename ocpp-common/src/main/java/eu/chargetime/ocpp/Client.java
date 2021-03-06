package eu.chargetime.ocpp;

import eu.chargetime.ocpp.feature.Feature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

import java.util.concurrent.CompletableFuture;
/*
 ChargeTime.eu - Java-OCA-OCPP
 Copyright (C) 2015-2016 Thomas Volden <tv@chargetime.eu>

 MIT License

 Copyright (c) 2016 Thomas Volden

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

/**
 * Abstract class.
 * Handles basic client logic:
 * Holds a list of supported features.
 * Keeps track of outgoing request.
 * Calls back when a confirmation is received.
 * <p>
 * Must be overloaded in order to support specific protocols and formats.
 */
public abstract class Client extends FeatureHandler
{
    private Session session;

    /**
     * Handle required injections.
     *
     * @param   session     Inject session object
     * @see                 Session
     */
    public Client(Session session) {
        this.session = session;
    }

    /**
     * Connect to server
     *
     * @param   uri     url and port of the server
     * @param   events  client events for connect/disconnect
     */
    public void connect(String uri, ClientEvents events)
    {
        session.open(uri, new SessionEvents() {
            @Override
            public Feature findFeatureByAction(String action) {
                return findFeature(action);
            }

            @Override
            public Feature findFeatureByRequest(Request request) {
                return findFeature(request);
            }

            @Override
            public void handleConfirmation(String uniqueId, Confirmation confirmation) {
                getPromise(uniqueId).complete(confirmation);
                removePromise(uniqueId);
            }

            @Override
            public Confirmation handleRequest(Request request) {
                Feature feature = findFeatureByRequest(request);
                return feature.handleRequest(null, request);
            }

            @Override
            public void handleError(String uniqueId, String errorCode, String errorDescription, Object payload) {
                getPromise(uniqueId).completeExceptionally(new CallErrorException(errorCode, errorCode, payload));
                removePromise(uniqueId);
            }

            @Override
            public void handleConnectionClosed() {
                if (events != null)
                    events.connectionClosed();
            }

            @Override
            public void handleConnectionOpened() {
                if (events != null)
                    events.connectionOpened();
            }
        });
    }

    /**
     * Disconnect from server
     */
    public void disconnect()
    {
        try {
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send a {@link Request} to the server.
     * Can only send {@link Request} that the client supports, see {@link #addFeatureProfile(Profile)}
     *
     * @param   request                         outgoing request
     * @return call back object, will be fulfilled with confirmation when received
     * @throws UnsupportedFeatureException     trying to send a request from an unsupported feature
     * @throws OccurenceConstraintException     Thrown if the request isn't valid.
     * @see                                     CompletableFuture
     */
    public CompletableFuture<Confirmation> send(Request request) throws UnsupportedFeatureException, OccurenceConstraintException {
        Feature feature = findFeature(request);
        if (feature == null)
            throw new UnsupportedFeatureException();

        if (!request.validate())
            throw new OccurenceConstraintException();

        String id = session.sendRequest(feature.getAction(), request);
        CompletableFuture<Confirmation> promise = createPromise(id);
        return promise;
    }
}
