package cl.cavallinux.jisocreator.action.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SaveISO9660ImageThread tests")
class SaveISO9660ImageThreadTest {
    @Test
    @DisplayName("Should extract elapsed percentage from mkisofs progress line")
    void shouldExtractElapsedPercentageFromMkisofsProgressLine() throws Exception {
        SaveISO9660ImageThread thread = SaveISO9660ImageThread.builder().build();

        String result = invokePrivateString(thread, "extractEllapsedPercentage", "12,5% done, estimate finish 12:30");

        assertEquals("12,5", result);
    }

    @Test
    @DisplayName("Should return empty percentage when line does not match")
    void shouldReturnEmptyPercentageWhenLineDoesNotMatch() throws Exception {
        SaveISO9660ImageThread thread = SaveISO9660ImageThread.builder().build();

        String result = invokePrivateString(thread, "extractEllapsedPercentage", "unrelated output line");

        assertEquals("", result);
    }

    @Test
    @DisplayName("Should destroy running mkisofs process")
    void shouldDestroyRunningMkisofsProcess() throws Exception {
        SaveISO9660ImageThread thread = SaveISO9660ImageThread.builder().build();
        StubProcess process = new StubProcess(true);
        thread.setMkisofsProcess(process);

        invokePrivateVoid(thread, "killISOSavingProcess");

        assertTrue(process.destroyCalled);
    }

    @Test
    @DisplayName("Should not destroy already stopped mkisofs process")
    void shouldNotDestroyAlreadyStoppedMkisofsProcess() throws Exception {
        SaveISO9660ImageThread thread = SaveISO9660ImageThread.builder().build();
        StubProcess process = new StubProcess(false);
        thread.setMkisofsProcess(process);

        invokePrivateVoid(thread, "killISOSavingProcess");

        assertFalse(process.destroyCalled);
    }

    private static String invokePrivateString(SaveISO9660ImageThread target, String methodName, String argument)
            throws Exception {
        Method method = SaveISO9660ImageThread.class.getDeclaredMethod(methodName, String.class);
        method.setAccessible(true);
        return (String) method.invoke(target, argument);
    }

    private static void invokePrivateVoid(SaveISO9660ImageThread target, String methodName) throws Exception {
        Method method = SaveISO9660ImageThread.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static final class StubProcess extends Process {
        private boolean alive;
        private boolean destroyCalled;

        private StubProcess(boolean alive) {
            this.alive = alive;
        }

        @Override
        public OutputStream getOutputStream() {
            return OutputStream.nullOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
            destroyCalled = true;
            alive = false;
        }

        @Override
        public boolean isAlive() {
            return alive;
        }
    }
}
