package cn.w_wei.groupon;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented hotel_booking_web_loading, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under hotel_booking_web_loading.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("cn.w_wei.groupon", appContext.getPackageName());
    }
}
