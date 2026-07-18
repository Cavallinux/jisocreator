package cl.cavallinux.jisocreator.model.cmdline;

import lombok.Builder;

@Builder(toBuilder = true)
public record JISOCreatorAttributes(String appName, 
        String appVersion, 
        String jvmVersion, 
        String jvmVendor, 
        String osName) {
}
