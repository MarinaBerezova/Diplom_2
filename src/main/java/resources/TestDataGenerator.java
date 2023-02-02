package resources;

import com.github.javafaker.Faker;
import java.util.Random;

public class TestDataGenerator {

    Faker faker = new Faker();
    Random random = new Random();

    private String name = faker.name().firstName();
    private String email = (faker.name().lastName() + "_" + name.substring(0,1) + random.nextInt(100) + "@yandex.ru").toLowerCase();
    private String password = name + random.nextInt(1000);

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
