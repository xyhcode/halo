package run.halo.app.extension.controller;

import java.time.Duration;

public interface Reconciler<R> {

    Result reconcile(R request);

    Controller setupWith(ControllerBuilder builder);

    record Request(String name) {
    }

    record Result(boolean reEnqueue, Duration retryAfter) {
    }
}
