package HitecIsFuture.demo;


import HitecIsFuture.demo.domain.service.MemberService;
import HitecIsFuture.demo.web.member.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@RequiredArgsConstructor
public class ChoApplication {


	public static void main(String[] args) {

		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AutoAppConfig.class);
		MemberRepository memberRepository = applicationContext.getBean(MemoryMemberRepository.class);
		MemberService memberService = applicationContext.getBean(MemberService.class);
		// 테스트를 위한 코드

		Member member = new Member("chominho96", "12345", "CHO", 24, Gender.MAN, Job.STUDENT);

		memberService.join(member);


		SpringApplication.run(ChoApplication.class, args);
	}

}
