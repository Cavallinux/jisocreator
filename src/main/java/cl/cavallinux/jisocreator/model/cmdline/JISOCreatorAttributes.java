package cl.cavallinux.jisocreator.model.cmdline;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record JISOCreatorAttributes(String appName, 
        String appVersion, 
        String jvmVersion, 
        String jvmVendor, 
        String osName) {
    
    @Override
    public final String toString() {
        return String.format("%s version %s", appName, appVersion);
    }
    
    public final String toString(String baseString) {
        List<String> versionArguments = List.of(appName(), appVersion(), jvmVersion(), jvmVendor(), osName());
        return String.format(baseString, versionArguments.toArray());
    }
}
