package tacos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/*
@SpringBootTest, like @SpringBootApplication, is a composite annotation, which is itself annotated with
@ExtentWith(SpringExtension.class), to add Spring testing capabilities to JUnit 5.
It is useful to think of this as the test class equivalent of calling SpringApplication.run() in a main() method.
 */
@SpringBootTest // A Spring Boot Test
class TacoCloudApplicationTests {

	@Test // A test method
	void contextLoads() {
		assert(true);
	}

}
