import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class DeepCopyUtils {

    public static <T> T deepCopy(T obj) {
        return deepCopyImpl(obj);
    }

    private static <T> T deepCopyImpl(T obj) {
        if (obj == null)
            return null;

        T newObj;
        try {
            Constructor<T> chosenConstructor = findSuitableConstructor(obj);
            if (chosenConstructor == null)
                throw new RuntimeException("No suitable constructor found");
            chosenConstructor.setAccessible(true);
            Object[] args = createConstructorArguments(obj, chosenConstructor);
            newObj = chosenConstructor.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return newObj;
    }

    /** AUX function for finding the right constructor for the given object */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findSuitableConstructor(Object obj) {
        Constructor<?>[] constructors = obj.getClass().getDeclaredConstructors();
        Constructor<T> chosenConstructor = null;

        // Check if the object has a default constructor
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                // If a default constructor is found, return it
                chosenConstructor = (Constructor<T>) constructor;
                return chosenConstructor;
            }
        }

        // If not, iterate over constructors to find the one that matches the object's fields
        outerloop:
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            // Check if the number of parameters matches the number of fields in the object
            if (parameterTypes.length != 0 && parameterTypes.length == obj.getClass().getDeclaredFields().length) {
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> fieldType = obj.getClass().getDeclaredFields()[i].getType();
                    // If the parameter type is not assignable from the field type, this constructor doesn't match
                    if (!parameterTypes[i].isAssignableFrom(fieldType)) {
                        continue outerloop; // Continue to the next constructor
                    }
                }
                // If all parameter types match the object's field types, this constructor is a match
                chosenConstructor = (Constructor<T>) constructor;
                break;
            }
        }
        return chosenConstructor;
    }

    /** AUX function for matching the parameters of the given object */
    private static Object[] createConstructorArguments(Object obj, Constructor<?> constructor) {
        // Get the parameter types of the chosen constructor
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        // Create an array to hold the arguments for the constructor
        Object[] args = new Object[parameterTypes.length];

        // Iterate over the parameters and extract the corresponding field values from the object
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> fieldType = parameterTypes[i];
            Field field = obj.getClass().getDeclaredFields()[i];
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(obj);
                if (fieldValue != null) {
                    if (fieldType.isPrimitive()) {
                        // If the field type is primitive and the field value is its wrapper class, unbox it
                        args[i] = unboxPrimitive(fieldType, fieldValue);
                    } else if (fieldType.isAssignableFrom(fieldValue.getClass())) {
                        // If the field value is not null and can be assigned to the parameter type, use it as an argument
                        args[i] = fieldValue;
                    } else {
                        // Handle the case where the field value cannot be assigned to the parameter type
                        throw new IllegalArgumentException("Field value cannot be assigned to the parameter type!");
                    }
                } else {
                    // If the field value is null, use it as an argument
                    args[i] = null;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return args;
    }

    /** AUX function for unboxing primitive types */
    private static Object unboxPrimitive(Class<?> primitiveType, Object value) {
        return switch (primitiveType.getName()) {
            case "byte" -> (value instanceof Byte) ? (Byte) value : 0;
            case "short" -> (value instanceof Short) ? (Short) value : 0;
            case "int" -> (value instanceof Integer) ? (Integer) value : 0;
            case "long" -> (value instanceof Long) ? (Long) value : 0L;
            case "float" -> (value instanceof Float) ? (Float) value : 0.0f;
            case "double" -> (value instanceof Double) ? (Double) value : 0.0;
            case "char" -> (value instanceof Character) ? (Character) value : '\u0000';
            case "boolean" -> (value instanceof Boolean) ? (Boolean) value : false;
            default -> throw new RuntimeException("Unsupported primitive type!");
        };
    }
}
