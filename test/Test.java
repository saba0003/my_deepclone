import java.util.List;

public class Test {

    public static void main(String[] args) {

        // Given
        Man man1 = new Man("Saba", 21, List.of("მეტრო 2033", "ჯინსების თაობა", "დიდოსტატის მარჯვენა"));
        Man man2 = new Man();
        MyCollection<Man> men = new MyCollection<>(new Man[]{man1, man2});

        // When
        Man copy1 = DeepCopyUtils.deepCopy(man1);
        Man copy2 = DeepCopyUtils.deepCopy(man2);
        MyCollection<Man> copy3 = DeepCopyUtils.deepCopy(men);

        // Then
        assertManEquals(copy1, man1);
        assertManEquals(copy2, man2);
        assertMenEquals(copy3, men);

        System.out.println("Assertion passed: copies are equal to actuals!");
    }

    private static void assertManEquals(Man expected, Man actual) {
        if ((expected.getName() == null && actual.getName() != null) ||
                (expected.getName() != null && !expected.getName().equals(actual.getName())))
            throw new AssertionError("Name does not match");

        if (expected.getAge() != actual.getAge())
            throw new AssertionError("Age does not match");

        if ((expected.getFavoriteBooks() == null && actual.getFavoriteBooks() != null) ||
                (expected.getFavoriteBooks() != null && !expected.getFavoriteBooks().equals(actual.getFavoriteBooks())))
            throw new AssertionError("Favorite books do not match");
    }

    private static void assertMenEquals(MyCollection<Man> expected, MyCollection<Man> actual) {
        if (expected.size() != actual.size())
            throw new AssertionError("Array lengths do not match");
        for (int i = 0; i < expected.size(); i++)
            assertManEquals(expected.get(i), actual.get(i));
    }
}
