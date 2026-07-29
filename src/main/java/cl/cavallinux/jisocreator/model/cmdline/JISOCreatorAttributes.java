package cl.cavallinux.jisocreator.model.cmdline;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record JISOCreatorAttributes(String appName, 
        String appVersion, 
        String jvmVersion, 
        String jvmVendor, 
        String osName,
        String osVersion) {
    
    @Override
    public final String toString() {
        return toString("%s version %s", List.of(appName(), appVersion()));
    }
    
    public final String toString(String baseString) {
        List<String> versionArguments = List.of(appName(), appVersion(), jvmVersion(), jvmVendor(), osName());
        return toString(baseString, versionArguments);
    }

    public final String toString(String baseString, List<String> versionArguments) {
        return String.format(baseString, versionArguments.toArray());
    }
}
