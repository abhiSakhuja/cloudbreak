package com.sequenceiq.cloudbreak.cloud.openstack.nativ;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.InstanceConnector;
import com.sequenceiq.cloudbreak.cloud.MetadataCollector;
import com.sequenceiq.cloudbreak.cloud.event.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.CloudVmInstanceStatus;
import com.sequenceiq.cloudbreak.cloud.model.InstanceStatus;
import com.sequenceiq.cloudbreak.cloud.openstack.OpenStackClient;
import com.sequenceiq.cloudbreak.cloud.openstack.status.NovaInstanceStatus;
import com.sequenceiq.cloudbreak.cloud.template.AbstractInstanceConnector;

@Service
public class OpenStackNativeInstanceConnector extends AbstractInstanceConnector implements InstanceConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenStackNativeInstanceConnector.class);
    private static final int CONSOLE_OUTPUT_LINES = Integer.MAX_VALUE;

    @Inject
    private OpenStackClient openStackClient;

    @Inject
    private OpenStackNativeMetaDataCollector metadataCollector;

    @Override
    public MetadataCollector metadata() {
        return metadataCollector;
    }

    @Override
    public List<CloudVmInstanceStatus> check(AuthenticatedContext ac, List<CloudInstance> vms) {
        List<CloudVmInstanceStatus> statuses = new ArrayList<>();
        OSClient osClient = openStackClient.createOSClient(ac);
        for (CloudInstance vm : vms) {
            Server server = osClient.compute().servers().get(vm.getInstanceId());
            if (server == null) {
                statuses.add(new CloudVmInstanceStatus(vm, InstanceStatus.TERMINATED));
            } else {
                statuses.add(new CloudVmInstanceStatus(vm, NovaInstanceStatus.get(server)));
            }
        }
        return statuses;
    }

    @Override
    public String getConsoleOutput(AuthenticatedContext authenticatedContext, CloudInstance vm) {
        OSClient osClient = openStackClient.createOSClient(authenticatedContext);
        return osClient.compute().servers().getConsoleOutput(vm.getInstanceId(), CONSOLE_OUTPUT_LINES);
    }
}
