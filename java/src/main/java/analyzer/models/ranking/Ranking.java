package analyzer.models.ranking;

import analyzer.stats.AuthorData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public abstract class Ranking {
    
    @Getter
    @Setter
    private transient List<AuthorData> authorDataList;
    
    public Ranking(List<AuthorData> authorDataList) {
        this.authorDataList = authorDataList;
    }
    
    public abstract String getOutputFilePath();
    
}
