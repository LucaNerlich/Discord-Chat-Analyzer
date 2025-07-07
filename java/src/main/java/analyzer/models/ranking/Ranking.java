package analyzer.models.ranking;

import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

public abstract class Ranking {

    @Getter
    @Setter
    private transient Collection<AuthorData> authorDataCollection;

    public Ranking(Collection<AuthorData> authorDataCollection) {
        this.authorDataCollection = authorDataCollection;
    }

    public String getOutputFileName() {
        return "not-implemented.json";
    }
}
