package com.sequenceiq.cloudbreak.orchestrator.salt.poller.checker;

import com.sequenceiq.cloudbreak.orchestrator.salt.client.SaltConnector;
import com.sequenceiq.cloudbreak.orchestrator.salt.client.target.Compound;
import com.sequenceiq.cloudbreak.orchestrator.salt.domain.ApplyResponse;
import com.sequenceiq.cloudbreak.orchestrator.salt.poller.BaseSaltJobRunner;
import com.sequenceiq.cloudbreak.orchestrator.salt.states.SaltStates;

import java.util.Set;


public class SyncGrainsChecker extends BaseSaltJobRunner {

    public SyncGrainsChecker(Set<String> target) {
        super(target);
    }

    @Override
    public String submit(SaltConnector saltConnector) {
        ApplyResponse grainsResult = SaltStates.syncGrains(saltConnector, new Compound(getTarget()));
        Set<String> strings = collectMissingNodes(saltConnector, collectNodes(grainsResult));
        setTarget(strings);
        return strings.toString();
    }

    @Override
    public String toString() {
        return "SyncGrainsChecker{" + super.toString() + "}";
    }

}