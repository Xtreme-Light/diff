import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.example.DiffPair;
import org.example.ReflectionDiffBuilder;
import org.example.DiffDetailResult;
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

        Person person1 = new Person("John", "Doe", 30, phoneNumbers1, address1,null);
        Person person2 = new Person("Jane", "Smith", 28, phoneNumbers2, address2,null);

        DiffResult<Person> diff = compare(person1, person2);
        for (Diff<?> d : diff.getDiffs()) {
            System.out.println(d.getFieldName() + ": " + d.getLeft() + " != " + d.getRight());
        }
        System.out.println(diff);

        assertFalse(diff.getDiffs().isEmpty());
    }

    public static DiffResult<Person> compare(Person first, Person second) {
        return new org.apache.commons.lang3.builder.ReflectionDiffBuilder<>( first, second, ToStringStyle.JSON_STYLE).build();
    }

    @Data
    @AllArgsConstructor
    @ToString
    @FieldSCName("手机号码")
    public static class PhoneNumber {
        @FieldSCName("号码类型")
        private String type;
        private String number;


        // standard constructors, getters and setters
    }

    @AllArgsConstructor
    @Getter
    @ToString
    @FieldSCName("地址")
    public static class Address {
        private String streetAddress;
        @FieldSCName("城市")
        private String city;
        private String postalCode;

        // standard constructors, getters and setters
    }

    @AllArgsConstructor
    @Getter
    @ToString
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
        @FieldSCName("父亲")
        private Person father;
        // standard constructors, getters and setters
    }

    @Test
    public void givenTwoPeopleDifferentToFindDifferentAndNamed() {
        List<PhoneNumber> phoneNumbers1 = new ArrayList<>();
        phoneNumbers1.add(new PhoneNumber("home", "123-456-7890"));
        phoneNumbers1.add(new PhoneNumber("work", "987-654-3210"));

        List<PhoneNumber> phoneNumbers2 = new ArrayList<>();
        phoneNumbers2.add(new PhoneNumber("mobile1", "123-456-7890"));
        phoneNumbers2.add(new PhoneNumber("mobile2", "987-654-3210"));

        List<PhoneNumber> phoneNumbers3 = new ArrayList<>();
        phoneNumbers3.add(new PhoneNumber("mobile3", "123-456-7890"));
        phoneNumbers3.add(new PhoneNumber("mobile4", "987-654-3210"));
        List<PhoneNumber> phoneNumbers4 = new ArrayList<>();
        phoneNumbers4.add(new PhoneNumber("mobile5", "123-456-7890"));
        phoneNumbers4.add(new PhoneNumber("mobile6", "987-654-3210"));

        Address address1 = new Address("123 Main St", "London", "12345");
        Address address2 = new Address("123 Main St", "Paris", "54321");
        Address address3 = new Address("456 Main St", "London", "54321");
        Address address4 = new Address("789 Main St", "Paris", "12345");

        Person person1 = new Person("John", "Doe", 30, phoneNumbers1, address1,null);
        Person person2 = new Person("Jane", "Smith", 28, phoneNumbers2, address2,null);
        Person person3 = new Person("Janee", "Smithh", 41, phoneNumbers3, address3,person1);
        Person person4 = new Person("Johnn", "Doee", 41, phoneNumbers4, address4,person2);


        compare2(person3, person4);
    }

    public List<DiffDetailResult<Person>> compare2(Person person1, Person person2) {
        DiffDetailResult<Person> build = new ReflectionDiffBuilder<>(person1, person2, Person.class).build();
//        System.out.println(build.getDiffs());
        for (DiffPair<?> d : build.getDiffs()) {
            System.out.println(d.path() + ": " + d.left() + " != " + d.right());
        }
        return List.of(build);
    }
}
