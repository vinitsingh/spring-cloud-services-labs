package io.pivotal.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import io.pivotal.fortune.FortuneService;

@Controller
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingController {

	Logger logger = LoggerFactory.getLogger(GreetingController.class);

	@Autowired
	GreetingProperties greetingProperties;

	@Autowired
	private EurekaClient discoveryClient;

	@Autowired
	FortuneService fortuneService;

	@RequestMapping("/")
	String getGreeting(Model model) {

		logger.debug("Adding greeting");
		model.addAttribute("msg", "Greetings!!!");

		RestTemplate restTemplate = new RestTemplate();
		String fortune = restTemplate.getForObject(fetchFortuneServiceUrl(), String.class);

		logger.debug("Adding fortune");
		model.addAttribute("fortune", fortune);

		// resolves to the greeting.vm velocity template
		return "greeting";
	}

	private String fetchFortuneServiceUrl() {
		InstanceInfo instance = discoveryClient.getNextServerFromEureka("FORTUNE-SERVICE", false);
		logger.debug("instanceID: {}", instance.getId());

		String fortuneServiceUrl = instance.getHomePageUrl();
		logger.debug("fortune service homePageUrl: {}", fortuneServiceUrl);

		return fortuneServiceUrl;
	}
}
