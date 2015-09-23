package com.sequenceiq.cloudbreak.cloud.openstack.nativ;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.OS4JException;
import org.openstack4j.api.exceptions.StatusCode;
import org.openstack4j.model.compute.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sequenceiq.cloudbreak.cloud.event.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudResource;
import com.sequenceiq.cloudbreak.cloud.model.CloudResourceStatus;
import com.sequenceiq.cloudbreak.cloud.model.ResourceStatus;
import com.sequenceiq.cloudbreak.cloud.openstack.OpenStackClient;
import com.sequenceiq.cloudbreak.cloud.openstack.nativ.context.OpenStackContext;
import com.sequenceiq.cloudbreak.domain.Resource;
import com.sequenceiq.cloudbreak.domain.ResourceType;

public abstract class AbstractOpenStackResourceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOpenStackResourceBuilder.class);

    @Inject
    private OpenStackClient openStackClient;

    protected OSClient createOSClient(AuthenticatedContext auth) {
        return openStackClient.createOSClient(auth);
    }

    protected CloudResource createNamedResource(ResourceType resourceType, String name) {
        return new CloudResource.Builder()
                .name(name)
                .type(resourceType)
                .status(Resource.Status.REQUESTED)
                .build();
    }

    protected CloudResource createPersistedResource(CloudResource namedResource, String reference) {
        return createPersistedResource(namedResource, reference, Maps.<String, Object>newHashMap());
    }

    protected CloudResource createPersistedResource(CloudResource namedResource, String reference, Map<String, Object> params) {
        return new CloudResource.Builder()
                .cloudResource(namedResource)
                .reference(reference)
                .status(Resource.Status.CREATED)
                .params(params)
                .build();
    }

    protected List<CloudResourceStatus> checkResources(ResourceType type, OpenStackContext context, AuthenticatedContext auth, List<CloudResource> resources) {
        List<CloudResourceStatus> result = new ArrayList<>();
        for (CloudResource resource : resources) {
            LOGGER.info("Check {} resource: {}", type, resource);
            try {
                boolean finished = checkStatus(context, auth, resource);
                ResourceStatus successStatus = context.isBuild() ? ResourceStatus.CREATED : ResourceStatus.DELETED;
                result.add(new CloudResourceStatus(resource, finished ? successStatus : ResourceStatus.IN_PROGRESS));
                if (finished) {
                    if (successStatus == ResourceStatus.CREATED) {
                        LOGGER.info("Creation of {} was successful", resource);
                    } else {
                        LOGGER.info("Deletion of {} was successful", resource);
                    }
                }
            } catch (OS4JException ex) {
                throw new OpenStackResourceException("Error during status check", type, resource.getName(), ex);
            }
        }
        return result;
    }

    protected CloudResource checkDeleteResponse(ActionResponse response, ResourceType resourceType, AuthenticatedContext auth, CloudResource resource,
            String faultMsg) {
        if (!response.isSuccess()) {
            if (response.getCode() != StatusCode.NOT_FOUND.getCode()) {
                throw new OpenStackResourceException(faultMsg, resourceType, resource.getName(), auth.getCloudContext().getStackId(),
                        response.getFault());
            } else {
                return null;
            }
        }
        return resource;
    }

    protected abstract boolean checkStatus(OpenStackContext context, AuthenticatedContext auth, CloudResource resource);
}
