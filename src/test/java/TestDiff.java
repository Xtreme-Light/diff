import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ReflectionDiffBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.example.AdapteReflectionDiffBuilder;
import org.example.FieldSCName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestDiff {

    @Test
    void givenTwoPeopleDifferent_whenComparingWithDiffBuilder_thenDifferencesFound() {
        List<PhoneNumber> phoneNumbers1 = new ArrayList<>();
        phoneNumbers1.add(new PhoneNumber("home", "123-456-7890"));
        phoneNumbers1.add(new PhoneNumber("work", "987-654-3210"));

        List<PhoneNumber> phoneNumbers2 = new ArrayList<>();
        phoneNumbers2.add(new PhoneNumber("mobile1", "123-456-7890"));
        phoneNumbers2.add(new PhoneNumber("mobile2", "987-654-3210"));

        Address address1 = new Address("123 Main St", "London", "12345");
        Address address2 = new Address("123 Main St", "Paris", "54321");

        Person person1 = new Person("John", "Doe", 30, phoneNumbers1, address1);
        Person person2 = new Person("Jane", "Smith", 28, phoneNumbers2, address2);

        DiffResult<Person> diff = compare(person1, person2);
        for (Diff<?> d : diff.getDiffs()) {
            System.out.println(d.getFieldName() + ": " + d.getLeft() + " != " + d.getRight());
        }
        System.out.println(diff);

        assertFalse(diff.getDiffs().isEmpty());
    }
    public static DiffResult<Person> compare(Person first, Person second) {
        return new AdapteReflectionDiffBuilder<>(first, second, ToStringStyle.JSON_STYLE).build();
    }
    @Data
    @AllArgsConstructor
    public static class PhoneNumber {
        private String type;
        private String number;


        // standard constructors, getters and setters
    }
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Address {
        private String streetAddress;
        private String city;
        private String postalCode;

        // standard constructors, getters and setters
    }
    @AllArgsConstructor
    @Getter
    public static class Person {
        @FieldSCName("姓")
        private String firstName;
        @FieldSCName("名")
        private String lastName;
        @FieldSCName("年龄")
        private int age;
        @FieldSCName("手机号码")
        private List<PhoneNumber> phoneNumbers;
        @FieldSCName("地址")
        private Address address;

        // standard constructors, getters and setters
    }
}
