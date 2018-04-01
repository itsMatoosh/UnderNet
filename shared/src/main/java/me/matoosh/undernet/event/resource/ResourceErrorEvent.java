package me.matoosh.undernet.event.resource;

import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when an error occurs with a resource.
 * Created by Mateusz Rębacz on 14.10.2017.
 */

public class ResourceErrorEvent extends ResourceEvent {
    /**
     * The exception.
     */
    public Exception exception;

    public ResourceErrorEvent(Resource resource, Exception exception) {
        super(resource);
        this.exception = exception;
    }

    @Override
    public void onCalled() {

    }
}
