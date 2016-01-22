package fi.zalando.core.utils;

import android.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RuntimeEnvironment;

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
        assertTrue(PermissionUtils.checkRuntimePermissions(RuntimeEnvironment.application,
                allowedPermission, allowedPermission2));
        // One allowed, 1 not allowed
        assertFalse(PermissionUtils.checkRuntimePermissions(RuntimeEnvironment.application,
                allowedPermission, notAllowedPermission));
        // Two allowed, 1 not allowed
        assertFalse(PermissionUtils.checkRuntimePermissions(RuntimeEnvironment.application,
                allowedPermission, allowedPermission2, notAllowedPermission));
    }

}
