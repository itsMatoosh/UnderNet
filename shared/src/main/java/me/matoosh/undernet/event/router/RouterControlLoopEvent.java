package me.matoosh.undernet.event.router;

import me.matoosh.undernet.p2p.router.Router;

public class RouterControlLoopEvent extends RouterEvent {
    public RouterControlLoopEvent(Router r) {
        super(r);
    }

    @Override
    public void onCalled() {

    }
}
