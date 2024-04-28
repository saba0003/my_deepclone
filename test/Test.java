import java.util.List;

public class Test {

    public static void main(String[] args) {

        Man man1 = new Man("Saba", 21, List.of("მეტრო 2033", "ჯინსების თაობა", "დიდოსტატის მარჯვენა"));
        Man man2 = new Man();
        MyCollection<Man> men = new MyCollection<>(new Man[]{man1});
        Man copy = DeepCopyUtils.deepCopy(man2);
        System.out.println(copy);
    }
}
