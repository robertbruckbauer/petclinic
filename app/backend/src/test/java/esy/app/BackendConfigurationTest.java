package esy.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BackendConfigurationTest {

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ResourceLoader resourceLoader;

	@Test
	void context() {
		assertNotNull(context);
		assertNotNull(publisher);
		assertNotNull(resourceLoader);
		assertNotNull(context.getBean(BackendConfiguration.class));
		assertNotNull(context.getBean(CollectionModelProcessor.class));
	}

	private <T> void assertBeanExists(final T bean) {
		assertNotNull(bean, bean.toString());
		ReflectionUtils.doWithFields(bean.getClass(), field -> {
			if (!Modifier.isStatic(field.getModifiers())) {
				ReflectionUtils.makeAccessible(field);
				assertNotNull(ReflectionUtils.getField(field, bean), field.toString());
			}
		});
	}
}
