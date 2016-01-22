package fi.zalando.core.utils;

import android.content.Context;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Test class for {@link PermissionUtils}
 *
 * Created by jduran on 21/01/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PermissionUtils.class)
public class PermissionUtilsTest {

    private Context mockContext;

    @Before
    public void setup() {

        mockContext = new MockContext();
    }

    @Test
    public void testCheckRuntimePermissions() throws Exception {

        String allowedPermission = "isAllowed";
        String allowedPermission2 = "isAllowed2";
        String notAllowedPermission = "notAllowed";

        PowerMockito.spy(PermissionUtils.class);

        // Mock the answers
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(allowedPermission));
        doReturn(true).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(allowedPermission2));
        doReturn(false).when(PermissionUtils.class, "checkRuntimePermission",
                any(), eq(notAllowedPermission));
        // Two allowed
        assertTrue(PermissionUtils.checkRuntimePermissions(mockContext, allowedPermission,
                allowedPermission2));
        // One allowed, 1 not allowed
        assertFalse(PermissionUtils.checkRuntimePermissions(mockContext, allowedPermission,
                notAllowedPermission));
        // Two allowed, 1 not allowed
        assertFalse(PermissionUtils.checkRuntimePermissions(mockContext, allowedPermission,
                allowedPermission2, notAllowedPermission));
    }

}
