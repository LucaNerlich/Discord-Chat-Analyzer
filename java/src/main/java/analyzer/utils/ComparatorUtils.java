package analyzer.utils;

import java.util.Comparator;
import java.util.function.Function;

public final class ComparatorUtils {
    
    private ComparatorUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a comparator that sorts in descending order and handles ties by returning 1
     * This is useful for TreeMap when you want to allow duplicate values
     */
    public static <T, R extends Comparable<R>> Comparator<T> descendingWithTieBreaker(Function<T, R> keyExtractor) {
        return (o1, o2) -> {
            R value1 = keyExtractor.apply(o1);
            R value2 = keyExtractor.apply(o2);
            int compare = value2.compareTo(value1); // Descending order
            return compare == 0 ? 1 : compare; // Handle ties
        };
    }
    
    /**
     * Creates a comparator that sorts in ascending order and handles ties by returning 1
     */
    public static <T, R extends Comparable<R>> Comparator<T> ascendingWithTieBreaker(Function<T, R> keyExtractor) {
        return (o1, o2) -> {
            R value1 = keyExtractor.apply(o1);
            R value2 = keyExtractor.apply(o2);
            int compare = value1.compareTo(value2); // Ascending order
            return compare == 0 ? 1 : compare; // Handle ties
        };
    }
    
    /**
     * Creates a case-insensitive string comparator with tie breaker
     */
    public static <T> Comparator<T> caseInsensitiveStringComparator(Function<T, String> keyExtractor) {
        return (o1, o2) -> {
            String str1 = keyExtractor.apply(o1);
            String str2 = keyExtractor.apply(o2);
            
            if (str1 == null || str2 == null) {
                return 0;
            }
            
            int compare = str1.compareToIgnoreCase(str2);
            return compare == 0 ? 1 : compare;
        };
    }
} 