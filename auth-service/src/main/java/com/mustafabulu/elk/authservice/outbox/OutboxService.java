package com.mustafabulu.elk.authservice.outbox;

import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;

public interface OutboxService {

    void enqueueUserRegisteredEvent(UserRegisteredEvent event);
}

