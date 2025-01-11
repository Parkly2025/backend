package pw.react.backend.models;

import org.springframework.context.annotation.Profile;

@Profile({"!cors"})
public class CorsDefaultProperties extends CorsProperties {
}
