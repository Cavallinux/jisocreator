package cl.cavallinux.jisocreator.model.cmdline;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record JISOCreatorAttributes(String appName, 
        String appVersion, 
        String jvmVersion,
        String appVendor, 
        String jvmVendor, 
        String osName,
        String osVersion,
        int year) {
    
    @Override
    public final String toString() {
        return toString("%s version %s", Arrays.asList(appName(), appVersion()));
    }
    
    public final String toString(String baseString) {
        List<String> versionArguments = Arrays.asList(appName(), appVersion(), jvmVersion(), jvmVendor(), osName(),
                appVendor(), osVersion(), String.valueOf(year()));
        return toString(baseString, versionArguments);
    }

    public final String toString(String baseString, List<String> versionArguments) {
        return String.format(baseString, versionArguments.toArray());
    }
}
