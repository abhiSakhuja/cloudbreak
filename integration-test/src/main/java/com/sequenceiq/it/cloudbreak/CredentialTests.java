package com.sequenceiq.it.cloudbreak;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakTest;
import com.sequenceiq.it.cloudbreak.newway.Credential;
import com.sequenceiq.it.cloudbreak.newway.cloud.AwsCloudProvider;
import com.sequenceiq.it.cloudbreak.newway.cloud.AzureCloudProvider;
import com.sequenceiq.it.cloudbreak.newway.cloud.GcpCloudProvider;
import com.sequenceiq.it.cloudbreak.newway.cloud.OpenstackCloudProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import java.util.HashMap;
import java.util.Map;

public class CredentialTests extends CloudbreakTest {

    public static final String VALID_CRED_NAME = "valid-credential";

    public static final String VALID_AWSKEY_CRED_NAME = "valid-keybased-credential";

    public static final String VALID_OSV3_CRED_NAME = "valid-v3-credential";

    public static final String AGAIN_CRED_NAME = "again-credential";

    public static final String LONG_DC_CRED_NAME = "long-description-credential";

    public static final String SPECIAL_CRED_NAME = "@#$%|:&*;";

    public static final String INVALID_SHORT_CRED_NAME = "";

    public static final String INVALID_LONG_CRED_NAME = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    public static final String EMPTY_CRED_NAME = "temp-empty-credential";

    public static final String INVALID_AWSROLE_CRED_NAME = "temp-role-credential";

    public static final String INVALID_AWSACCESS_CRED_NAME = "temp-access-credential";

    public static final String INVALID_AWSSECRET_CRED_NAME = "temp-secret-credential";

    public static final String INVALID_OSUSER_CRED_NAME = "temp-user-credential";

    public static final String INVALID_OSENDPOINT_CRED_NAME = "temp-endpoint-credential";

    public static final String INVALID_ARMACCESS_CRED_NAME = "temp-access-credential";

    public static final String INVALID_ARMSECRET_CRED_NAME = "temp-secret-credential";

    public static final String INVALID_ARMTENANT_CRED_NAME = "temp-tenant-credential";

    public static final String INVALID_ARMSUBSCRIPTION_CRED_NAME = "temp-subscription-credential";

    public static final String INVALID_GCP12_CRED_NAME = "temp-p12-credential";

    public static final String INVALID_GCPROJECT_CRED_NAME = "temp-project-credential";

    public static final String INVALID_GCPSERVICEACCOUNT_CRED_NAME = "temp-serviceacc-credential";

    public static final String CRED_DESCRIPTION = "temporary credential for API E2E tests";

    public static final String INVALID_LONG_DESCRIPTION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudbreakTest.class);

    private String platform;

    private String errorMessage = "";

    private OpenstackCloudProvider cloudproviderOS;

    private AwsCloudProvider cloudproviderAWS;

    private GcpCloudProvider cloudproviderGCP;

    private AzureCloudProvider cloudproviderARM;

    private Map<String, Object> invalidUserMap = new HashMap<String, Object>();

    private Map<String, Object> invalidEndpointMap = new HashMap<String, Object>();

    private Map<String, Object> invalidRoleMap = new HashMap<String, Object>();

    private Map<String, Object> invalidAccessKeyMap = new HashMap<String, Object>();

    private Map<String, Object> invalidSecretKeyMap = new HashMap<String, Object>();

    private Map<String, Object> invalidTenantIDMap = new HashMap<String, Object>();

    private Map<String, Object> invalidSubscriptionIDMap = new HashMap<String, Object>();

    private Map<String, Object> emptyP12KeyMap = new HashMap<String, Object>();

    private Map<String, Object> emptyProjectId = new HashMap<String, Object>();

    private Map<String, Object> emptyServiceAccount = new HashMap<String, Object>();

    private Map<String, Object> validCredentialParametersMap = new HashMap<String, Object>();

    private Map<String, Object> validKeyBasedCredentialParametersMap = new HashMap<String, Object>();

    private Map<String, Object> validV3CredentialParametersMap = new HashMap<String, Object>();

    @BeforeTest
    @Parameters({ "provider" })
    public void beforeTest(@Optional(OpenstackCloudProvider.OPENSTACK) String provider) {
        switch (provider) {
            case "aws":
                cloudproviderAWS = new AwsCloudProvider(getTestParameter());

                platform = "AWS";

                validCredentialParametersMap = cloudproviderAWS.awsCredentialDetailsArn();
                validKeyBasedCredentialParametersMap = cloudproviderAWS.awsCredentialDetailsKey();
                invalidRoleMap = cloudproviderAWS.awsCredentialDetailsInvalidArn();
                invalidAccessKeyMap = cloudproviderAWS.awsCredentialDetailsInvalidAccessKey();
                invalidSecretKeyMap = cloudproviderAWS.awsCredentialDetailsInvalidSecretKey();

                break;
            case "azure":
                cloudproviderARM = new AzureCloudProvider(getTestParameter());

                platform = "AZURE";

                validCredentialParametersMap = cloudproviderARM.azureCredentialDetails();
                invalidAccessKeyMap = cloudproviderARM.azureCredentialDetailsInvalidAccessKey();
                invalidSecretKeyMap = cloudproviderARM.azureCredentialDetailsInvalidSecretKey();
                invalidTenantIDMap = cloudproviderARM.azureCredentialDetailsInvalidTenantID();
                invalidSubscriptionIDMap = cloudproviderARM.azureCredentialDetailsInvalidSubscriptionID();

                break;
            case "gcp":
                cloudproviderGCP = new GcpCloudProvider(getTestParameter());

                platform = "GCP";

                validCredentialParametersMap = cloudproviderGCP.gcpCredentialDetails();
                emptyP12KeyMap = cloudproviderGCP.gcpCredentialDetailsEmptyP12File();
                emptyProjectId = cloudproviderGCP.gcpCredentialDetailsEmptyProjectId();
                emptyServiceAccount = cloudproviderGCP.gcpCredentialDetailsEmptyServiceAccount();

                break;
            case "openstack":
                cloudproviderOS = new OpenstackCloudProvider(getTestParameter());

                platform = "OPENSTACK";

                validCredentialParametersMap = cloudproviderOS.openstackCredentialDetails();
                validV3CredentialParametersMap = cloudproviderOS.openstackV3CredentialDetails();
                invalidUserMap = cloudproviderOS.openstackCredentialDetailsInvalidUser();
                invalidEndpointMap = cloudproviderOS.openstackCredentialDetailsInvalidEndpoint();

                break;
            default:
                LOGGER.info("CloudProvider {} is not supported!");
                break;
        }
    }

    @AfterSuite
    public void cleanUp() throws Exception {
        String[] nameArray = {VALID_AWSKEY_CRED_NAME, VALID_OSV3_CRED_NAME};

        for (int i = 0; i < nameArray.length; i++) {
            LOGGER.info("Delete credential: \'{}\'", nameArray[i].toLowerCase().trim());
            try {
                given(CloudbreakClient.isCreated());
                given(Credential.request()
                        .withName(nameArray[i]));
                when(Credential.delete());
            } catch (ForbiddenException e) {
                String exceptionMessage = e.getResponse().readEntity(String.class);
                this.errorMessage = exceptionMessage.substring(exceptionMessage.lastIndexOf(":") + 1);
                LOGGER.info("ForbiddenException message ::: " + this.errorMessage);
            }
        }
    }

    @Test
    public void testCreateValidCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(VALID_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(validCredentialParametersMap));
        when(Credential.post());
        then(Credential.assertThis(
                (credential, t) -> {
                    Assert.assertEquals(credential.getResponse().getName(), VALID_CRED_NAME);
                })
        );
    }

    @Test
    public void testCreateValidOSV3Credential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(VALID_OSV3_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(validV3CredentialParametersMap));
        when(Credential.post());
        then(Credential.assertThis(
                (credential, t) -> {
                    Assert.assertEquals(credential.getResponse().getName(), VALID_OSV3_CRED_NAME);
                })
        );
    }

    @Test
    public void testCreateValidAWSKeyCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(VALID_AWSKEY_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(validKeyBasedCredentialParametersMap));
        when(Credential.post());
        then(Credential.assertThis(
                (credential, t) -> {
                    Assert.assertEquals(credential.getResponse().getName(), VALID_AWSKEY_CRED_NAME);
                })
        );
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidOSUserCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_OSUSER_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidUserMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidOSEndpointCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_OSENDPOINT_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidEndpointMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAWSAccessKeyCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_AWSACCESS_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidAccessKeyMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAWSSecrectKeyCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_AWSSECRET_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidSecretKeyMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAWSRoleARNCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_AWSROLE_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidRoleMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAzureSecretKeyCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_ARMSECRET_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidSecretKeyMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAzureAccessKeyCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_ARMACCESS_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidAccessKeyMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAzureInvalidTenantIDCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_ARMTENANT_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidTenantIDMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidAzureSubscriptionIDCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_ARMSUBSCRIPTION_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(invalidSubscriptionIDMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateEmptyGCPP12FileCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_GCP12_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(emptyP12KeyMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateEmptyGCPProjectIdCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_GCPROJECT_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(emptyProjectId));
        when(Credential.post());
    }

    // BUG-96615
    // GCP Credential Creation: serviceAccountId validation is missing on API
    @Test(expectedExceptions = BadRequestException.class, enabled = false)
    public void testCreateEmptyGCPServiceAccountCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_GCPSERVICEACCOUNT_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(emptyServiceAccount));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateAgainCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.isCreated()
                .withName(AGAIN_CRED_NAME));
        given(Credential.request()
                .withName(AGAIN_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withCloudPlatform(platform)
                .withParameters(validCredentialParametersMap));
        when(Credential.post());
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateCredentialWithNameOnlyException() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(EMPTY_CRED_NAME)
                .withDescription(CRED_DESCRIPTION));
        when(Credential.post());
    }

    @Test
    public void testCreateCredentialWithNameOnlyMessage() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(EMPTY_CRED_NAME)
                .withDescription(CRED_DESCRIPTION));
        try {
            when(Credential.post());
        } catch (BadRequestException e) {
            String exceptionMessage = e.getResponse().readEntity(String.class);
            this.errorMessage = exceptionMessage.substring(exceptionMessage.lastIndexOf(":") + 1);
            LOGGER.info("BadRequestException message ::: " + this.errorMessage);
        }
        then(Credential.assertThis(
                (credential, t) -> {
                    Assert.assertEquals(this.errorMessage, "\"may not be null\"}}");
                })
        );
    }

    //"The length of the credential's name has to be in range of 1 to 100"
    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidShortCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_SHORT_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withParameters(validCredentialParametersMap));
        when(Credential.post());
    }

    //"The length of the credential's name has to be in range of 1 to 100"
    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateInvalidLongCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(INVALID_LONG_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withParameters(validCredentialParametersMap));
        when(Credential.post());
    }

    //"The length of the credential's name has to be in range of 1 to 100"
    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateSpecialCharacterCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(SPECIAL_CRED_NAME)
                .withDescription(CRED_DESCRIPTION)
                .withParameters(validCredentialParametersMap));
        when(Credential.post());
    }

    //BUG-95609
    @Test(expectedExceptions = BadRequestException.class)
    public void testCreateLongDescriptionCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(LONG_DC_CRED_NAME)
                .withDescription(INVALID_LONG_DESCRIPTION)
                .withParameters(validCredentialParametersMap));
        when(Credential.post());
    }

    @Test
    public void testDeleteValidCredential() throws Exception {
        given(CloudbreakClient.isCreated());
        given(Credential.request()
                .withName(VALID_CRED_NAME));
        when(Credential.delete());
        then(Credential.assertThis(
                (credential, t) -> {
                    Assert.assertNull(credential.getResponse());
                })
        );
    }
}
