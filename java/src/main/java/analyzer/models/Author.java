package analyzer.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Objects;

@Getter
@Setter
public class Author {
    
    private String id;
    private String name;
    private String discriminator;
    private String nickname;
    private String color;
    private boolean isBot;
    private String avatarUrl;
    private String url;
    private String iconUrl;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Author author = (Author) o;
        return id.equals(author.id) && name.equals(author.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return nickname;
    }
    
    public static class AuthorComparator implements Comparator<Author> {
        @Override
        public int compare(Author o1, Author o2) {
            int result = 0;
            
            if (StringUtils.hasText(o1.getName()) && StringUtils.hasText(o2.getName())) {
                result = o1.getName().compareToIgnoreCase(o2.getName());
            }
            
            return result;
        }
    }
}
