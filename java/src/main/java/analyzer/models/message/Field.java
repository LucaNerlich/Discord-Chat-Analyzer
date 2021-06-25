package analyzer.models.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {
    private String name;
    private String value;
    private boolean isInline;
}
