package utils;

import com.github.javafaker.Faker;

import java.util.Locale;

public class RandomGenerator {
	private static Faker faker = new Faker(new Locale("en-US"));

	private RandomGenerator () {
	}

	public static Faker getFaker () {
		return faker;
	}

	public static String numeric (int length) {
		return String.valueOf(faker.number().randomNumber(length, true));
	}

	public static String phoneNumberUnFormatted () {
		return faker.numerify("##########");
	}

	public static String phoneNumber () {
		return faker.phoneNumber().phoneNumber();
	}

	public static String cellPhone () {
		return faker.phoneNumber().cellPhone();
	}

	public static String firstName () {
		return faker.name().firstName();
	}

	public static String lastName () {
		return faker.name().lastName();
	}

	public static String fullName () {
		return faker.name().fullName();
	}

	public static String businessName () {
		return faker.company().name();
	}

	public static String emailAddress () {
		return faker.internet().emailAddress();
	}

	public static String hiscoxEmailAddress () {
		return System.getProperty("user.name") + "@hiscox.nonprod";
	}

	public static String rickAndMorty () {
		return faker.rickAndMorty().location() + "|" + faker.rickAndMorty().quote() + "|" + faker.rickAndMorty().character();
	}

}
