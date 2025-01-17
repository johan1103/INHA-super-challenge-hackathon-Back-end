package HitecIsFuture.demo.web.self_diagnosis;


import HitecIsFuture.demo.domain.self_diagnosis.SelfDiagnosisService;
import HitecIsFuture.demo.web.member.Member;
import HitecIsFuture.demo.web.member.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/self_diagnosis")
public class SelfDiagnosisController {

    private final AllDiagnosisFormRepository allDiagnosisFormRepository;
    private final MemberRepository memberRepository;
    private final SelfDiagnosisService selfDiagnosisService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/write")
    public String createForm(@CookieValue(name = "memberId", required = false) Long memberId,
                             HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // <입력 JSON 형식>
        // ********************************************************************************************************
        // { "date" : 날짜(yyyy.MM.dd 형식), "fever" : 체온(double), "visitOverseas" : 해외 방문 여부(boolean),
        //  "haveSelfQuarantineFamily" : 자가격리 가족 유무(boolean), "contactConfirmedPerson" : 확진자 접촉 여부(boolean),
        //  "visitHighRiskFacilities" : 고위험 시설 방문 여부(boolean),  "cough": 기침 여부(boolean),
        //  "shortness_OF_BREATH" : 호흡곤란 여부(boolean), "chills" : 오한 여부(boolean), "arches_PAINS" : 근육통 여부(boolean),
        //  "headache" : 두통 여부(boolean), "sore_THROAT" : 인후통 여부(boolean), "loss_OF_TASTE" : 미각 상실 여부(boolean),
        //  "loss_OF_SMELL" : 후각 상실 여부(boolean) }
        // ********************************************************************************************************

        //  WARNING : date의 경우 yyyy.mm.dd 의 형식으로 넘겨받아야 한다.

        // 로그인이 되어있는 상태에서만 자가격리서를 작성할 수 있다.
        if (memberId == null) {
            // 로그인이 되어 있지 않다면
            return "EXCEPTION_NEED_SIGN_IN";

            // TODO
            // 이를 받아서 프론트에서 "로그인이 필요합니다" 출력 후 로그인 창으로 연결
            // 로그인 창으로 넘겨지고 로그인을 완료했을 때 다시 자가진단서 작성 페이지로 돌아오게 하는 로직 구성 필요
        }

        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isPresent()) {
            ServletInputStream inputStream = request.getInputStream();
            // 데이터를 받는 형식
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            // String으로 받아서 넘겨줘도 objectMapper가 알아서 JSON 처리를 해준다.


            // 입력받은 JSON으로 자가진단서 객체 생성
            SelfDiagnoseForm selfDiagnoseForm = objectMapper.readValue(messageBody, SelfDiagnoseForm.class);
            selfDiagnoseForm.setMember(member.get());

            // member로 헤당하는 자가진단서 repository 탐색
            Optional<SingleDiagnosisRepository> repository =
                    allDiagnosisFormRepository.findById(member.get().getId());

            if (repository.isPresent()) {
                // 성공 케이스
                selfDiagnosisService.create_new_diagnosis(member.get(), selfDiagnoseForm);

                // 현재 member의 자가진단 여부를 true로 갱신
                member.get().setSelf_diagnosis_notification(true);

                // *************************************************************************
                // 테스트 출력
                // member 이름으로 repository가 만들어졌는지, 그 안에 객체가 저장되었는지 테스트
                System.out.println(member.get().getName() + "가(이) 작성한 총 자가진단서 작성 횟수 : "
                        + allDiagnosisFormRepository.findById(member.get().getId()).get().getSelfDiagnoseForms().size());
                // 테스트 출력
                // *************************************************************************

                return "SELF_DIAGNOSIS_WRITE_COMPLETE";
            }
            else {
                // 해당 멤버의 자가진단서 repository를 찾을 수 없는 경우
                return "EXCEPTION_CANNOT_FIND_DIAGNOSIS_REPOSITORY";
            }
        }
        else {
            // 잘못된 쿠키인 경우
            return "EXCEPTION_CANNOT_FIND_MEMBER_FROM_COOKIE";
        }

    }
}



// TODO
// 2022.01.17
// h2를 이용한 비메모리 DB 저장 성공
// 미리 테이블을 만들어두는 등 조치를 취해 메모리 DB 저장 필요요