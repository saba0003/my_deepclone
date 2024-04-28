import java.util.AbstractList;

public class MyCollection<E> extends AbstractList<E> {

    private final E[] array;

    public MyCollection(E[] array) {
        this.array = array;
    }

    @Override
    public E get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array.length;
    }
}
